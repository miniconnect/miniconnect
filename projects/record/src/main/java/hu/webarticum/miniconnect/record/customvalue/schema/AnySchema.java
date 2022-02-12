package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class AnySchema implements Schema {
    
    private static final AnySchema INSTANCE = new AnySchema();
    
    
    private AnySchema() {
        // singleton
    }
    

    public static AnySchema instance() {
        return INSTANCE;
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
