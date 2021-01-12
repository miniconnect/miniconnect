package hu.webarticum.miniconnect.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * Simple immutable wrapper for byte arrays
 */
public class ByteString {
    
    private final byte[] bytes;
    

    public ByteString(byte[] bytes) {
        Objects.requireNonNull(bytes);
        this.bytes = bytes;
    }
    

    public int length() {
        return bytes.length;
    }
    
    public byte byteAt(int position) {
        return bytes[position];
    }

    public byte[] extract() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] extract(int position, int length) {
        checkExtraction(position, length);
        byte[] extractedBytes = new byte[length];
        System.arraycopy(bytes, position, extractedBytes, 0, length);
        return extractedBytes;
    }
    
    private void checkExtraction(int position, int length) {
        if (position < 0 || length <= 0 || (position + length) > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid extraction, position: %d, length: %d, array length: %d",
                    position, length, bytes.length));
        }
    }

    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteString)) {
            return false;
        }
        
        ByteString otherByteString = (ByteString) other;
        return Arrays.equals(bytes, otherByteString.bytes);
    }
    
    @Override
    public String toString() {
        return toString(Charset.defaultCharset());
    }

    public String toString(Charset charset) {
        return new String(bytes, charset);
    }
    
}
