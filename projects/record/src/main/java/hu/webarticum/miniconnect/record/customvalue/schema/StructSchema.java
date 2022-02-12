package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class StructSchema implements Schema {

    public StructSchema() {
        // singleton
    }
    

    public static StructSchema readMainFrom(InputStream in) {
        
        // TODO
        return null;
        
    }

    
    @Override
    public void writeTo(OutputStream out) {
        
        // TODO
        
    }

    @Override
    public Object readValueFrom(InputStream in) {
        
        // TODO
        return null;
        
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        
        // TODO
        
    }

}
