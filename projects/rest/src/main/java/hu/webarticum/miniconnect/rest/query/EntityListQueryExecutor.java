package hu.webarticum.miniconnect.rest.query;

import hu.webarticum.miniconnect.api.MiniResult;

public interface EntityListQueryExecutor {

    public MiniResult execute(EntityListQuery query);
    
}
