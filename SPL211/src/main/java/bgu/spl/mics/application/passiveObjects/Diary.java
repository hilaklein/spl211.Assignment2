package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    /*
    ●int totalAttacks - the total number of attacks executed by HanSolo and C3PO.can also be of AtomicInteger type. Stamped only by HanSolo or C3PO ​(!!)​.
    ●long HanSoloFinish - a timestamp indicating when HanSolo finished theexecution of all his attacks.
    ●long C3POFinish - a timestamp indicating when C3PO finished the execution ofall his attacks.
    ●long R2D2Deactivate - a timestamp indicating when R2D2 finished deactivationthe shield generator.
    ●long LeiaTerminate - a time stamp that Leia puts in right before termination.
    ●long HanSoloTerminate - a time stamp that HanSolo puts in right beforetermination.
    ●long C3POTerminate - a time stamp that C3PO puts in right before termination.
    ●long R2D2Terminate - a time stamp that R2d2 puts in right before termination.
    ●long LandoTerminate - a time stamp that Lando puts in right before termination.
    ●To get those timestamps, simply use System.currentTimeMillis().
    ●We will check that your timestamps make sense.
    ●Each timestamp is recorded by the specified name, e.g. only C3PO is allowed toset the value of C3POFinish. The totalAttacks member is recorded​
    only byHanSolo or C3PO​.
    ●You can add to this class members and methods as you see right
     */

    private static class SingletonHolder{
        private static Diary instance = new Diary();
    }

    private AtomicInteger totalAttacks = new AtomicInteger(0);
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;



    public static Diary getInstance(){
        return SingletonHolder.instance;
    }

    public void incrementTotalAttacks() {
        this.totalAttacks.incrementAndGet();
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }
}
