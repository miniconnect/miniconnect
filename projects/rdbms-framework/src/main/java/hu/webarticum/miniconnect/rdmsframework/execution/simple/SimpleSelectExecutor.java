package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.impl.result.StoredValue;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SimpleSelectExecutor implements QueryExecutor {

    @Override
    public MiniResult execute(StorageAccess storageAccess, Query query) {
        if (!(query instanceof SelectQuery)) {
            throw new IllegalArgumentException("Only SELECT queries are supported");
        }
        
        SelectQuery selectQuery = (SelectQuery) query;
        String tableName = selectQuery.tableName();
        
        MiniValueDefinition definition =
                new StoredValueDefinition(StandardValueType.STRING.name());
        ImmutableList<MiniColumnHeader> headers = ImmutableList.of(
                new StoredColumnHeader("name", false, definition));
        ImmutableList<ImmutableList<MiniValue>> data = ImmutableList.of(ImmutableList.of(
                new StoredValue(definition, false, ByteString.of(tableName))));
        return new StoredResult(new StoredResultSetData(headers, data));
    }

}
