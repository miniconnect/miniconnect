package hu.webarticum.miniconnect.transfer.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteString;

public class BlockWriter {

    private final OutputStream out;

    public BlockWriter(OutputStream out) {
        this.out = out;
    }
    
    public void write(Block block) throws IOException {
        ByteString content = block.content();
        int length = content.length();
        byte[] lengthBytes = ByteBuffer.allocate(Integer.BYTES).putInt(length).array();
        
        out.write(Block.MAGIC_BYTE);
        out.write(lengthBytes);
        content.writeTo(out);
        out.flush();
    }

}
