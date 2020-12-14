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
        //we need to define here what kind of the messages we want to receive and how the callback will respond to different kind of messages
        //so, eventually we need to:
        // define here different scenarios of callbacks (regarding different types of messages a.k.a events/broadcasts)
        // send those callback classes with the relevant type of event to super.subscribeEvent/Broadcast(Class type, Callback c)
        // then we call the super.run() function, so the cycle of "PullingMessagesFromQueueAndResolveThemWithRelevantCallbacks" will begin


        // Callback c1 {anonym class }
        //subscribeEvent (attackEv.getCLass, c1)
        // Callback c2 {anonym class }
        //subscribeTermination (terminationEv.getclass, c2);

    }
}
