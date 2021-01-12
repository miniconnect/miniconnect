package hu.webarticum.miniconnect.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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

        byte checkByte = (byte) in.read();
        
        int length = readInt();
        byte[] contentBytes = in.readNBytes(length);

        byte finalByte = (byte) in.read();

        if (finalByte != checkByte) {
            throw new IOException(String.format(
                    "Wrong final byte: %d, expected: %d",
                    finalByte, checkByte));
        }

        return new Block(new ByteString(contentBytes));
    }
    
    private int readInt() throws IOException {
        byte[] intBytes = in.readNBytes(Integer.BYTES);
        return ByteBuffer.wrap(intBytes).getInt();
    }
    
}
