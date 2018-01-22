package amu.zhcet.common.realtime;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class RealTimeStatusTest {

    @Test
    public void testCompleteChangeListener() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();
        List<Integer> list = new ArrayList<>();
        realTimeStatus.addChangeListener(status -> list.add(status.getCompleted()));

        realTimeStatus.increment();
        realTimeStatus.increment();
        realTimeStatus.increment();

        assertEquals(3, list.size());
        assertEquals(1, (int) list.get(0));
        assertEquals(2, (int) list.get(1));
        assertEquals(3, (int) list.get(2));
    }

    @Test
    public void testFailureListener() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();

        AtomicReference<RealTimeStatus> reference = new AtomicReference<>();
        AtomicBoolean stopped = new AtomicBoolean(false);
        realTimeStatus.addChangeListener(reference::set);
        realTimeStatus.onStop(() -> stopped.set(true));

        realTimeStatus.setFailed(true);

        assertEquals(true, stopped.get());
        assertEquals(realTimeStatus, reference.get());
    }

    @Test
    public void testFinishListener() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();

        AtomicReference<RealTimeStatus> reference = new AtomicReference<>();
        AtomicBoolean stopped = new AtomicBoolean(false);
        realTimeStatus.addChangeListener(reference::set);
        realTimeStatus.onStop(() -> stopped.set(true));

        realTimeStatus.setFinished(true);

        assertEquals(true, stopped.get());
        assertEquals(realTimeStatus, reference.get());
    }

    @Test
    public void testRemoveChangeListener() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();
        List<Integer> list = new ArrayList<>();

        Consumer<RealTimeStatus> statusConsumer = status -> list.add(status.getCompleted());

        realTimeStatus.addChangeListener(statusConsumer);

        realTimeStatus.increment();
        realTimeStatus.increment();

        realTimeStatus.removeChangeListener(statusConsumer);

        realTimeStatus.increment();

        assertEquals(2, list.size());
        assertEquals(1, (int) list.get(0));
        assertEquals(2, (int) list.get(1));
    }

    @Test
    public void testStopChangeListener() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();

        AtomicBoolean atomicBoolean = new AtomicBoolean();
        AtomicBoolean atomicBooleanRemoved = new AtomicBoolean();

        Runnable runnable = () -> atomicBoolean.set(true);
        Runnable runnableToRemove = () -> atomicBooleanRemoved.set(true);
        realTimeStatus.onStop(runnable);
        realTimeStatus.onStop(runnableToRemove);

        realTimeStatus.removeStopListener(runnableToRemove);

        realTimeStatus.setFinished(true);

        assertEquals(true, atomicBoolean.get());
        assertEquals(false, atomicBooleanRemoved.get());
    }

    @Test
    public void testSingleFailure() {
        RealTimeStatus realTimeStatus = new RealTimeStatus();

        List<RealTimeStatus> list = new ArrayList<>();
        List<Integer> stopEvents = new ArrayList<>();
        realTimeStatus.addChangeListener(list::add);

        realTimeStatus.onStop(() -> stopEvents.add(0));

        realTimeStatus.increment();
        realTimeStatus.increment();
        realTimeStatus.increment();

        realTimeStatus.setFinished(true);

        realTimeStatus.increment();
        realTimeStatus.increment();
        realTimeStatus.increment();

        realTimeStatus.setFinished(true);

        assertEquals(4, list.size());
        assertEquals(1, stopEvents.size());
    }

}