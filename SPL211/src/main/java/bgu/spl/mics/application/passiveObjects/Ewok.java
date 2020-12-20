package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
	boolean available;

	public Ewok (int serialNum){
	    this.serialNumber = serialNum;
	    available = true;
    }

    public boolean isAvailable() {
        return available;
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
        while (!available) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
    	available = true;
    	this.notifyAll();
    }

    @Override
    public String toString() {
        return "Ewok{" +
                + serialNumber +
                '}';
    }
}
