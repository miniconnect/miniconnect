package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class TupleSchema implements Schema {

    public TupleSchema() {
        // singleton
    }
    

    public static TupleSchema readMainFrom(InputStream in) {
        
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
