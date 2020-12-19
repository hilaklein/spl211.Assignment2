package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Input;
import  bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;

import java.util.List;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
  //  private MessageBus messageBus;


    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        Callback<AttackEvent> callAttack = new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent attackEvent) {
                List<Integer> ewokList = attackEvent.getEwoksId();
                Ewok[] tempEw = Input.getInstance().getEwoks().getEwoksArr();
                for (Integer tempId : ewokList){
                    tempEw[tempId-1].acquire();
                }
                try {
                    Thread.currentThread().sleep(attackEvent.getDuration().longValue());
                }catch (InterruptedException e) {}
                for (Integer tempId : ewokList){
                    tempEw[tempId-1].release();
                }
                Diary diary = Diary.getInstance();
                diary.setC3POFinish(System.currentTimeMillis());
                diary.incrementTotalAttacks();
                complete(attackEvent, true);
            }
        };
        subscribeEvent(AttackEvent.class, callAttack);


        Callback<TerminationBroadcast> callTerminate = new Callback<TerminationBroadcast>() {
            @Override
            public void call(TerminationBroadcast c) {
                WriteToDiary();
                terminate();
            }
        };
        subscribeBroadcast(TerminationBroadcast.class, callTerminate);
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setC3POTerminate(System.currentTimeMillis());
    }
}
