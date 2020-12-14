package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

public class Input {
    /*
      "attacks": [
    {
      "duration" : 1000,
      "serials" : [1,2]
    },
    {
      "duration" : 1000,
      "serials" : [2,1]
    }
  ],
  "R2D2": 2000,
  "Lando": 2000,
  "Ewoks": 2
     */

    public Attack[] attacks;
    public R2D2Microservice R2D2;
    public LandoMicroservice Lando;
    public Ewoks Ewoks;


}
