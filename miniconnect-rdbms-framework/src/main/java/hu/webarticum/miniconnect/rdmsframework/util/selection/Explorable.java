package hu.webarticum.miniconnect.rdmsframework.util.selection;

import java.math.BigInteger;

public interface Explorable extends Iterable<BigInteger> {

    public boolean contains(BigInteger value);
    
}
