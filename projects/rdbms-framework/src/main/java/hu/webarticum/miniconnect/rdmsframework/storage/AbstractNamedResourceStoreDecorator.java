package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.ImmutableList;

public abstract class AbstractNamedResourceStoreDecorator<T extends NamedResource> implements NamedResourceStore<T> {
    
    protected final NamedResourceStore<T> baseStore;

    
    protected AbstractNamedResourceStoreDecorator(NamedResourceStore<T> baseStore) {
        this.baseStore = baseStore;
    }
    

    @Override
    public ImmutableList<String> names() {
        return baseStore.names();
    }

    @Override
    public ImmutableList<T> resources() {
        return names().map(this::get);
    }

    @Override
    public boolean contains(String name) {
        return baseStore.contains(name);
    }

    @Override
    public T get(String name) {
        return baseStore.get(name);
    }
    
}
