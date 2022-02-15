package hu.webarticum.miniconnect.record.custom.schema;

import java.io.InputStream;

public interface AlreadyMetaTypedSchemaReader {

    public Schema readFrom(InputStream in);
    
}
