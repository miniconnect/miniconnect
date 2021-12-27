package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface NamedResourceStore<T extends NamedResource> {

    public ImmutableList<String> names();
    
    public T get(String name);
    
}
