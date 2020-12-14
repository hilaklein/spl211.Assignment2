package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
	private boolean result;
    private int duration;
    private List<Integer> ewokSerials;

    public Integer getDuration() {
        return duration;
    }

    public List<Integer> getEwoksId() {
        return ewoksId;
    }


    public void setResult(boolean result) {
        this.result = result;
    }

    public AttackEvent(int duration, List<Integer> ewoksId) {
        this.result = false;
        this.duration = duration;
        this.ewoksId = ewoksId;
    }
}
