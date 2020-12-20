package bgu.spl.mics;

import java.util.*;
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
	private Object queueMapLock;
//	private Object futureMapLock;



	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
		managerMapLock = new Object();
		queueMapLock = new Object();
//		futureMapLock = new Object();
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
//			try {
				managerMap.get(type).add(m);
//				BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
//				queueManager.put(m, msgToAdd);
				managerMapLock.notifyAll();
//			} catch (Exception e) {}
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
//		synchronized (futureMapLock) {
//			while (!futureMap.containsKey(e)){
//				try {
//					futureMapLock.wait();
//				} catch (InterruptedException exc) {}
//			}
			futureMap.get(e).resolve(result);
			futureMap.remove(e);
//		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(b.getClass())) {
				try {
//					System.out.println("wait in sendBroadcast() for subscribe to broadcast "+b.getClass().getName());
					managerMapLock.wait();
				} catch (InterruptedException interruptedException) {
				}
			}
			try {
				BlockingQueue<MicroService> tempQueue = managerMap.get(b.getClass());
				while (!tempQueue.isEmpty()) {
					queueManager.get(tempQueue.take()).add(b);
				}
			} catch (Exception exp) {}
//			Iterator<MicroService> microItr = managerMap.get(b.getClass()).iterator();
//			MicroService tempM = microItr.next();
//			MicroService tempM2 = tempM;
//			while (tempM2 != null){
//				if (microItr.hasNext())
//					tempM2 = microItr.next();
//				queueManager.get(tempM).add(b);
//				tempM = tempM2;
//			}

//			for (MicroService tempM : managerMap.get(b.getClass())) {
//				BlockingQueue<Message> tempQueue = queueManager.get(tempM);
//				tempQueue.add(b);
//				queueManager.get(tempM).add(b);
//			}
//			List<MicroService> tempList = new LinkedList<>(managerMap.get(b.getClass()));
//			for (MicroService microService : tempList) {
//				managerMap.get(b.getClass()).add(microService);
//			}
			managerMapLock.notifyAll();
		}
		synchronized (queueMapLock){
			queueMapLock.notifyAll();
		}

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(e.getClass())) {
				try {
//					System.out.println("wait in sendEvent() for subscribe to event "+e.getClass().getName());
					managerMapLock.wait();
				} catch (InterruptedException exception) {
				}
			}
			if (!managerMap.get(e.getClass()).isEmpty()) { //added cause tempM received null - maybe is not needed after the tests
				try {
					MicroService tempM = managerMap.get(e.getClass()).take(); //part 1 of round robin method
//					BlockingQueue<Message> tempQueue = queueManager.get(tempM);
					queueManager.get(tempM).add(e);
					managerMap.get(e.getClass()).add(tempM); //part 2 of round robin method
					managerMapLock.notifyAll();
				} catch (Exception exp) {
				}
			}
		}
		synchronized (queueMapLock){
			queueMapLock.notifyAll();
		}
//		synchronized (futureMapLock) {
			Future<T> future = new Future<>();
			futureMap.put(e, future);
//			futureMapLock.notifyAll();
			return future;
//		}
	}


	@Override
	public void register(MicroService m) {
//		System.out.println(m.getName() + " registered");
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
//			System.out.println(m.getName() + " unregistered");
		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (queueMapLock) {
			while (queueManager.get(m).isEmpty()) {
//				System.out.println(m.getName() + "is waiting in await()");
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
