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
	private Map<Message,Future> futureMap;
	private  Object managerMapLock;
	private Object queueEventLock;


	private MessageBusImpl() {
		managerMap = new HashMap<>();
		queueManager = new HashMap<>();
		futureMap = new HashMap<>();
		managerMapLock = new Object();
		queueEventLock = new Object();
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
		//synchronized (queueEventLock) {
			//BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
			//queueManager.put(m, msgToAdd);
			//queueEventLock.notifyAll();
		//}
	}
	//}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (managerMapLock) {
			if (!managerMap.containsKey(type)) {
				BlockingQueue<MicroService> toAdd = new LinkedBlockingQueue<>();
				managerMap.put(type, toAdd);
				//managerMapLock.notifyAll();
			}
			managerMap.get(type).add(m);
			managerMapLock.notifyAll();
		}
		//synchronized (queueEventLock) {
			BlockingQueue<Message> msgToAdd = new LinkedBlockingQueue<>();
			queueManager.put(m, msgToAdd);
			//queueEventLock.notifyAll();
		//}
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
			while (!managerMap.containsKey(b.getClass())) {
				try {
					managerMapLock.wait();
				} catch (InterruptedException interruptedException) {
				}
			}
		}
		//synchronized (queueEventLock) {
		Iterator<MicroService> iter = managerMap.get(b.getClass()).iterator(); //itr instead of the foreach commentedOut loop (relevant lines are marked with =====================>>>)
		MicroService tempMicro = iter.next();
		while (iter.hasNext() && queueManager.containsKey(tempMicro)) {
			queueManager.get(tempMicro).add(b);
			tempMicro = iter.next();
		}

		//=====================>>>for (MicroService tempM : managerMap.get(b.getClass())) {
				//while (!queueManager.containsKey(tempM)) {
					//try {
					//	queueEventLock.wait();
					//} catch (InterruptedException interruptedException) {
					//}
				//}
				//=====================>>>queueManager.get(tempM).add(b);
				//queueEventLock.notifyAll();
			//}
			//queueEventLock.notifyAll();
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (managerMapLock) {
			while (!managerMap.containsKey(e.getClass())) {
				try {
					managerMapLock.wait();
				} catch (InterruptedException exception) {
					//System.out.println("send event: " + exception.getMessage());
				}
			}
			try {
				if (!managerMap.get(e.getClass()).isEmpty()) { //added cause tempM received null - maybe is not needed after the tests
					MicroService tempM = managerMap.get(e.getClass()).take();
					queueManager.get(tempM).add(e);
					//queueEventLock.notifyAll();
					managerMap.get(e.getClass()).add(tempM);
				}
				managerMapLock.notifyAll();
			} catch (Exception ex) {
			}
		}
		Future<T> future = new Future<>();
		futureMap.put(e, future);
		return future; // return future object that is connected to e specified event
	}
	//}


	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> tempBQ = new LinkedBlockingQueue<>();
		queueManager.put(m, tempBQ);
		//queueEventLock.notifyAll();
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
		//System.out.println("awaitMessage() called in msgBus");
		//synchronized (queueEventLock) {
//			while (queueManager.get(m).isEmpty()) {
//				System.out.println("msgBus: await: while: " + Thread.currentThread().getName() +"this is "+ this);
//				queueEventLock.wait();
//			}
			try {
				return queueManager.get(m).take();
			} catch (Exception exp) {
				//System.out.println(exp.getMessage());
				return null;
			}
		}
	//}
}
