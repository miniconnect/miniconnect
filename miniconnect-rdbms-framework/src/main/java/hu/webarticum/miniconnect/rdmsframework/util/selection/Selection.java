package hu.webarticum.miniconnect.rdmsframework.util.selection;

import java.math.BigInteger;

// FIXME: copied from holodb
public interface Selection extends Iterable<BigInteger> {

    public BigInteger size();

    public boolean isEmpty();

    public BigInteger at(BigInteger index);
    
    public boolean contains(BigInteger value);
    
}
