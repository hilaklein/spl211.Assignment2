package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
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
    	//subscribeEvent();
        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);

    	// List<Future<T>> futures
        // foreach (sends atack events){
        //        cuurFuture = new Future
        //        while (currFuture == null) { try: currrFuture = sendEvent(attackEv1), catch  }
        //        futures.add(currFuture)
        //}

        List<Future<Boolean>> futures = new LinkedList<>();
        for (Attack tempAt : attacks){

        }

        // waiting for Futures to be done
        // sends deactivationEvent
        // waits for it to be done
        //sends bombEvent
        //waits for it to be done
        //terminate everyone by terminationEvent

    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setLeiaTerminate(System.currentTimeMillis());
    }
}
