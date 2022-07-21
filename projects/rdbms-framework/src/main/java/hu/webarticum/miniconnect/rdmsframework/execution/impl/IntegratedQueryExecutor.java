package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.DeleteQuery;
import hu.webarticum.miniconnect.rdmsframework.query.InsertQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class IntegratedQueryExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        if (query instanceof SelectQuery) {
            return new SelectExecutor().execute(storageAccess, state, query);
        } else if (query instanceof SpecialSelectQuery) {
            return new SpecialSelectExecutor().execute(storageAccess, state, query);
        } else if (query instanceof InsertQuery) {
            return new InsertExecutor().execute(storageAccess, state, query);
        } else if (query instanceof UpdateQuery) {
            return new UpdateExecutor().execute(storageAccess, state, query);
        } else if (query instanceof DeleteQuery) {
            return new DeleteExecutor().execute(storageAccess, state, query);
        } else if (query instanceof ShowSchemasQuery) {
            return new ShowSchemasExecutor().execute(storageAccess, state, query);
        } else if (query instanceof ShowTablesQuery) {
            return new ShowTablesExecutor().execute(storageAccess, state, query);
        } else if (query instanceof UseQuery) {
            return new UseExecutor().execute(storageAccess, state, query);
        } else {
            return new StoredResult(new StoredError(
                    1,
                    "00001",
                    "Unknown query type: " + query.getClass().getName()));
        }
    }

}
