package amu.zhcet.common.realtime;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class RealTimeStatusService {

    private final RealTimeStatus invalidStatus;
    private final Cache<String, RealTimeStatus> realTimeStatusCache;

    public RealTimeStatusService() {
        this.invalidStatus = new RealTimeStatus();
        invalidStatus.setInvalid(true);

        this.realTimeStatusCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.of(20, ChronoUnit.MINUTES))
                .maximumSize(100)
                .build();
    }

    public RealTimeStatus install() {
        String uuid = UUID.randomUUID().toString();

        RealTimeStatus realTimeStatus = new RealTimeStatus();
        realTimeStatus.setId(uuid);
        realTimeStatusCache.put(uuid, realTimeStatus);

        return realTimeStatus;
    }

    public RealTimeStatus get(String id) {
        return realTimeStatusCache.get(id, uuid -> invalidStatus);
    }

}
