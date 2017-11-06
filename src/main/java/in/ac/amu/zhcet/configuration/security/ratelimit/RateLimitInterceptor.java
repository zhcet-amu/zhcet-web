package in.ac.amu.zhcet.configuration.security.ratelimit;

import in.ac.amu.zhcet.service.security.ratelimit.RateLimitService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private static final int POST_LIMIT = 15;
    private static final int GET_LIMIT = 150;
    private static final String POST_METHOD = "POST";

    private final RateLimitService rateLimitService;

    @Autowired
    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    private String getKey(HttpServletRequest request) {
        return request.getMethod().equals(POST_METHOD) ? "POST~" : "GET~" + Utils.getClientIP(request);
    }

    private boolean isLimitExceeded(HttpServletRequest request, String key) {
        int requests = rateLimitService.incrementAttempts(key);

        return requests > (request.getMethod().equals(POST_METHOD) ? POST_LIMIT : GET_LIMIT);
    }

    private int getRemainingLimit(HttpServletRequest request, String key) {
        int requests = rateLimitService.getAttempts(key);

        return (request.getMethod().equals(POST_METHOD) ? POST_LIMIT : GET_LIMIT) - requests;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = getKey(request);

        if (isLimitExceeded(request, key)) {
            log.warn("{} accessed {} far too frequently", Utils.getClientIP(request), request.getRequestURL());
            response.sendError(429, "Rate limit exceeded, wait for the next minute");
            return false;
        }

        response.addIntHeader("Remaining-Requests", getRemainingLimit(request, key));
        return true;
    }

}

