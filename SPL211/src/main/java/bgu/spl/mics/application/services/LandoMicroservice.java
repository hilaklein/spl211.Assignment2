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

        /* !!!!!!!!Lando can be the class that sends Broadcast message so every other microservices (threads) could terminate themselves!!!!!!!!! */
    }

    @Override
    protected void initialize() {
        //System.out.println( "lando start init");
        Callback<BombDestroyerEvent> bombEvent = new Callback<BombDestroyerEvent>() {
            @Override
            public void call(BombDestroyerEvent c) {
                //System.out.println("bomb event start");
                try {
                    Thread.currentThread().sleep(duration);
                } catch (InterruptedException e) { }
                complete(c, true);
                //System.out.println("termination sent");
            }
        };
        subscribeEvent(BombDestroyerEvent.class, bombEvent);

        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
//                Thread writeIt = new Thread(() -> {
                WriteToDiary();
                terminate();
//                });
//                writeIt.start();
//                try { writeIt.join();} catch (InterruptedException e) {}
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
        //System.out.println( "lando stop init");

    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
