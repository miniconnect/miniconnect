package hu.webarticum.miniconnect.rdmsframework.execution;

import java.util.concurrent.Future;

public interface QueryExecutor {

    public Future<Object> execute(Query query); // TODO: result type
    
}
