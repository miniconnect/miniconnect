package hu.webarticum.miniconnect.rdmsframework.execution;

import java.util.concurrent.Future;

public interface SqlExecutor {

    public Future<Object> execute(String sql); // TODO: result type
    
}
