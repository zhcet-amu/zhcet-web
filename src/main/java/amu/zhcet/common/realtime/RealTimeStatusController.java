package amu.zhcet.common.realtime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@RestController
public class RealTimeStatusController {

    private static final long TIMEOUT = 1200000L; // 20 Minutes
    private final RealTimeStatusService realTimeStatusService;

    public RealTimeStatusController(RealTimeStatusService realTimeStatusService) {
        this.realTimeStatusService = realTimeStatusService;
    }

    @GetMapping("/management/task/status/{id}")
    public RealTimeStatus realTimeStatus(@PathVariable String id) {
        return realTimeStatusService.get(id);
    }

    @GetMapping("/management/task/sse/{id}")
    public SseEmitter realTimeSse(@PathVariable String id) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        RealTimeStatus status = realTimeStatusService.get(id);

        Consumer<RealTimeStatus> consumer = statusChange -> {
            try {
                emitter.send(statusChange);
            } catch (IOException e) {
                log.error("Error sending event", e);
                emitter.complete();
            }
        };

        Runnable completeListener = emitter::complete;

        Runnable onComplete = () -> {
            status.removeChangeListener(consumer);
            status.removeStopListener(completeListener);
        };

        status.addChangeListener(consumer);
        status.onStop(completeListener);

        emitter.onCompletion(onComplete);
        emitter.onTimeout(onComplete);
        consumer.accept(status);

        if (status.isInvalid() || status.isFailed() || status.isFinished())
            emitter.complete();

        return emitter;
    }

}
