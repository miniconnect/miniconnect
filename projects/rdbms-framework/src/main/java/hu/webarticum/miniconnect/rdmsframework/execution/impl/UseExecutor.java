package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.ThrowingQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class UseExecutor implements ThrowingQueryExecutor {

    @Override
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query) {
        return executeInternal(storageAccess, state, (UseQuery) query);
    }
    
    private MiniResult executeInternal(StorageAccess storageAccess, EngineSessionState state, UseQuery useQuery) {
        String schemaName = useQuery.schema();
        if (!storageAccess.schemas().contains(schemaName)) {
            throw PredefinedError.SCHEMA_NOT_FOUND.toException(schemaName);
        }
        
        state.setCurrentSchema(schemaName);
        return new StoredResult();
    }

}
