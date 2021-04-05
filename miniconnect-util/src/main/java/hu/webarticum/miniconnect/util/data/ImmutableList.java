package hu.webarticum.miniconnect.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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

}
