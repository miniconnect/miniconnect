package hu.webarticum.miniconnect.rest.crud;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public interface EntityCrud {

    public MiniResult create(ImmutableMap<String, Object> data);

    public MiniResult read(ImmutableMap<String, Object> key);

    public MiniResult update(ImmutableMap<String, Object> key, ImmutableMap<String, Object> data);

    public MiniResult delete(ImmutableMap<String, Object> key);
    
}
