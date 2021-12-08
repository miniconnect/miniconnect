package hu.webarticum.miniconnect.rdmsframework.util.selection;

import java.math.BigInteger;

public interface Selection extends Explorable {

    public BigInteger size();

    public boolean isEmpty();

    public BigInteger at(BigInteger index);
    
}
