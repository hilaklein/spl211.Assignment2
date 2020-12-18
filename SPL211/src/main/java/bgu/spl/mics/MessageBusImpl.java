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

	private Map<Class<? extends Message>, BlockingQueue<MicroService>> managerMap;
	private Map<MicroService, BlockingQueue<Message>> queueManager;
	private Map<Message,Future> futureMap;
	private  Object managerMapLock;


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
		//synchronized (managerMapLock) {
			if (!managerMap.containsKey(type)) {
				BlockingQueue<MicroService> toAdd = new LinkedBlockingQueue<>();
				managerMap.put(type, toAdd);
			}
			managerMap.get(type).add(m);
			BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
			queueManager.put(m, msgToAdd);
			managerMapLock.notifyAll();
		}
	//}

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
			managerMapLock.notifyAll();
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
//		synchronized (managerMapLock) {
//			while (!managerMap.containsKey(b.getClass())) {
//				try {
//					wait();
//				} catch (InterruptedException exc) {
//					System.out.println(	exc.getMessage());
//				}
//			}

		synchronized (managerMapLock) {
			for (MicroService tempM : managerMap.get(b.getClass())) {
				queueManager.get(tempM).add(b);
				managerMapLock.notifyAll();
			}
		}
	}
//	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
//		synchronized (managerMapLock) {
//			while (!managerMap.containsKey(e.getClass())) {
//				try {
//					wait();
//				} catch (InterruptedException exception) {
//					System.out.println(	exception.getMessage());
//				}
//			}

			try {
				MicroService tempM = managerMap.get(e.getClass()).take(); //part 1 of round robin method
				synchronized (managerMapLock) {
					queueManager.get(tempM).add(e);
					managerMap.get(e.getClass()).add(tempM); //part 2 of round robin method
					managerMapLock.notifyAll();
				}


				Future<T> future = new Future<>();
				futureMap.put(e, future);
				return future; // return future object that is connected to e specified event
			} catch (Exception exc) { // changed instead of InterruptedException
				System.out.println(exc.getMessage());
				return null;
			}

		}

	//}


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
		System.out.println("awaitMessage() called in msgBus");
		synchronized (managerMapLock) {
			while (queueManager.get(m).isEmpty()){
				System.out.println(	"msgBus: await: while: " + Thread.currentThread().getName());
				Thread.currentThread().wait();
			}
			try {
				return queueManager.get(m).take();
			} catch (Exception exp) {
				System.out.println(exp.getMessage());
				return null;
			}
		}
	}
}
