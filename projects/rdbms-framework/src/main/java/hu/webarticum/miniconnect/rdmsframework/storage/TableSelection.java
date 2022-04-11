package hu.webarticum.miniconnect.rdmsframework.storage;

import java.math.BigInteger;

public interface TableSelection extends Iterable<BigInteger> {

    public boolean containsRow(BigInteger rowIndex);
    
}
