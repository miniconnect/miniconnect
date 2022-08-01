package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface Sequence {
    
    public BigInteger get();
    
    public BigInteger getAndIncrement();
    
    public void ensureGreaterThan(BigInteger high);

}
