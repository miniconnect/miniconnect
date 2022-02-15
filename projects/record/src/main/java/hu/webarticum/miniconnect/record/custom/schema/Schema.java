package hu.webarticum.miniconnect.record.custom.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.type.StandardValueType;

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
    
    public static Schema buildAdHocFor(Object value) {
        if (value instanceof ImmutableList) {
            return new ListSchema(AnySchema.instance());
        } else if (value instanceof ImmutableMap) {
            return new MapSchema(AnySchema.instance(), AnySchema.instance());
        }
        
        for (StandardValueType valueType : StandardValueType.values()) {
            if (valueType.clazz().isAssignableFrom(value.getClass())) {
                return new StandardSchema(valueType);
            }
        }
        
        if (value instanceof Serializable) {
            return JavaSchema.instance();
        }
        
        throw new IllegalArgumentException(
                "Unencodable type given: " + value.getClass().getCanonicalName());
    }
    
}
