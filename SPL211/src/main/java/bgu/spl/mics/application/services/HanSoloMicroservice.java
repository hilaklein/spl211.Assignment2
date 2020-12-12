package bgu.spl.mics.application.services;


import bgu.spl.mics.*;
import  bgu.spl.mics.application.messages.AttackEvent;


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
    protected void initialize() {
        Callback callback = new Callback() {
            @Override
            public void call(Object c) {
                Thread.sleep();
                //Future handling can be done here - office hours remark
            }
        }

        subscribeEvent(AttackEvent.class, );
    }

}
