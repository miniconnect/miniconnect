package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResource;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;

public class EmptyNamedResourceStore<T extends NamedResource> implements NamedResourceStore<T> {

    @Override
    public ImmutableList<String> names() {
        return ImmutableList.empty();
    }

    @Override
    public ImmutableList<T> resources() {
        return ImmutableList.empty();
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public T get(String name) {
        return null;
    }

}
