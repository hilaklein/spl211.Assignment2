package bgu.spl.mics;

import java.util.HashMap;
import java.util.Iterator;
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

	private Map<Class<? extends Message>, BlockingQueue<MicroService>> managerMap;
	private Map<MicroService, BlockingQueue<Message>> queueManager;
	private Map<Message, Future> futureMap;
	private Object managerMapLock;


	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
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
				//managerMapLock.notifyAll();
			}
			managerMap.get(type).add(m);
			managerMapLock.notifyAll();
		}
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (managerMapLock) {
			if (!managerMap.containsKey(type)) {
				BlockingQueue<MicroService> toAdd = new LinkedBlockingQueue<>();
				managerMap.put(type, toAdd);
				//managerMapLock.notifyAll();
			}
			try {
				managerMap.get(type).add(m);
				BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
				queueManager.put(m, msgToAdd);
				managerMapLock.notifyAll();
			} catch (Exception e) {}
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		synchronized (managerMapLock) {
			futureMap.get(e).resolve(result);
			futureMap.remove(e);
			managerMapLock.notifyAll();
		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(b.getClass())) {
				try {
					managerMapLock.wait();
				} catch (InterruptedException interruptedException) {
				}
			}
		}
		for (MicroService tempM : managerMap.get(b.getClass())) {
			queueManager.get(tempM).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(e.getClass())) {
				try {
					managerMapLock.wait();
				} catch (InterruptedException exception) {
				}
			}
		}
		if (!managerMap.get(e.getClass()).isEmpty()) { //added cause tempM received null - maybe is not needed after the tests
			try {
				MicroService tempM = managerMap.get(e.getClass()).take(); //part 1 of round robin method
				BlockingQueue<Message> tempQueue = queueManager.get(tempM);
				tempQueue.add(e);
				managerMap.get(e.getClass()).add(tempM); //part 2 of round robin method
			} catch (Exception exp) {
			}
		}

		synchronized (managerMapLock) {
			managerMapLock.notifyAll();
		}

		Future<T> future = new Future<>();
		futureMap.put(e, future);
		return future;
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
		} catch (Exception exp) {
			return null;
		}
	}

}
