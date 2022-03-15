package hu.webarticum.miniconnect.rest.crud;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;

public interface EntityCrudStrategy {

    public EntityCrud createFor(
            MiniSession session, String tableName, ImmutableList<String> primaryKey);
    
}
