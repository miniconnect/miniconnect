package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.util.function.Function;

public enum MetaType {

    ANY((byte) 0, in -> AnySchema.instance()),
    
    STANDARD((byte) 1, StandardSchema::readMainFrom),
    
    LIST((byte) 'L', ListSchema::readMainFrom),

    TUPLE((byte) 'T', TupleSchema::readMainFrom),
    
    STRUCT((byte) 'S', StructSchema::readMainFrom),
    
    ;
    
    
    private final byte flag;
    
    private final Function<InputStream, Schema> schemaSubReader;
    
    
    private MetaType(byte flag, Function<InputStream, Schema> schemaSubReader) {
        this.flag = flag;
        this.schemaSubReader = schemaSubReader;
    }
    
    public static MetaType ofFlag(byte flag) {
        for (MetaType metaType : MetaType.values()) {
            if (metaType.flag == flag) {
                return metaType;
            }
        }
        throw new IllegalArgumentException("No meta type with flag: " + flag);
    }
    

    public byte flag() {
        return flag;
    }

    public Function<InputStream, Schema> alreadyMetaTypedSchemaReader() {
        return schemaSubReader;
    }
    
}
