package amu.zhcet.auth.login;

import amu.zhcet.auth.login.handler.UsernameAuthenticationFailureHandler;
import amu.zhcet.auth.login.listener.PathAuthorizationAuditListener;
import amu.zhcet.common.utils.Utils;
import amu.zhcet.data.config.ConfigurationService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.security.AuthenticationAuditListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LoginAttemptService {

    private static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

    private final Cache<String, Integer> attemptsCache;
    private final ConfigurationService configurationService;

    @Autowired
    public LoginAttemptService(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        RemovalListener<String, Integer> removalListener = (key, value, cause) ->
                log.debug("Login Key {} with value {} was removed because : {}",
                key, value, cause);

        attemptsCache = Caffeine
                .newBuilder()
                .maximumSize(10000)
                .removalListener(removalListener)
                .expireAfterWrite(getBlockDuration(), TIME_UNIT)
                .build(key -> 0);
    }

    private int getMaxRetries() {
        return configurationService.getConfigCache().getMaxRetries();
    }

    private int getBlockDuration() {
        return configurationService.getConfigCache().getBlockDuration();
    }

    public String getErrorMessage(HttpServletRequest request) {
        String defaultMessage = "Username or Password is incorrect!";

        Object exception = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        Object rawUsername = request.getSession().getAttribute(UsernameAuthenticationFailureHandler.USERNAME);
        if (exception == null)
            return defaultMessage;

        String ip = Utils.getClientIP(request);
        String coolDownPeriod = getBlockDuration() + " " + LoginAttemptService.TIME_UNIT;
        if(exception instanceof LockedException || isBlocked(ip)) {
            return "IP blocked for <strong>" + coolDownPeriod + "</strong> since last wrong login attempt";
        } else if (exception instanceof BadCredentialsException && rawUsername instanceof String) {
            String username = (String) rawUsername;
            String key = getKey(ip, username);
            String tries = String.format("%d out of %d tries left!" , triesLeft(key), getMaxRetries());
            String message = "IP will be blocked for " + coolDownPeriod + " after all tries are exhausted";
            return defaultMessage + "<br><strong>" + tries  + "</strong> " + message;
        } else if (exception instanceof DisabledException) {
            return "User is disabled from site";
        }

        return defaultMessage;
    }

    public void loginAttempt(AuditEvent auditEvent, WebAuthenticationDetails details) {
        String requestUri = (String) auditEvent.getData().get("requestUrl");
        if (requestUri != null) {
            log.debug("Ignoring Access Denied Authentication Failure for URL : {}", requestUri);
            return;
        }

        log.info("Login Attempt for Principal : {}", auditEvent.getPrincipal());
        if (auditEvent.getType().equals(AuthenticationAuditListener.AUTHENTICATION_FAILURE)) {
            Object type = auditEvent.getData().get("type");
            if (type != null && type.toString().equals(BadCredentialsException.class.getName())) {
                log.debug("Login Failed. Incrementing Attempts");
                loginFailed(details.getRemoteAddress(), auditEvent.getPrincipal());
            } else if(type != null) {
                log.debug("Login Failed due to {}", type.toString());
            }
        } else if (auditEvent.getType().equals(PathAuthorizationAuditListener.SUCCESS)) {
            log.debug("Login Succeeded. Invalidating");
            loginSucceeded(details.getRemoteAddress(), auditEvent.getPrincipal());
        }
    }

    public static String getKey(String ip, String username) {
        return ip + "~" + username;
    }

    private int getFromCache(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null ? attempts : 0;
    }

    private void loginSucceeded(String ip, String username) {
        String key = getKey(ip, username);
        attemptsCache.invalidate(key);
    }

    private void loginFailed(String ip, String username) {
        String key = getKey(ip, username);
        if (isBlocked(key)) {
            log.debug("IP {} is already blocked, even correct attempts will be marked wrong, hence ignoring", key);
            return;
        }

        int attempts = getFromCache(key);
        attemptsCache.put(key, ++attempts);
        log.info("Attempts : {}, Max Attempts : {}", attempts, getMaxRetries());
    }

    public boolean isBlocked(String key) {
        return getFromCache(key) >= getMaxRetries();
    }

    private int triesLeft(String key) {
        return getMaxRetries() - getFromCache(key);
    }

    @Data
    @AllArgsConstructor
    public static class BlockedIp {
        private String ip;
        private int attempts;
        private boolean blocked;
    }

    public List<BlockedIp> getBlockedIps() {
        return attemptsCache.asMap()
                .entrySet()
                .stream()
                .map(entry -> new BlockedIp(entry.getKey(), entry.getValue(), isBlocked(entry.getKey())))
                .collect(Collectors.toList());
    }
}