package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
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
	private Object queueMapLock;
	private Object futureMapLock;



	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
		managerMapLock = new Object();
		queueMapLock = new Object();
		futureMapLock = new Object();
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
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		synchronized (futureMapLock) {
			while (!futureMap.containsKey(e)) {
				try {
					futureMapLock.wait();
				} catch (InterruptedException ex) {
				}
			}
			futureMap.get(e).resolve(result);
			futureMap.remove(e);
		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		if (managerMap.containsKey(b.getClass())) {
			MicroService m = null;
			BlockingQueue<MicroService> tempQueue;
			int count;

			synchronized (managerMapLock) {
				tempQueue = new LinkedBlockingQueue<>(managerMap.get(b.getClass()));
				count = tempQueue.size();
			}
			for (int i = 0; i < count; i++) {
				try {
					m = tempQueue.take();
					tempQueue.add(m);
				} catch (InterruptedException ex) {
				}
				synchronized (queueMapLock) {
					if (m == null) {
						System.out.println("m is null");
					}
					if (m != null) {
						queueManager.get(m).add(b);
						queueMapLock.notifyAll();
					}
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (managerMapLock) {
			if (!managerMap.containsKey(e.getClass())) {
				return null;
			}
		}
		Future<T> future = new Future<>();
		MicroService tempM = null;
		synchronized (managerMapLock) {
			try {
				tempM = managerMap.get(e.getClass()).take(); //part 1 of round robin method
				managerMap.get(e.getClass()).add(tempM); //part 2 of round robin method
			} catch (InterruptedException ex) {
			}
		}
		synchronized (queueMapLock) {
			if (tempM != null) {
				queueManager.get(tempM).add(e);
				queueMapLock.notifyAll();
			}
		}
		synchronized (futureMapLock) {
			futureMap.put(e, future);
			futureMapLock.notifyAll();
		}
		return future;
	}


	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> tempBQ = new LinkedBlockingQueue<>();
		synchronized (queueMapLock) {
			queueManager.put(m, tempBQ);
			queueMapLock.notifyAll();
		}
	}


	@Override
	public void unregister(MicroService m) {
		synchronized (managerMapLock) {
			for (Class<? extends Message> msg : managerMap.keySet()) {
				managerMap.get(msg).remove(m);
			}
		}
		synchronized (queueMapLock) {
			queueManager.remove(m);
		}
		if(queueManager.isEmpty()){
			managerMap.clear();
			queueManager.clear();
			futureMap.clear();
		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (queueMapLock) {
			while (queueManager.get(m).isEmpty()) {
				queueMapLock.wait();
			}
			try {
				return queueManager.get(m).take();
			} catch (Exception exp) {
				return null;
			}
		}
	}
}
