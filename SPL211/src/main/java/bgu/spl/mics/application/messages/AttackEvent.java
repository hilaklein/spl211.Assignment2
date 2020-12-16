package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.cEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AttackEvent extends cEvent<Boolean> {
	private boolean result;
    private int duration;
    private List<Integer> ewokSerials;


    public Integer getDuration() {
        return duration;
    }

    public List<Integer> getEwoksId() {
        return ewokSerials;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public AttackEvent(int duration, List<Integer> ewoksId) {
        this.result = false;
        this.duration = duration;
        this.ewokSerials = ewoksId;
    }
}
