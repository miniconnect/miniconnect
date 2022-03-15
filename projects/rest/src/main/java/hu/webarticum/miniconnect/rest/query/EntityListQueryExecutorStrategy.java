package hu.webarticum.miniconnect.rest.query;

import hu.webarticum.miniconnect.api.MiniSession;

public interface EntityListQueryExecutorStrategy {

    public EntityListQueryExecutor createFor(MiniSession session);
    
}
