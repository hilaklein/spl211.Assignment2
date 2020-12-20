package bgu.spl.mics.application.services;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import  bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        //System.out.println( "leia start init");
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

//        try {
//            Thread.currentThread().sleep(500);
//        } catch (InterruptedException exception) {
//        }

        BlockingQueue<Future<Boolean>> futures = new LinkedBlockingQueue<>();
        for (Attack tempAt : attacks) {
            AttackEvent attackEvent = new AttackEvent(tempAt.getDuration(), tempAt.getSerials());
            futures.add(sendEvent(attackEvent));
        }
        System.out.println( "sendEvent happened");

        for (Future ftr : futures) {
            ftr.get();
        }
        System.out.println("futures resolved");

        DeactivationEvent deactivationEvent = new DeactivationEvent();
        Future<Boolean> deactFuture = sendEvent(deactivationEvent);
        deactFuture.get();
        System.out.println( "sendDeactivation happened");
        Future<Boolean> bombFuture = sendEvent(new BombDestroyerEvent());
        bombFuture.get();
        sendBroadcast(new TerminationBroadcast<>());
        System.out.println( "sendBroadcast happened");

        //System.out.println( "leia stop init");

    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLeiaTerminate(System.currentTimeMillis());
    }
}
