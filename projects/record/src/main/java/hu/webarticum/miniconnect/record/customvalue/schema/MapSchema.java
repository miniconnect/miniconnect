package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class MapSchema implements Schema {
    
    public static final byte FLAG = (byte) 'M';
    

    public MapSchema() {
        // singleton
    }
    

    public static MapSchema readMainFrom(InputStream in) {
        
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
