package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;

public interface AlreadyMetaTypedSchemaReader {

    public Schema readFrom(InputStream in);
    
}
