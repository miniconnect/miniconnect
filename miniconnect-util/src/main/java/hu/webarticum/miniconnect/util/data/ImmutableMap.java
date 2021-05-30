package hu.webarticum.miniconnect.util.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImmutableMap<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;


    private final Map<K, V> data; // NOSONAR


    public ImmutableMap() {
        this.data = Collections.emptyMap();
    }

    public ImmutableMap(Map<K, V> data) {
        this.data = new HashMap<>(data);
    }

    
    public static <K, V> ImmutableMap<K, V> empty() {
        return new ImmutableMap<>();
    }


    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    public V get(K key) {
        return data.get(key);
    }

    public Map<K, V> toMap() {
        return new HashMap<>(data);
    }
    
    @Override
    public int hashCode() {
        return data.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        
        if (!(other instanceof ImmutableMap)) {
            return false;
        }
        
        ImmutableMap<?, ?> otherMap = (ImmutableMap<?, ?>) other;
        return data.equals(otherMap.data);
    }
    
    @Override
    public String toString() {
        return data.toString();
    }

}
