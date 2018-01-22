package amu.zhcet.common.realtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
@Data
@ToString(exclude = {"listeners", "stopListeners"})
public class RealTimeStatus {
    private String id;
    private String context;
    private String message;
    private int total;
    private int completed;
    private float duration;
    private boolean started;
    private boolean finished;
    private boolean failed;
    private boolean invalid;

    @JsonIgnore
    private final List<Consumer<RealTimeStatus>> listeners = new CopyOnWriteArrayList<>();
    @JsonIgnore
    private final List<Runnable> stopListeners = new CopyOnWriteArrayList<>();

    public void start() {
        this.started = true;
        notifyChange();
    }

    public void increment() {
        completed++;
        notifyChange();
    }

    public void setFailed(boolean failed) {
        this.failed = failed;

        if (failed) {
            notifyChange();
            stop();
        }
    }

    public void setFinished(boolean finished) {
        this.finished = finished;

        if (finished) {
            notifyChange();
            stop();
        }
    }

    public void addChangeListener(Consumer<RealTimeStatus> listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(Consumer<RealTimeStatus> listener) {
        listeners.remove(listener);
    }

    public void removeStopListener(Runnable listener) {
        stopListeners.remove(listener);
    }

    public void onStop(Runnable runnable) {
        stopListeners.add(runnable);
    }

    private void notifyChange() {
        for (Consumer<RealTimeStatus> listener : listeners)
            listener.accept(this);
    }

    private void stop() {
        for (Runnable runnable : stopListeners)
            runnable.run();

        stopListeners.clear();
        listeners.clear();
    }
}
