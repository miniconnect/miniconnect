package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class SimpleQueryExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        if (query instanceof SelectQuery) {
            return new SimpleSelectExecutor().execute(storageAccess, state, query);
        } else if (query instanceof ShowSchemasQuery) {
            return new SimpleShowSchemasExecutor().execute(storageAccess, state, query);
        } else if (query instanceof ShowTablesQuery) {
            return new SimpleShowTablesExecutor().execute(storageAccess, state, query);
        } else if (query instanceof UseQuery) {
            return new SimpleUseExecutor().execute(storageAccess, state, query);
        } else {
            return new StoredResult(new StoredError(
                    1,
                    "00001",
                    "Unknown query type: " + query.getClass().getName()));
        }
    }

}
