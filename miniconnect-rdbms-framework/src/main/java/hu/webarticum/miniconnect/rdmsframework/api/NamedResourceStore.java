package hu.webarticum.miniconnect.rdmsframework.api;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public interface NamedResourceStore<T extends NamedResource> {

    public ImmutableList<String> names();
    
    public T get(String name);
    
}
