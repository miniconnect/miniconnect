package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class SimpleUseExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        UseQuery useQuery = (UseQuery) query;
        String schemaName = useQuery.schema();
        if (!storageAccess.schemas().contains(schemaName)) {
            return new StoredResult(new StoredError(4, "00004", "No such schema: " + schemaName));
        }
        state.setCurrentSchema(schemaName);
        return new StoredResult();
    }

}
