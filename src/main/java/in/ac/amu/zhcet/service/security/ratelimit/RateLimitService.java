package in.ac.amu.zhcet.service.security.ratelimit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RateLimitService {

    private static final int EXPIRE_TIME = 30;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final LoadingCache<String, Integer> attemptsCache;

    public RateLimitService() {
        attemptsCache = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(EXPIRE_TIME, TIME_UNIT)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(@Nonnull String key) throws Exception {
                        return 0;
                    }
                });

    }

    public int incrementAttempts(String key) {
        int attempts = getAttempts(key);
        attempts++;
        attemptsCache.put(key, attempts);

        return attempts;
    }

    public int getAttempts(String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }

        return attempts;
    }
}

