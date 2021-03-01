package hu.webarticum.miniconnect.transfer.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteString;

public class BlockReader {

    private final InputStream in;

    public BlockReader(InputStream in) {
        this.in = in;
    }
    
    public Block read() throws IOException {
        byte firstByte = (byte) in.read();
        
        if (firstByte != Block.MAGIC_BYTE) {
            throw new IOException(String.format(
                    "Invalid first byte: %d",
                    firstByte));
        }

        int length = readInt();
        byte[] contentBytes = in.readNBytes(length);

        return new Block(ByteString.wrap(contentBytes));
    }
    
    private int readInt() throws IOException {
        byte[] intBytes = in.readNBytes(Integer.BYTES);
        return ByteBuffer.wrap(intBytes).getInt();
    }
    
}
