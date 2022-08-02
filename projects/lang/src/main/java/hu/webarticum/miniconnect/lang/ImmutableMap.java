package hu.webarticum.miniconnect.lang;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ImmutableMap<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;


    private final Map<K, V> data; // NOSONAR


    private ImmutableMap(Map<K, V> data) {
        this.data = data;
    }

    
    public static <K, V> ImmutableMap<K, V> empty() {
        return new ImmutableMap<>(Collections.emptyMap());
    }

    public static <K, V> ImmutableMap<K, V> of(K key, V value) {
        Map<K, V> data = new HashMap<>(1);
        data.put(key, value);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of(K key1, V value1, K key2, V value2) {
        Map<K, V> data = new HashMap<>(2);
        data.put(key1, value1);
        data.put(key2, value2);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3) {
        Map<K, V> data = new HashMap<>(3);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4) {
        Map<K, V> data = new HashMap<>(4);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5) {
        Map<K, V> data = new HashMap<>(5);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6) {
        Map<K, V> data = new HashMap<>(6);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        data.put(key6, value6);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7) {
        Map<K, V> data = new HashMap<>(7);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        data.put(key6, value6);
        data.put(key7, value7);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8) {
        Map<K, V> data = new HashMap<>(8);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        data.put(key6, value6);
        data.put(key7, value7);
        data.put(key8, value8);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9) {
        Map<K, V> data = new HashMap<>(9);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        data.put(key6, value6);
        data.put(key7, value7);
        data.put(key8, value8);
        data.put(key9, value9);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> of( // NOSONAR many parameter is OK
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9,
            K key10, V value10) {
        Map<K, V> data = new HashMap<>(10);
        data.put(key1, value1);
        data.put(key2, value2);
        data.put(key3, value3);
        data.put(key4, value4);
        data.put(key5, value5);
        data.put(key6, value6);
        data.put(key7, value7);
        data.put(key8, value8);
        data.put(key9, value9);
        data.put(key10, value10);
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> ofClazzes(
            Class<K> keyClass, // NOSONAR unused generic parameter is OK
            Class<V> valueClass,
            Object... keyValuePairs) {
        int numberOfKeyValuePairs = keyValuePairs.length / 2;
        Map<K, V> data = new HashMap<>(numberOfKeyValuePairs);
        for (int i = 0; i < numberOfKeyValuePairs; i++) {
            @SuppressWarnings("unchecked")
            K key = (K) keyValuePairs[i * 2];
            @SuppressWarnings("unchecked")
            V value = (V) keyValuePairs[(i * 2) + 1];
            data.put(key, value);
        }
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> fromMap(Map<K, V> map) {
        return new ImmutableMap<>(new HashMap<>(map));
    }

    public static <K, V> ImmutableMap<K, V> assignFrom(
            Iterable<K> source, int size, Function<K, V> mapper) {
        Map<K, V> data = new HashMap<>(size);
        for (K key : source) {
            data.put(key, mapper.apply(key));
        }
        return new ImmutableMap<>(data);
    }

    public static <K, V> ImmutableMap<K, V> assignFrom(
            Iterable<K> source, int size, BiFunction<K, Integer, V> mapper) {
        Map<K, V> data = new HashMap<>(size);
        int i = 0;
        for (K key : source) {
            data.put(key, mapper.apply(key, i));
            i++;
        }
        return new ImmutableMap<>(data);
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
    
    public Set<K> keySet() {
        return Collections.unmodifiableSet(data.keySet());
    }

    public ImmutableList<K> keys() {
        return ImmutableList.fromCollection(data.keySet());
    }

    public ImmutableList<V> values() {
        return ImmutableList.fromCollection(data.values());
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

    public <K2, V2> ImmutableMap<K2, V2> map(
            Function<K, K2> keyMapper, Function<V, V2> valueMapper) {
        Map<K2, V2> mappedData = new HashMap<>();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            K2 newKey = keyMapper.apply(entry.getKey());
            V2 newValue = valueMapper.apply(entry.getValue());
            mappedData.put(newKey, newValue);
        }
        return new ImmutableMap<>(mappedData);
    }

    public <K2, V2> ImmutableMap<K2, V2> map(
            BiFunction<K, V, K2> keyMapper, BiFunction<K, V, V2> valueMapper) {
        Map<K2, V2> mappedData = new HashMap<>();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            K2 newKey = keyMapper.apply(key, value);
            V2 newValue = valueMapper.apply(key, value);
            mappedData.put(newKey, newValue);
        }
        return new ImmutableMap<>(mappedData);
    }

    public <V2> ImmutableMap<K, V2> mapValues(BiFunction<K, V, V2> valueMapper) {
        Map<K, V2> mappedData = new HashMap<>(data.size());
        for (Map.Entry<K, V> entry : data.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            V2 newValue = valueMapper.apply(key, value);
            mappedData.put(key, newValue);
        }
        return new ImmutableMap<>(mappedData);
    }
    
    public Map<K, V> asMap() {
        return Collections.unmodifiableMap(data);
    }

    public HashMap<K, V> toHashMap() { // NOSONAR
        return new HashMap<>(data);
    }
    
    public void forEach(BiConsumer<K, V> action) {
        asMap().forEach(action);
    }

    public ImmutableMap<K, V> filter(Predicate<K> filter) {
        return filter((k, v) -> filter.test(k));
    }

    public ImmutableMap<K, V> filter(BiPredicate<K, V> filter) {
        Map<K, V> filteredData = new HashMap<>();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (filter.test(key, value)) {
                filteredData.put(key, value);
            }
        }
        return new ImmutableMap<>(filteredData);
    }

    public ImmutableMap<K, V> merge(ImmutableMap<K, V> otherMap) {
        Map<K, V> mergedData = new HashMap<>(data);
        mergedData.putAll(otherMap.data);
        return new ImmutableMap<>(mergedData);
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
