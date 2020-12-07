package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok ewok;

    @BeforeEach
    public void setUp(){
        ewok = new Ewok();
    }


    @Test
    void acquire() {
        ewok.acquire();
        assertFalse(ewok.available);
    }

    @Test
    void release() {
        ewok.release();
        assertTrue(ewok.available);
    }
}