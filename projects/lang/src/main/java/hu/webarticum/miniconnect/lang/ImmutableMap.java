package hu.webarticum.miniconnect.lang;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ImmutableMap<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;


    private final Map<K, V> data; // NOSONAR


    private ImmutableMap(Map<K, V> data) {
        this.data = data;
    }

    
    public static <K, V> ImmutableMap<K, V> empty() {
        return new ImmutableMap<>(Collections.emptyMap());
    }

    public static <K, V> ImmutableMap<K, V> fromMap(Map<K, V> map) {
        return new ImmutableMap<>(new HashMap<>(map));
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

    public V getOrDefault(K key, V defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableMap(data).entrySet();
    }

    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(data);
    }

    public HashMap<K, V> toHashMap() { // NOSONAR
        return new HashMap<>(data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ImmutableMap)) {
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
