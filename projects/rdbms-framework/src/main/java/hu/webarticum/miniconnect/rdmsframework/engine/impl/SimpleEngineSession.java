package hu.webarticum.miniconnect.rdmsframework.engine.impl;

import hu.webarticum.miniconnect.rdmsframework.engine.EngineSession;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class SimpleEngineSession implements EngineSession {
    
    private final SimpleEngine engine;
    
    
    public SimpleEngineSession(SimpleEngine engine) {
        this.engine = engine;
    }
    

    @Override
    public SimpleEngine engine() {
        return engine;
    }
    
    @Override
    public SqlParser sqlParser() {
        return engine.sqlParser();
    }
    
    @Override
    public QueryExecutor queryExecutor() {
        return engine.queryExecutor();
    }
    
    @Override
    public StorageAccess storageAccess() {
        return engine.storageAccess();
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public boolean isClosed() {
        return engine.isClosed();
    }
    
}
