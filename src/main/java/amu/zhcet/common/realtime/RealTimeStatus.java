package amu.zhcet.common.realtime;

import lombok.Data;

@Data
public class RealTimeStatus {
    private String id;
    private String context;
    private String message;
    private int total;
    private int completed;
    private float duration;
    private boolean finished;
    private boolean failed;
    private boolean invalid;
    private transient String meta;

    public void increment() {
        completed++;
    }

}
