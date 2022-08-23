package hu.webarticum.miniconnect.rdmsframework.engine;

import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.parser.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;

public interface TackedEngine extends Engine {

    public SqlParser sqlParser();

    public QueryExecutor queryExecutor();

    public StorageAccess storageAccess();

}