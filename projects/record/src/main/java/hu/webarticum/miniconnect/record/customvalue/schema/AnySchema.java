package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class AnySchema implements Schema {
    
    public static final byte FLAG = (byte) 0;
    
    
    private static final AnySchema INSTANCE = new AnySchema();
    
    
    private AnySchema() {
        // singleton
    }
    

    public static AnySchema instance() {
        return INSTANCE;
    }


    @Override
    public void writeTo(OutputStream out) {
        StreamUtil.write(out, FLAG);
    }

    @Override
    public Object readValueFrom(InputStream in) {
        Schema adHocSchema = Schema.readFrom(in);
        return adHocSchema.readValueFrom(in);
    }
    
    @Override
    public void writeValueTo(Object value, OutputStream out) {
        
        // TODO
        
        Schema adHocSchema = null; // TODO
        adHocSchema.writeTo(out);
        adHocSchema.writeValueTo(value, out);
    }
    
}
