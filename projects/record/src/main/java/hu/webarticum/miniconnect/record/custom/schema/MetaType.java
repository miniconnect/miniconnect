package hu.webarticum.miniconnect.record.custom.schema;

import java.io.InputStream;
import java.util.function.Function;

public enum MetaType {

    ANY(AnySchema.FLAG, in -> AnySchema.instance()),
    
    STANDARD(StandardSchema.FLAG, StandardSchema::readMainFrom),
    
    LIST(ListSchema.FLAG, ListSchema::readMainFrom),

    MAP(MapSchema.FLAG, MapSchema::readMainFrom),

    STRUCT(StructSchema.FLAG, StructSchema::readMainFrom),

    JAVA(JavaSchema.FLAG, in -> JavaSchema.instance()),
    
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
