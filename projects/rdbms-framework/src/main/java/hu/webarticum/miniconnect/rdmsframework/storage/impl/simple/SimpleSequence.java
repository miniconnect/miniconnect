package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

import hu.webarticum.miniconnect.rdmsframework.storage.Sequence;

public class SimpleSequence implements Sequence {
    
    private final AtomicReference<BigInteger> valueHolder;
    
    
    public SimpleSequence() {
        this(BigInteger.ONE);
    }

    public SimpleSequence(BigInteger value) {
        this.valueHolder = new AtomicReference<>(value);
    }
    

    @Override
    public BigInteger get() {
        return valueHolder.get();
    }

    @Override
    public BigInteger getAndIncrement() {
        return valueHolder.getAndUpdate(v -> v.add(BigInteger.ONE));
    }

    @Override
    public void ensureGreaterThan(BigInteger high) {
        valueHolder.updateAndGet(v -> calculateForEnsureGreaterThan(v, high));
    }
    
    private BigInteger calculateForEnsureGreaterThan(BigInteger currentValue, BigInteger high) {
        if (currentValue.compareTo(high) > 0) {
            return currentValue;
        }
        
        return high.add(BigInteger.ONE);
    }

}
