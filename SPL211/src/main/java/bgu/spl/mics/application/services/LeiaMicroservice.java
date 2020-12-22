package bgu.spl.mics.application.services;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
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
        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                WriteToDiary();
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
        TerminationBroadcast.terminateCountDown.countDown();

//        try {
//            Thread.currentThread().sleep(250);
//        } catch (InterruptedException exception) {
//        }

        Future f;
        try {
            //System.out.println(this.getName() + "waits for hansolo and c3po to subscribe");
            //System.out.println(AttackEvent.countSubscribed.getCount());
            AttackEvent.countSubscribed.await();
        }catch (InterruptedException ex){}

        BlockingQueue<Future<Boolean>> futures = new LinkedBlockingQueue<>();
        for (Attack tempAt : attacks) {
            AttackEvent attackEvent = new AttackEvent(tempAt.getDuration(), tempAt.getSerials());
            f=sendEvent(attackEvent);
            if(f!=null)
            futures.add(f);
        }
        for (Future ftr : futures) {
            ftr.get();
        }
        try {
            //System.out.println(this.getName() + "waits for r2d2 to subscribe");
            DeactivationEvent.countSubscribed.await();
            //System.out.println("r2d2 subscribed deact");
        }catch (InterruptedException ex){}
        DeactivationEvent deactivationEvent = new DeactivationEvent();
        Future<Boolean> deactFuture = sendEvent(deactivationEvent);
        if(deactFuture!=null)
        deactFuture.get();
        try {
            //System.out.println(this.getName() + "waits for lando to subscribe");
            BombDestroyerEvent.countSubscribed.await();
        }catch (InterruptedException ex){}
        Future<Boolean> bombFuture = sendEvent(new BombDestroyerEvent());
        if(bombFuture!=null)
        bombFuture.get();
        try {
            //System.out.println(this.getName() + "waits for everyone to subscribe brod");
            TerminationBroadcast.terminateCountDown.await();

        }catch (InterruptedException ex){}
        //System.out.println(this.getName() + "EVERYONE SUBSCRIBED BROD");
        sendBroadcast(new TerminationBroadcast<>());
        //System.out.println("leia sent brod");
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLeiaTerminate(System.currentTimeMillis());
    }
}
