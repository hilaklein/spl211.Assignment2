package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.LeiaMicroservice;

import java.util.LinkedList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	//singleton constructor has to be added here
	private static MessageBusImpl instance;

	//blocking queue for every Microservice
	// keyToSendEvent which is managed by subscribe event
	//

	private MessageBusImpl(){}

	public static MessageBusImpl getInstance(){
		if (MessageBusImpl.instance == null) //maybe we need to synchronize here so the method will not stop in the middle of the creation!!!!!!!!!!!!!!!!
			MessageBusImpl.instance = new MessageBusImpl();
		return MessageBusImpl.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//create a blocking queue for the Microservice which has sent this Event
		//add this Microservice to HashMap<someEventClass, microservice>
		//release the blocking of the sendEvent for this type of Event
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//while (!subscribed) return null
        return null;
	}

	@Override
	public void register(MicroService m) {
		
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}
}
