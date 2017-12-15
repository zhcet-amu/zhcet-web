package in.ac.amu.zhcet.configuration.security.ratelimit;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This class was a naive implementation of rate limiting which would
 * have failed for proxied networks which is very much the case with AMU,
 * thus, it has been disabled till a better implementation is thought of
 */
//@Configuration
public class RateLimitConfiguration extends WebMvcConfigurerAdapter {

    private final RateLimitInterceptor rateLimitInterceptor;

    //@Autowired
    public RateLimitConfiguration(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
    }

}