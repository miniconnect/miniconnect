package hu.webarticum.miniconnect.rest.query;

import hu.webarticum.miniconnect.api.MiniSession;

public class DefaultEntityListQueryExecutorStrategy implements EntityListQueryExecutorStrategy {

    @Override
    public EntityListQueryExecutor createFor(MiniSession session) {
        return new DefaultEntityListQueryExecutor(session);
    }

}
