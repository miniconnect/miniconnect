package hu.webarticum.miniconnect.rest;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSession;
import hu.webarticum.miniconnect.rest.crud.DefaultEntityCrudStrategy;
import hu.webarticum.miniconnect.rest.crud.EntityCrudStrategy;
import hu.webarticum.miniconnect.rest.query.DefaultEntityListQueryExecutorStrategy;
import hu.webarticum.miniconnect.rest.query.EntityListQueryExecutorStrategy;
import hu.webarticum.miniconnect.rest.schema.DefaultRestSchemaScanner;
import hu.webarticum.miniconnect.rest.schema.RestSchema;
import hu.webarticum.miniconnect.rest.schema.RestSchemaScanner;
import hu.webarticum.miniconnect.server.ClientMessenger;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import jakarta.inject.Singleton;

// TODO
@Factory
public class Configuration {

    @Prototype
    public RestSchema createRestSchema(MiniSession session, RestSchemaScanner scanner) {
        return scanner.scanSchema(session);
    }

    @Prototype
    public RestSchemaScanner createRestSchemaScanner() {
        return new DefaultRestSchemaScanner();
    }

    @Prototype
    public EntityListQueryExecutorStrategy createEntityListQueryExecutorStrategy() {
        return new DefaultEntityListQueryExecutorStrategy();
    }

    @Singleton
    public EntityCrudStrategy createEntityCrudStrategy() {
        return new DefaultEntityCrudStrategy();
    }
    
    @Singleton
    public MiniSession createMiniSession() {
        return new MessengerSession(new ClientMessenger("localhost", 3430));
    }

}
