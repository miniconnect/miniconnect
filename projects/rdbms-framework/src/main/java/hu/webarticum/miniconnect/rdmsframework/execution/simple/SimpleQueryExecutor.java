package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public class SimpleQueryExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, Query query) {
        if (query instanceof SelectQuery) {
            return new SimpleSelectExecutor().execute(storageAccess, query);
        } else if (query instanceof ShowTablesQuery) {
                return new SimpleShowTablesExecutor().execute(storageAccess, query);
        } else {
            return new StoredResult(new StoredError(
                    1,
                    "00001",
                    "Unknown query type: " + query.getClass().getName()));
        }
    }

}
