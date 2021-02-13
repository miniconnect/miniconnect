package hu.webarticum.miniconnect.protocol.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.common.ByteString;

public class BlockWriter {

    private final OutputStream out;

    public BlockWriter(OutputStream out) {
        this.out = out;
    }
    
    public void write(Block block) throws IOException {
        ByteString content = block.content();
        int length = content.length();
        byte[] lengthBytes = ByteBuffer.allocate(Integer.BYTES).putInt(length).array();
        byte checkByte = (byte) (lengthBytes[0] ^ lengthBytes[1] ^ lengthBytes[2] ^ lengthBytes[3]);
        
        out.write(Block.MAGIC_BYTE);
        out.write(checkByte);
        out.write(lengthBytes);
        content.writeTo(out);
        out.write(checkByte);
        out.flush();
    }

}
