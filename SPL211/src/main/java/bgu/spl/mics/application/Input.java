package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

public class Input {
    public Attack[] attacks;

    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }

    public void setR2D2(R2D2Microservice r2D2) {
        R2D2 = r2D2;
    }

    public void setLando(LandoMicroservice lando) {
        Lando = lando;
    }

    public void setEwoks(bgu.spl.mics.application.passiveObjects.Ewoks ewoks) {
        Ewoks = ewoks;
    }

    public Attack[] getAttacks() {
        return attacks;
    }

    public R2D2Microservice getR2D2() {
        return R2D2;
    }

    public LandoMicroservice getLando() {
        return Lando;
    }

    public bgu.spl.mics.application.passiveObjects.Ewoks getEwoks() {
        return Ewoks;
    }

    public R2D2Microservice R2D2;
    public LandoMicroservice Lando;
    public Ewoks Ewoks;


}
