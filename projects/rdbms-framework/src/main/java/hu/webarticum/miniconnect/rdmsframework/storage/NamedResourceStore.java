package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface NamedResourceStore<T extends NamedResource> {

    public ImmutableList<String> names();
    
    public boolean contains(String name);
    
    public T get(String name);
    
}
