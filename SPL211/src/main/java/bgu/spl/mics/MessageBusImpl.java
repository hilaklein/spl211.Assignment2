package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	private Map<Class<? extends Message>, BlockingQueue<MicroService>> managerMap;
	private Map<MicroService, BlockingQueue<Message>> queueManager;
	private Map<Message,Future> futureMap;
	private  Object managerMapLock;
	private  Object queueManagerLock;
	private  Object futureMapLock;

	//blocking queue for every Microservice
	// keyToSendEvent which is managed by subscribe event
	//

	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
		futureMapLock = new Object();
		queueManagerLock = new Object();
		managerMapLock = new Object();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHOlder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (managerMapLock) {
			if (!managerMap.containsKey(type)) {
				BlockingQueue<MicroService> toAdd = new LinkedBlockingQueue<>();
				managerMap.put(type, toAdd);
			}
			managerMap.get(type).add(m);
			BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
			queueManager.put(m, msgToAdd);
			notifyAll();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (managerMapLock) {
			if (!managerMap.containsKey(type)) {
				BlockingQueue<MicroService> toAdd = new LinkedBlockingQueue<>();
				managerMap.put(type, toAdd);
			}
			managerMap.get(type).add(m);
			BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
			queueManager.put(m, msgToAdd);
			notifyAll();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
		futureMap.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(b)) {
				try {
					Thread.currentThread().wait();
				} catch (InterruptedException exc) {
				}
			}

			for (MicroService tempM : managerMap.get(b)) {
				queueManager.get(tempM).add(b);
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(e)) {
				try {
					Thread.currentThread().wait();
				} catch (InterruptedException exception) {
				}

				try {
					MicroService tempM = managerMap.get(e).take(); //part 1 of round robin method
					queueManager.get(tempM).add(e);
					managerMap.get(e).add(tempM); //part 2 of round robin method
					Future<T> future = new Future<>();
					futureMap.put(e, future);
					return future; // return future object that is connected to e specified event
				} catch (Exception exc) { // changed instead of InterruptedException
					return null;
				}
			}
		}
		return null;
	}


	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> tempBQ = new LinkedBlockingQueue<>();
		queueManager.put(m, tempBQ);
	}

	@Override
	public void unregister(MicroService m) {
		queueManager.remove(m);
		for (Class<? extends Message> msg : managerMap.keySet()) {
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
