package hu.webarticum.miniconnect.util.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Simple immutable wrapper for byte arrays
 */
public class ByteString implements Serializable {

    private static final long serialVersionUID = 3831893275159407967L;


    private final byte[] bytes;


    private ByteString(byte[] bytes) {
        Objects.requireNonNull(bytes);
        this.bytes = bytes;
    }

    public static ByteString empty() {
        return new ByteString(new byte[0]);
    }

    public static ByteString of(byte[] bytes) {
        return new ByteString(Arrays.copyOf(bytes, bytes.length));
    }

    public static ByteString wrap(byte[] bytes) {
        return new ByteString(bytes);
    }

    public static Builder builder() {
        return new Builder();
    }


    public boolean isEmpty() {
        return (bytes.length == 0);
    }

    public int length() {
        return bytes.length;
    }

    public byte byteAt(int position) {
        return bytes[position];
    }
    
    public ByteString substring(int beginIndex) {
        return substring(beginIndex, bytes.length);
    }

    public ByteString substring(int beginIndex, int endIndex) {
        return substringLength(beginIndex, endIndex - beginIndex);
    }

    public ByteString substringLength(int beginIndex, int length) {
        return ByteString.wrap(extractLength(beginIndex, length));
    }

    public byte[] extract() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] extract(int beginIndex) {
        return extract(beginIndex, bytes.length);
    }
    
    public byte[] extract(int beginIndex, int endIndex) {
        return extractLength(beginIndex, endIndex - beginIndex);
    }

    public byte[] extractLength(int beginIndex, int length) {
        checkBounds(beginIndex, length);
        byte[] extractedBytes = new byte[length];
        System.arraycopy(bytes, beginIndex, extractedBytes, 0, length);
        return extractedBytes;
    }

    public void extractTo(byte[] target, int targetOffset, int selfOffset, int length) {
        System.arraycopy(bytes, selfOffset, target, targetOffset, length);
    }

    private void checkBounds(int beginIndex, int length) {
        if (beginIndex < 0 || length <= 0 || (beginIndex + length) > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid substring, beginIndex: %d, length: %d, content length: %d",
                    beginIndex, length, bytes.length));
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes);
    }

    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
    }

    public ByteArrayInputStream asInputStream() {
        return new ByteArrayInputStream(bytes);
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
        return toString(StandardCharsets.UTF_8);
    }

    public String toString(Charset charset) {
        return new String(bytes, charset);
    }

    public Reader reader() {
        return new Reader();
    }


    public static class Builder {

        private final List<byte[]> parts = new ArrayList<>();
        
        private int length = 0;


        public Builder append(ByteString part) {
            return this.append(part.bytes);
        }

        public Builder append(byte part) {
            return this.append(new byte[] { part });
        }

        public Builder append(byte[] part) {
            parts.add(part);
            length += part.length;
            return this;
        }
        
        public int length() {
            return length;
        }

        public ByteString build() {
            byte[] bytes = new byte[length];
            int position = 0;
            for (byte[] part : parts) {
                System.arraycopy(part, 0, bytes, position, part.length);
                position += part.length;
            }

            return ByteString.wrap(bytes);
        }

    }


    public class Reader {

        private int position = 0;

        public Reader skip(int length) {
            position += length;
            return this;
        }

        public byte read() {
            byte result = byteAt(position);
            position++;
            return result;
        }

        public byte[] read(int length) {
            byte[] result = extractLength(position, length);
            position += length;
            return result;
        }

        public byte[] readRemaining() {
            return read(bytes.length - position);
        }

    }

}
