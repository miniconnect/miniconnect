package hu.webarticum.miniconnect.rest.crud;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class DefaultEntityCrud implements EntityCrud {
    
    private final MiniSession session;
    
    private final String tableName;
    
    private final ImmutableList<String> primaryKey;
    

    public DefaultEntityCrud(
            MiniSession session, String tableName, ImmutableList<String> primaryKey) {
        this.session = session;
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }


    @Override
    public MiniResult create(ImmutableMap<String, Object> data) {
        // TODO
        throw new UnsupportedOperationException("This API is read-only");
    }

    @Override
    public MiniResult read(ImmutableMap<String, Object> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MiniResult update(ImmutableMap<String, Object> key, ImmutableMap<String, Object> data) {
        // TODO
        throw new UnsupportedOperationException("This API is read-only");
    }

    @Override
    public MiniResult delete(ImmutableMap<String, Object> key) {
        // TODO
        throw new UnsupportedOperationException("This API is read-only");
    }
    
}
