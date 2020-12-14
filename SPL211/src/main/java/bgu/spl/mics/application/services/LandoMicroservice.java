package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
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

        /* !!!!!!!!Lando can be the class that sends Broadcast message so every other microservices (threads) could terminate themselves!!!!!!!!! */
    }

    @Override
    protected void initialize() {

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
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
