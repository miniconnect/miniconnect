package hu.webarticum.miniconnect.rdmsframework.execution;

import java.util.concurrent.Future;

import hu.webarticum.miniconnect.rdmsframework.query.Query;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface QueryExecutor {

    public Future<Object> execute(StorageAccess storageAccess, Query query); // TODO: result type
    
}
