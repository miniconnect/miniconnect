package hu.webarticum.miniconnect.rdmsframework.api;

import java.math.BigInteger;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface Index extends NamedResource {

    public boolean isUnique();

    public boolean isOrdered();
    
    public Iterable<BigInteger> find(
            ImmutableList<?> from,
            boolean fromInclusive,
            ImmutableList<?> to,
            boolean toInclusive);

    public default Iterable<BigInteger> find(
            Object from, boolean fromInclusive, Object to, boolean toInclusive) {
        return find(ImmutableList.of(from), fromInclusive, ImmutableList.of(to), toInclusive);
    }

    public default Iterable<BigInteger> find(Object value) {
        return find(ImmutableList.of(value), true, ImmutableList.of(value), true);
    }
    
}
