package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import  bgu.spl.mics.application.messages.AttackEvent;

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

    }

    @Override
    protected void initialize() {
    	//subscribeEvent();

//    	AttackEvent atEv = new AttackEvent();
//    	synchronized (eventLock) {
//            while (sendEvent(atEv) == null) {
//                try { this.wait(); }
//                catch (InterruptedException e) {}
//            }
//        }


    	// List<Future<T>> futures
        // foreach (sends atack events){
        //        cuurFuture = new Future
        //        while (currFuture == null) { try: currrFuture = sendEvent(attackEv1), catch  }
        //        futures.add(currFuture)
        //}

        // waiting for Futures to be done
        // sends deactivationEvent
        // waits for it to be done
        //sends bombEvent
        //waits for it to be done
        //terminate everyone by terminationEvent
    }
}
