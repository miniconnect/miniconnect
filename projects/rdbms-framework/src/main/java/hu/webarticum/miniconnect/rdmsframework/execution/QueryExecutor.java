package hu.webarticum.miniconnect.rdmsframework.execution;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface QueryExecutor {

    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query);
    
}
