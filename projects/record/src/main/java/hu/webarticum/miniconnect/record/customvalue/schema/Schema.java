package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public interface Schema {

    public void writeTo(OutputStream out);

    public Object readValueFrom(InputStream in);
    
    public void writeValueTo(Object value, OutputStream out);

    
    public static Schema readFrom(InputStream in) {
        try {
            MetaType metaType = MetaType.ofFlag((byte) in.read());
            return metaType.alreadyMetaTypedSchemaReader().apply(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
