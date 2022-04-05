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
import hu.webarticum.miniconnect.rdmsframework.query.ShowSchemasQuery;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.util.LikeMatcher;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SimpleShowSchemasExecutor implements QueryExecutor {
    
    private static final String COLUMN_NAME = "Schemas";
    
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, Query query) {
        ShowSchemasQuery showSchemasQuery = (ShowSchemasQuery) query;
        ImmutableList<String> schemaNames = storageAccess.schemas().names();
        String like = showSchemasQuery.like();
        if (like != null) {
            schemaNames = schemaNames.filter(schemaName -> match(like, schemaName));
        }
        ValueTranslator stringTranslator = StandardValueType.STRING.defaultTranslator();
        MiniColumnHeader columnHeader = new StoredColumnHeader(
                COLUMN_NAME,
                false,
                stringTranslator.definition());
        ImmutableList<MiniColumnHeader> columnHeaders = ImmutableList.of(columnHeader);
        ImmutableList<ImmutableList<MiniValue>> data = schemaNames.map(
                tableName -> ImmutableList.of(stringTranslator.encodeFully(tableName)));
        return new StoredResult(new StoredResultSetData(columnHeaders, data));
    }

    private boolean match(String like, String tableName) {
        return new LikeMatcher(like).test(tableName);
    }
    
}
