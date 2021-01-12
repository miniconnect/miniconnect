package hu.webarticum.miniconnect.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Block {
    
    public static final byte MAGIC_BYTE = 42;
    
    
    private final ByteString content;
    

    public Block(ByteString content) {
        this.content = content;
    }
    
    
    public static Block readFrom(InputStream in) throws IOException {
        return new BlockReader(in).read();
    }
    
    
    public ByteString content() {
        return content;
    }
    
    public void writeTo(OutputStream out) throws IOException {
        new BlockWriter(out).write(this);
    }
    
}
