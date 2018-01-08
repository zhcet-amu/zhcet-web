package amu.zhcet.security.ratelimit;

import amu.zhcet.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * This class was a naive implementation of rate limiting which would
 * have failed for proxied networks which is very much the case with AMU,
 * thus, it has been disabled till a better implementation is thought of
 */
@Slf4j
//@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private static final int POST_LIMIT = 30;
    private static final int GET_LIMIT = 300;
    private static final String POST_METHOD = "POST";

    private final List<String> warningBuffer = new ArrayList<>();

    private final RateLimitService rateLimitService;
    private final TaskScheduler scheduler;
    private long lastWarning;
    private ScheduledFuture<?> scheduledFuture;

    //@Autowired
    public RateLimitInterceptor(RateLimitService rateLimitService, @Lazy TaskScheduler scheduler) {
        this.rateLimitService = rateLimitService;
        this.scheduler = scheduler;
    }

    private boolean isPost(HttpServletRequest request) {
        return request.getMethod().equals(POST_METHOD);
    }

    private int getLimit(HttpServletRequest request) {
        return isPost(request) ? POST_LIMIT : GET_LIMIT;
    }

    private String getKey(HttpServletRequest request) {
        return isPost(request) ? "POST~" : "GET~" + Utils.getClientIP(request);
    }

    private boolean isLimitExceeded(HttpServletRequest request, int requests) {
        return requests > getLimit(request);
    }

    private int getRemainingLimit(HttpServletRequest request, int requests) {
        return getLimit(request) - requests;
    }

    private void issueWarning(String ip, StringBuffer url) {
        long currentTime = System.currentTimeMillis();

        warningBuffer.add(String.format("%s accessed %s far too frequently", ip, url));

        if (scheduledFuture != null)
            scheduledFuture.cancel(false);

        if (currentTime - lastWarning > 5000) {
            log.warn("RATE LIMITER {}: {}", warningBuffer.size(), warningBuffer.toString());
            warningBuffer.clear();
        } else {
            Date now = new Date();
            scheduledFuture = scheduler.schedule(
                    () -> issueWarning(ip, url),
                    new Date(now.getTime() + 5000));
        }

        lastWarning = currentTime;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = getKey(request);

        int attempts = rateLimitService.incrementAttempts(key);
        if (isLimitExceeded(request, attempts)) {
            issueWarning(Utils.getClientIP(request), request.getRequestURL());
            response.setStatus(429);
            response.addHeader("Content-Type", "application/json");
            response.getOutputStream().print("{\"message\":\"Rate limit exceeded. Try again later\"}");
            return false;
        }

        response.addIntHeader("Remaining-Requests", getRemainingLimit(request, attempts));
        return true;
    }

}

