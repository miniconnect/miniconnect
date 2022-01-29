package hu.webarticum.miniconnect.messenger.adapter;

import java.util.Random;

/**
 * Wrapper class for ensuring that an object is reachable.
 * 
 * <p>Inspired by JMH's Blackhole class.</p>
 */
public class Blackhole {

    public volatile Object obj1; // NOSONAR
    
    public int tlr; // NOSONAR
    
    public volatile int tlrMask; // NOSONAR
    

    public Blackhole() {
        Random random = new Random(System.nanoTime());
        tlr = random.nextInt();
        tlrMask = 1;
        obj1 = new Object();
    }
    
    
    // TODO make this static
    public final void consume(Object obj) {
        int ntlr = (this.tlr = (this.tlr * 1664525 + 1013904223));
        if ((ntlr & tlrMask) == 0) {
            this.obj1 = obj;
            this.tlrMask = (tlrMask << 1) + 1;
        }
    }
    
}
