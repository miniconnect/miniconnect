package hu.webarticum.miniconnect.rest.schema;

import hu.webarticum.miniconnect.api.MiniSession;

public interface RestSchemaScanner {

    public RestSchema scanSchema(MiniSession session);
    
}
