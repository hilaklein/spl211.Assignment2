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

    //public int counter;

    public HanSoloMicroservice() {
        super("Han");
        //counter = 0;
    }


    @Override
    protected void initialize()  {
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

        //System.out.println( "han start init");
        Callback<AttackEvent> callAttack = new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent attackEvent) {
                //System.out.println("attack event");
                //counter++;
                //System.out.println(getName() + " started attack #: " + counter);
                List<Integer> ewokList = attackEvent.getEwoksId();
                Ewok[] tempEw = Input.getInstance().getEwoks().getEwoksArr();


//                Thread preperation = new Thread(() -> {
//                    boolean canStartAttack = false;
//                    while (!canStartAttack) {
//                        canStartAttack = true;
                        for (Integer tempId : ewokList) {
//                            if (tempEw[tempId - 1].isAvailable())
                                tempEw[tempId - 1].acquire();
//                            else canStartAttack = false;
                        }
//                    }
//                });
//                preperation.start();
//                try{ preperation.join();} catch (InterruptedException e) {}


                try {
                    Thread.currentThread().sleep(attackEvent.getDuration().longValue());
                }catch (InterruptedException e) {}


//                Thread releaseEwoks = new Thread(() -> {
                    for (Integer tempId : ewokList){
                        tempEw[tempId-1].release();
                    }
 //               });
//                releaseEwoks.start();
//                try{ releaseEwoks.join();} catch (InterruptedException e) {}


//                Thread writeToDiary = new Thread(() -> {
                    Diary diary = Diary.getInstance();
                    diary.setHanSoloFinish(System.currentTimeMillis());
                    diary.incrementTotalAttacks();
                    complete(attackEvent, true);
//                });
//                writeToDiary.start();
//                try{ writeToDiary.join();} catch (InterruptedException e) {}
                //System.out.println(getName() + " stopped attack #: " + counter);
            }
        };

        subscribeEvent(AttackEvent.class, callAttack);



        //System.out.println( "han stop init");

    }

    @Override
    protected void WriteToDiary() {
        Diary diary = Diary.getInstance();
        diary.setHanSoloTerminate(System.currentTimeMillis());
    }
}
