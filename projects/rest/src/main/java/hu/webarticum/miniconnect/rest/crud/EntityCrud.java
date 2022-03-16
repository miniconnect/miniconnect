package hu.webarticum.miniconnect.rest.crud;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public interface EntityCrud {

    public MiniResult create(ImmutableMap<String, Object> data);

    public MiniResult read(ImmutableList<String> key);

    public MiniResult update(ImmutableList<String> key, ImmutableMap<String, Object> data);

    public MiniResult delete(ImmutableList<String> key);
    
}
