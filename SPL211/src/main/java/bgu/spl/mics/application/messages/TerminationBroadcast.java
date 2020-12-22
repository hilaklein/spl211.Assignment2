package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.CountDownLatch;

public class TerminationBroadcast<T> implements Broadcast {
    public static CountDownLatch terminateCountDown = new CountDownLatch(5);

    public TerminationBroadcast() {
        terminateCountDown = new CountDownLatch(5);
    }
}
