package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        Callback<DeactivationEvent> deactEvent = new Callback<DeactivationEvent>() {
            @Override
            public void call(DeactivationEvent c) {
                try {
                    Thread.currentThread().sleep(duration);
                } catch (InterruptedException e) { }
                complete(c, true);
                Diary diary = Diary.getInstance();
                diary.setR2D2Deactivate(System.currentTimeMillis());
            }
        };
        subscribeEvent(DeactivationEvent.class, deactEvent);

        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setR2D2Terminate(System.currentTimeMillis());
    }
}
