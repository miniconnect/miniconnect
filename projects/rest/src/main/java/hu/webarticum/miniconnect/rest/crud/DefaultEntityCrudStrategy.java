package hu.webarticum.miniconnect.rest.crud;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class DefaultEntityCrudStrategy implements EntityCrudStrategy {

    @Override
    public EntityCrud createFor(
            MiniSession session, String tableName, ImmutableList<String> primaryKey) {
        return new DefaultEntityCrud(session, tableName, primaryKey);
    }

}
