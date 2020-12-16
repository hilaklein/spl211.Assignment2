package bgu.spl.mics.application;

import bgu.spl.mics.Event;

public class cEvent<T> implements Event {
    private int ID;

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
