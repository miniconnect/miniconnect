package hu.webarticum.miniconnect.rdmsframework.execution;

import hu.webarticum.miniconnect.api.MiniErrorException;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.PredefinedError;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSessionState;
import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface ThrowingQueryExecutor extends QueryExecutor {

    @Override
    public default MiniResult execute(StorageAccess storageAccess, EngineSessionState state, Query query) {
        try (CheckableCloseable lock = storageAccess.lockManager().lockExclusively()) {
            return executeThrowing(storageAccess, state, query);
        } catch (MiniErrorException e) {
            return new StoredResult(StoredError.of(e));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PredefinedError.QUERY_INTERRUPTED.toResult();
        }
    }
    
    public MiniResult executeThrowing(StorageAccess storageAccess, EngineSessionState state, Query query);

}
