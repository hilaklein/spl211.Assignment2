package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                WriteToDiary();
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
        TerminationBroadcast.terminateCountDown.countDown();

        Callback<BombDestroyerEvent> bombEvent = new Callback<BombDestroyerEvent>() {
            @Override
            public void call(BombDestroyerEvent c) {
                try {
                    Thread.currentThread().sleep(duration);
                } catch (InterruptedException e) { }
                complete(c, true);
            }
        };
        subscribeEvent(BombDestroyerEvent.class, bombEvent);
        BombDestroyerEvent.countSubscribed.countDown();
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
