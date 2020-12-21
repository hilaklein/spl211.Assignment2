package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Ewok[] ewoks;

    public Ewoks(int numOfEwoks){
        ewoks = new Ewok[numOfEwoks];
        for (int i = 0; i < numOfEwoks; i++){
            ewoks[i] = new Ewok(i+1);
        }
    }

    public Ewok[] getEwoksArr(){
        return this.ewoks;
    }

}
