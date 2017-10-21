package in.ac.amu.zhcet.service.user.auth;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import in.ac.amu.zhcet.configuration.security.ExposeAttemptedPathAuthorizationAuditListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    public static final int BLOCK_DURATION = 6;
    public static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

    private final LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        RemovalListener<String, Integer> removalListener = removal ->
                log.info("Key {} with value {} was removed because : {}",
                removal.getKey(), removal.getValue(), removal.getCause());

        attemptsCache = CacheBuilder
                .newBuilder()
                .maximumSize(10000)
                .removalListener(removalListener)
                .expireAfterWrite(BLOCK_DURATION, TIME_UNIT)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(@Nonnull String key) throws Exception {
                        return 0;
                    }
                });
    }

    public static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public String getErrorMessage(HttpServletRequest request) {
        String defaultMessage = "Username or Password is incorrect!";

        Object object = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (object == null)
            return defaultMessage;

        log.info(object.toString());
        String ip = LoginAttemptService.getClientIP(request);
        String coolDownPeriod = LoginAttemptService.BLOCK_DURATION + " " + LoginAttemptService.TIME_UNIT;
        if(object instanceof LockedException || isBlocked(ip)) {
            return "IP blocked for <strong>" + coolDownPeriod + "</strong> since last wrong login attempt";
        } else if (object instanceof BadCredentialsException) {
            String tries = String.format("%d out of %d tries left!" , triesLeft(ip), LoginAttemptService.MAX_ATTEMPTS);
            String message = "IP will be blocked for " + coolDownPeriod + " after all tries are exhausted";
            return defaultMessage + "<br><strong>" + tries  + "</strong> " + message;
        } else if (object instanceof DisabledException) {
            return "User is disabled from site";
        }

        return defaultMessage;
    }

    public void loginAttempt(AuditEvent auditEvent, WebAuthenticationDetails details) {
        String requestUri = (String) auditEvent.getData().get("requestUrl");
        if (requestUri != null) {
            log.info("Ignoring Access Denied Authentication Failure for URL : {}", requestUri);
            return;
        }

        log.info("Login Attempt for Principal : {}", auditEvent.getPrincipal());;
        if (auditEvent.getType().equals(ExposeAttemptedPathAuthorizationAuditListener.FAILURE)) {
            Object type = auditEvent.getData().get("type");
            if (type instanceof BadCredentialsException) {
                log.info("Login Failed. Incrementing Attempts");
                loginFailed(details.getRemoteAddress());
            } else if(type != null) {
                log.info("Login Failed due to {}", type.toString());
            }
        } else if (auditEvent.getType().equals(ExposeAttemptedPathAuthorizationAuditListener.SUCCESS)) {
            log.info("Login Succeeded. Invalidating");
            loginSucceeded(details.getRemoteAddress());
        }
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        if (isBlocked(key)) {
            log.info("IP {} is already blocked, even correct attempts will be marked wrong, hence ignoring", key);
            return;
        }

        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
        log.info("Attempts : {}, Max Attempts : {}", attempts, MAX_ATTEMPTS);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            return false;
        }
    }

    private int triesLeft(String key) {
        try {
            return MAX_ATTEMPTS - attemptsCache.get(key);
        } catch (ExecutionException e) {
            return MAX_ATTEMPTS;
        }
    }

    @Data
    @AllArgsConstructor
    public static class BlockedIp {
        private String ip;
        private int attempts;
    }

    public List<BlockedIp> getBlockedIps() {
        return attemptsCache.asMap()
                .entrySet()
                .stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() >= MAX_ATTEMPTS)
                .map(entry -> new BlockedIp(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}