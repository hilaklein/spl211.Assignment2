package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.testBroadcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MessageBusImplTest {
    MessageBusImpl messageBus;
    Broadcast broadcast;
    MicroService microService1;
    MicroService microService2;
    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();

        microService1 = new MicroService("SendTest") {
            @Override
            protected void initialize() {

            }
        };
        microService2 = new MicroService("receiveTest") {
            @Override
            protected void initialize() {

            }
        };
        messageBus.register(microService1);
        messageBus.register(microService2);
    }

    @Test
    void testComplete() {
        AttackEvent attackEvent = new AttackEvent();
        Callback callback = c -> {};
        microService1.subscribeEvent(attackEvent.getClass(),callback);
        Future future = new Future();
        future = microService2.sendEvent(attackEvent);
        assertFalse(future.isDone());
        microService1.complete(attackEvent,true); // calling the complete method in messageBus
        assertTrue(future.isDone());
    }

    @Test
    void testSendBroadcast() {
        Callback callback = c -> {};
        broadcast = new testBroadcast();
        microService1.subscribeBroadcast(broadcast.getClass(), callback);
        microService2.sendBroadcast(broadcast);
        Message m = new Message() {
        };
        try {
            m = messageBus.awaitMessage(microService1);

        } catch (InterruptedException e) {

        }
        assertTrue(broadcast.equals(m));

    }

    @Test
    void testSendEvent() {

        Callback callback = c -> {};
        AttackEvent attackEvent = new AttackEvent();
        microService1.subscribeEvent(attackEvent.getClass(), callback);
        microService2.sendEvent(attackEvent);
        Message m = new Message() {
        };

        try {
            m = messageBus.awaitMessage(microService1);

        } catch (InterruptedException e) {

        }
        assertTrue(attackEvent.equals(m));

    }
}