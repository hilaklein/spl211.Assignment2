package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    //private MessageBus messageBus;

    public LandoMicroservice(long duration) {
        super("Lando");
        //messageBus = new MessageBusImpl();
        //messageBus.register(this);
    }

    @Override
    protected void initialize() {
       
    }
}
