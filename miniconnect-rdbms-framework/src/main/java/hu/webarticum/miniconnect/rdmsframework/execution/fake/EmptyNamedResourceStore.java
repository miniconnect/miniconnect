package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import hu.webarticum.miniconnect.rdmsframework.storage.NamedResource;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class EmptyNamedResourceStore<T extends NamedResource> implements NamedResourceStore<T> {

    @Override
    public ImmutableList<String> names() {
        return ImmutableList.empty();
    }

    @Override
    public T get(String name) {
        return null;
    }

}
