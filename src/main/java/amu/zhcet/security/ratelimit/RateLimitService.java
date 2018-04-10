package amu.zhcet.security.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Slf4j
/* Decommissioned */
public class RateLimitService {

    private static final int EXPIRE_TIME = 30;
    private static final TemporalUnit TIME_UNIT = ChronoUnit.SECONDS;

    private final Cache<String, Integer> attemptsCache;

    public RateLimitService() {
        attemptsCache = Caffeine
                .newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.of(EXPIRE_TIME, TIME_UNIT))
                .build();
    }

    public int incrementAttempts(String key) {
        int attempts = getAttempts(key);
        attemptsCache.put(key, ++attempts);

        return attempts;
    }

    private int getAttempts(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null ? attempts : 0;
    }
}

