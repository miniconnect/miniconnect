package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;

public class JavaSchema implements Schema {
    
    public static final byte FLAG = (byte) 'J';
    
    
    private static final JavaSchema INSTANCE = new JavaSchema();
    
    
    private JavaSchema() {
        // singleton
    }
    

    public static JavaSchema instance() {
        return INSTANCE;
    }

    
    @Override
    public void writeTo(OutputStream out) {
        StreamUtil.write(out, FLAG);
    }

    @Override
    public Object readValueFrom(InputStream in) {
        return StreamUtil.readObject(in);
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        StreamUtil.writeObject(out, value);
    }

}
