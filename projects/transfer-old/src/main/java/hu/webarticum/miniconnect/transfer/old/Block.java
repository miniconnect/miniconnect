package hu.webarticum.miniconnect.transfer.old;

import hu.webarticum.miniconnect.util.data.ByteString;

public class Block {
    
    public static final byte MAGIC_BYTE = 42;
    

    private final BlockHeader header;
    
    private final ByteString content;
    

    public Block(BlockHeader header, ByteString content) {
        this.header = header;
        this.content = content;
    }

    public static Block dataOf(ByteString content) {
        return new Block(new BlockHeader(), content);
    }
    

    public BlockHeader header() {
        return header;
    }
    
    public ByteString content() {
        return content;
    }

}
