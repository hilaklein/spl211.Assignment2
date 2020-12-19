package bgu.spl.mics.application.services;


import bgu.spl.mics.*;
import bgu.spl.mics.application.Input;
import  bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;

import java.util.List;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize()  {
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

                Thread writeToDiary = new Thread(() -> {
                    Diary diary = Diary.getInstance();
                    diary.setHanSoloFinish(System.currentTimeMillis());
                    diary.incrementTotalAttacks();
                    complete(attackEvent, true);
                });
                writeToDiary.start();
                try{ writeToDiary.join();} catch (InterruptedException e) {}
            }
        };

        subscribeEvent(AttackEvent.class, callAttack);


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
    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setHanSoloTerminate(System.currentTimeMillis());
    }
}
