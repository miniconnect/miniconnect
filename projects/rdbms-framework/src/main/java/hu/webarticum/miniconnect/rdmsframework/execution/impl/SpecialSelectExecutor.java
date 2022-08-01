package hu.webarticum.miniconnect.rdmsframework.execution.impl;

import java.math.BigInteger;
import java.util.Optional;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectQuery;
import hu.webarticum.miniconnect.rdmsframework.query.SpecialSelectQueryType;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class SpecialSelectExecutor implements QueryExecutor {
    
    @Override
    public MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockShared()) {
            return executeInternal(storageAccess, state, (SpecialSelectQuery) query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new StoredResult(new StoredError(99, "00099", "Query was interrupted"));
        }
    }
    
    private MiniResult executeInternal(
            StorageAccess storageAccess, EngineSessionState state, SpecialSelectQuery specialSelectQuery) {
        StandardValueType.forClazz(String.class).get().defaultTranslator().definition();
        SpecialSelectQueryType queryType = specialSelectQuery.queryType();
        switch (queryType) {
            case CURRENT_USER:
                return createSingleValueResult(String.class, "CURRENT_USER", "");
            case CURRENT_SCHEMA:
                return createSingleValueResult(String.class, "CURRENT_SCHEMA", state.getCurrentSchema());
            case CURRENT_CATALOG:
                return createSingleValueResult(String.class, "CURRENT_CATALOG", state.getCurrentSchema()); // FIXME
            case READONLY:
                return createSingleValueResult(Boolean.class, "READONLY", false); // FIXME
            case AUTOCOMMIT:
                return createSingleValueResult(Boolean.class, "AUTOCOMMIT", true);
            case LAST_INSERT_ID:
                return createSingleValueResult(BigInteger.class, "LAST_INSERT_ID", state.getLastInsertId());
            default:
                return new StoredResult(new StoredError(42, "00042", "No luck, sorry"));
        }
    }
    
    private <T> MiniResult createSingleValueResult(Class<T> clazz, String columnName, T content) {
        ValueTranslator translator = createValueDefinition(clazz);
        MiniValueDefinition columnDefinition = translator.definition();
        MiniColumnHeader columnHeader = new StoredColumnHeader(columnName, false, columnDefinition);
        MiniValue value = translator.encodeFully(content);
        return new StoredResult(new StoredResultSetData(
                ImmutableList.of(columnHeader),
                ImmutableList.of(ImmutableList.of(value))));
    }
    
    // FIXME
    private ValueTranslator createValueDefinition(Class<?> clazz) {
        Optional<StandardValueType> valueTypeOptional = StandardValueType.forClazz(String.class);
        if (valueTypeOptional.isPresent()) {
            return valueTypeOptional.get().defaultTranslator();
        } else {
            return JavaTranslator.of(clazz);
        }
    }

}
