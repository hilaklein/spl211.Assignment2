package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.LeiaMicroservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//singleton constructor has to be added here
	private static class SingletonHOlder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	//private Map<Message, Map<MicroService, BlockingQueue<Message>>> managerMap;
	private Map<Message, BlockingQueue<MicroService>> managerMap;
	private Map<MicroService, BlockingQueue<Message>> queueManager;
	private Map<Integer,Future> futureMap;
	private  Object managerMapLock;
	private  Object queueManagerLock;
	//private  Object managerMapLock;

	//blocking queue for every Microservice
	// keyToSendEvent which is managed by subscribe event
	//

	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHOlder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!managerMap.containsKey(type)) {
			LinkedBlockingQueue<MicroService>()
			managerMap.put(type,
		}
		managerMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		managerMap.get(type).add(m);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService tempM : managerMap.get(b)) {
			queueManager.get(tempM).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

			try {
				MicroService tempM = managerMap.get(e).take();
				queueManager.get(tempM).add(e);
				managerMap.get(e).add(tempM);
				return null; // return future object that is connected to e specified event
			} catch (InterruptedException exc) {
				return null;
			}
		}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> tempBQ = new LinkedBlockingQueue<>();
		queueManager.put(m, tempBQ);
	}

	@Override
	public void unregister(MicroService m) {
		queueManager.remove(m);
		for (Message msg : managerMap.keySet()) {
			managerMap.get(msg).remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			return queueManager.get(m).take();
		} catch (InterruptedException exp) {
			return null;
		}
	}
}
