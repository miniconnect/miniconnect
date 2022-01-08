package hu.webarticum.miniconnect.rdmsframework.engine;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface EngineSession extends CheckableCloseable {
    
    public Engine engine();

    public SqlParser sqlParser();

    public QueryExecutor queryExecutor();

    public StorageAccess storageAccess();
    
    // TODO: transaction management

}
