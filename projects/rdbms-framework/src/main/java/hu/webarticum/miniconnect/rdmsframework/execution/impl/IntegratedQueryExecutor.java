package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.impl.select.SelectExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.DeleteQuery;
import hu.webarticum.miniconnect.rdmsframework.query.InsertQuery;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectCountQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SelectSpecialQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SelectValueQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SetVariableQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UpdateQuery;
import hu.webarticum.miniconnect.rdmsframework.query.UseQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class IntegratedQueryExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        if (query instanceof SelectQuery) {
            return new SelectExecutor().execute(storageAccess, state, query);
        } else if (query instanceof SelectCountQuery) {
            return new SelectCountExecutor().execute(storageAccess, state, query);
        } else if (query instanceof SelectSpecialQuery) {
            return new SelectSpecialExecutor().execute(storageAccess, state, query);
        } else if (query instanceof SelectValueQuery) {
            return new SelectValueExecutor().execute(storageAccess, state, query);
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
        } else if (query instanceof SetVariableQuery) {
            return new SetVariableExecutor().execute(storageAccess, state, query);
        } else {
            return PredefinedError.QUERY_TYPE_NOT_FOUND.toResult(query.getClass().getName());
        }
    }

}
