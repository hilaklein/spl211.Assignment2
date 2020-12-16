package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
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
    private int eventID;
	private Attack[] attacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        eventID = 0;
    }

    @Override
    protected void initialize() {
    	//subscribeEvent();
        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
        try {
            Thread.currentThread().sleep(1000);
        }
        catch (InterruptedException exception){}
        List<Future<Boolean>> futures = new LinkedList<>();
        for (Attack tempAt : attacks){
            AttackEvent attackEvent = new AttackEvent(tempAt.getDuration(),tempAt.getSerials());
            attackEvent.setID(eventID);
            futures.add(sendEvent(attackEvent));
            eventID++;
        }
        while(!futures.isEmpty()){
            for(Future future : futures){
                if(future.isDone())
                    futures.remove(future);
            }
        }
        DeactivationEvent deactivationEvent = new DeactivationEvent();
    	sendEvent(deactivationEvent);
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLeiaTerminate(System.currentTimeMillis());
    }
}
