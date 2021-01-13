package hu.webarticum.miniconnect.protocol.block;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public class Block {
    
    public static final byte MAGIC_BYTE = 42;
    
    
    private final ByteString content;
    

    public Block(ByteString content) {
        this.content = content;
    }
    
    
    public ByteString content() {
        return content;
    }

}
