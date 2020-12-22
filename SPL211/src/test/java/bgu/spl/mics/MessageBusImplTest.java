package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.testBroadcast;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class MessageBusImplTest {
    MessageBusImpl messageBus;
    Broadcast broadcast;
    MicroService microService1;
    MicroService microService2;

    @AfterEach
    void tearDown() {

        messageBus.unregister(microService1);
        messageBus.unregister(microService2);
    }

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();

        microService1 = new MicroService("SendTest") {
            @Override
            protected void initialize() {

            }

            @Override
            protected void WriteToDiary() {

            }
        };

        microService2 = new MicroService("receiveTest") {
            @Override
            protected void initialize() {

            }

            @Override
            protected void WriteToDiary() {

            }
        };
        messageBus.register(microService1);
        messageBus.register(microService2);
    }

    @Test
    void testComplete() {
        List<Integer> tempEwoks = new LinkedList<>();
        tempEwoks.add(1);
        tempEwoks.add(2);
        AttackEvent attackEvent = new AttackEvent(1000, tempEwoks);
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
        List<Integer> tempEwoks = new LinkedList<>();
        tempEwoks.add(1);
        tempEwoks.add(2);
        AttackEvent attackEvent = new AttackEvent(1000, tempEwoks);
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