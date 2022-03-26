package hu.webarticum.miniconnect.rdmsframework.execution.simple;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.ShowTablesQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.LikeMatcher;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SimpleShowTablesExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, Query query) {
        ShowTablesQuery showTablesQuery = (ShowTablesQuery) query;
        Schema schema = storageAccess.schemas().get("default"); // FIXME
        ImmutableList<String> tableNames = schema.tables().names();
        String like = showTablesQuery.like();
        if (like != null) {
            tableNames = tableNames.filter(tableName -> match(like, tableName));
        }
        ValueTranslator stringTranslator = StandardValueType.STRING.defaultTranslator();
        MiniColumnHeader columnHeader = new StoredColumnHeader(
                "Tables_in_default", // FIXME
                false,
                stringTranslator.definition());
        ImmutableList<MiniColumnHeader> columnHeaders = ImmutableList.of(columnHeader);
        ImmutableList<ImmutableList<MiniValue>> data = tableNames.map(
                tableName -> ImmutableList.of(stringTranslator.encodeFully(tableName)));
        return new StoredResult(new StoredResultSetData(columnHeaders, data));
    }

    private boolean match(String like, String tableName) {
        return new LikeMatcher(like).test(tableName);
    }
    
}
