package hu.webarticum.miniconnect.transfer.old.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.transfer.old.Block;
import hu.webarticum.miniconnect.util.data.ByteString;

public class BlockReader {

    private final InputStream in;

    public BlockReader(InputStream in) {
        this.in = in;
    }
    
    public Block read() throws IOException {
        byte magicByte = (byte) in.read();
        
        if (magicByte != Block.MAGIC_BYTE) {
            throw new IOException(String.format(
                    "Invalid magic byte: %d",
                    magicByte));
        }

        int length = readInt();
        byte[] contentBytes = in.readNBytes(length);

        return Block.dataOf(ByteString.wrap(contentBytes));
    }
    
    private int readInt() throws IOException {
        byte[] intBytes = in.readNBytes(Integer.BYTES);
        return ByteBuffer.wrap(intBytes).getInt();
    }
    
}
