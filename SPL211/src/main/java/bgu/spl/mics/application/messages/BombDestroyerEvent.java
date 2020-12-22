package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.concurrent.CountDownLatch;

public class BombDestroyerEvent implements Event<Boolean> {
    public static CountDownLatch countSubscribed = new CountDownLatch(1);

    public BombDestroyerEvent() {
        countSubscribed = new CountDownLatch(1);
    }
}
