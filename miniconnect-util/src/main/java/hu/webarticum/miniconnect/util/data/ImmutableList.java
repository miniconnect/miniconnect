package hu.webarticum.miniconnect.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ImmutableList<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;


    private final List<T> data; // NOSONAR


    public ImmutableList() {
        this.data = Collections.emptyList();
    }

    public ImmutableList(ImmutableList<? extends T> data) {
        this(data.data);
    }

    public ImmutableList(Collection<? extends T> data) {
        this.data = new ArrayList<>(data);
    }

    private ImmutableList(List<T> data, Void ignored) { // NOSONAR
        this.data = data;
    }

    
    public static <T> ImmutableList<T> empty() {
        return new ImmutableList<>();
    }

    @SafeVarargs
    public static <T> ImmutableList<T> of(T... items) {
        return new ImmutableList<>(Arrays.asList(items));
    }

    public static <T> ImmutableList<T> fromIterable(
            Iterable<? extends T> iterable) {

        return fromIterator(iterable.iterator());
    }

    public static <T> ImmutableList<T> fromIterator(
            Iterator<? extends T> iterator) {

        List<T> data = new ArrayList<>();
        iterator.forEachRemaining(data::add);
        return new ImmutableList<>(data);
    }


    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    public T get(int index) {
        return data.get(index);
    }

    public boolean contains(T item) {
        return data.contains(item);
    }
    
    public <U> ImmutableList<U> map(Function<T, U> mapper) {
        List<U> mappedData = new ArrayList<>(data.size());
        for (T item : data) {
            mappedData.add(mapper.apply(item));
        }
        return new ImmutableList<>(mappedData, null);
    }

    public <U> ImmutableList<U> mapIndex(BiFunction<Integer, T, U> mapper) {
        List<U> mappedData = new ArrayList<>(data.size());
        int length = data.size();
        for (int i = 0; i < length; i++) {
            T item = data.get(i);
            mappedData.add(mapper.apply(i, item));
        }
        return new ImmutableList<>(mappedData, null);
    }

    public ImmutableList<T> filter(Predicate<T> filter) {
        List<T> filteredData = new ArrayList<>();
        for (T item : data) {
            if (filter.test(item)) {
                filteredData.add(item);
            }
        }
        return new ImmutableList<>(filteredData, null);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public Object[] toArray() {
        return data.toArray();
    }

    public List<T> toList() {
        return new ArrayList<>(data);
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
        
        if (!(other instanceof ImmutableList)) {
            return false;
        }
        
        ImmutableList<?> otherList = (ImmutableList<?>) other;
        return data.equals(otherList.data);
    }
    
    @Override
    public String toString() {
        return data.toString();
    }

}
