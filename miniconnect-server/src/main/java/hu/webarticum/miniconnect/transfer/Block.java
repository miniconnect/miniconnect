package hu.webarticum.miniconnect.transfer;

import hu.webarticum.miniconnect.transfer.util.ByteString;

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
