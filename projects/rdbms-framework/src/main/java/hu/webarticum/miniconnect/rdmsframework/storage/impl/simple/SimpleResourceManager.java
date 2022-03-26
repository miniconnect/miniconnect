package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResource;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;

public class SimpleResourceManager<T extends NamedResource> implements NamedResourceStore<T> {
    
    private Map<String, T> resources = Collections.synchronizedMap(new TreeMap<>());
    

    @Override
    public ImmutableList<String> names() {
        return ImmutableList.fromCollection(resources.keySet());
    }

    @Override
    public boolean contains(String name) {
        return resources.containsKey(name);
    }
    
    @Override
    public T get(String name) {
        return resources.get(name);
    }
    
    public void register(T resource) {
        String name = resource.name();
        resources.compute(name, (n, t) -> checkRegister(n, t, resource));
    }
    
    private T checkRegister(String name, T existingResource, T resource) {
        if (existingResource != null) {
            throw new IllegalArgumentException("Resource exists: " + name);
        }
        return resource;
    }

    public void remove(String name) {
        if (resources.remove(name) == null) {
            throw new NoSuchElementException("No resource named " + name);
        }
    }

}
