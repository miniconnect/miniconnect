package hu.webarticum.miniconnect.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
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
public final class ByteString implements Serializable {

    private static final long serialVersionUID = 2392643209772967829L;
    
    
    private static final ByteString EMPTY = new ByteString(new byte[0]);
    
    
    private final byte[] bytes;


    private ByteString(byte[] bytes) {
        Objects.requireNonNull(bytes);
        this.bytes = bytes;
    }

    public static ByteString empty() {
        return EMPTY;
    }

    public static ByteString of(byte[] bytes) {
        return of(bytes, 0, bytes.length);
    }

    public static ByteString of(byte[] bytes, int offset, int length) {
        byte[] partBytes = new byte[length];
        System.arraycopy(bytes, offset, partBytes, 0, length);
        return new ByteString(partBytes);
    }

    public static ByteString of(String string) {
        return of(string, StandardCharsets.UTF_8);
    }

    public static ByteString of(String string, Charset charset) {
        return wrap(string.getBytes(charset));
    }

    public static ByteString ofByte(byte b) {
        return wrap(new byte[] { b });
    }

    public static ByteString ofChar(char charValue) {
        return wrap(ByteBuffer.allocate(Character.BYTES).putChar(charValue).array());
    }

    public static ByteString ofShort(short shortValue) {
        return wrap(ByteBuffer.allocate(Short.BYTES).putShort(shortValue).array());
    }

    public static ByteString ofInt(int intValue) {
        return wrap(ByteBuffer.allocate(Integer.BYTES).putInt(intValue).array());
    }

    public static ByteString ofLong(long longValue) {
        return wrap(ByteBuffer.allocate(Long.BYTES).putLong(longValue).array());
    }

    public static ByteString ofFloat(float floatValue) {
        return wrap(ByteBuffer.allocate(Float.BYTES).putFloat(floatValue).array());
    }

    public static ByteString ofDouble(double doubleValue) {
        return wrap(ByteBuffer.allocate(Double.BYTES).putDouble(doubleValue).array());
    }

    public static ByteString fromInputStream(InputStream inputStream) {
        return fromInputStream(inputStream, 1024);
    }
    
    public static ByteString fromInputStream(InputStream inputStream, int size) {
        ByteArrayOutputStream resultBuilder = new ByteArrayOutputStream();

        int readLength;
        byte[] buffer = new byte[1024];
        try {
            while ((readLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
                resultBuilder.write(buffer, 0, readLength);
            }
            resultBuilder.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
        byte[] bytes = resultBuilder.toByteArray();
        return wrap(bytes);
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
        if (beginIndex == 0 && length == bytes.length) {
            return this;
        }
        
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
        if (length > 0) {
            System.arraycopy(bytes, beginIndex, extractedBytes, 0, length);
        }
        return extractedBytes;
    }

    private void checkBounds(int beginIndex, int length) {
        if (beginIndex < 0 || length < 0 || (beginIndex + length) > bytes.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid substring, beginIndex: %d, length: %d, content length: %d",
                    beginIndex, length, bytes.length));
        }
    }

    public void extractTo(byte[] target, int targetOffset, int selfOffset, int length) {
        System.arraycopy(bytes, selfOffset, target, targetOffset, length);
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes);
    }

    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
    }

    public ByteArrayInputStream inputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public ByteArrayInputStream inputStream(int offset, int length) {
        return new ByteArrayInputStream(bytes, offset, length);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ByteString)) {
            return false;
        }

        ByteString otherByteString = (ByteString) other;
        return Arrays.equals(bytes, otherByteString.bytes);
    }

    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        for (byte b : bytes) {
            if (isAsciiPrintable(b)) {
                resultBuilder.append((char) b);
            } else {
                resultBuilder.append('[');
                resultBuilder.append(toHexadecimalString(b));
                resultBuilder.append(']');
            }
        }
        return resultBuilder.toString();
    }
    
    private boolean isAsciiPrintable(byte b) {
        int intValue = Byte.toUnsignedInt(b);
        return intValue >= 32 && intValue <= 126;
    }
    
    private String toHexadecimalString(byte b) {
        String stringValue = Integer.toString(Byte.toUnsignedInt(b), 16);
        return stringValue.length() < 2 ? "0" + stringValue : stringValue;
    }

    public String toString(Charset charset) {
        return new String(bytes, charset);
    }

    public String toArrayString() {
        return Arrays.toString(bytes);
    }

    public Reader reader() {
        return new Reader();
    }


    public static class Builder {

        private final List<byte[]> parts = new ArrayList<>();
        
        private int length = 0;


        public Builder append(ByteString part, int beginIndex, int length) {
            return this.append(part.substringLength(beginIndex, length));
        }

        public Builder append(ByteString part) {
            return this.append(part.bytes);
        }

        public Builder append(byte part) {
            return this.append(new byte[] { part });
        }

        public Builder append(byte[] partContainer, int beginIndex, int length) {
            byte[] part = new byte[length];
            System.arraycopy(partContainer, beginIndex, part, 0, length);
            return append(part);
        }
        
        public Builder append(byte[] part) {
            parts.add(part);
            length += part.length;
            return this;
        }

        public Builder appendChar(char charValue) {
            return append(ByteBuffer.allocate(Character.BYTES).putChar(charValue).array());
        }

        public Builder appendShort(short shortValue) {
            return append(ByteBuffer.allocate(Short.BYTES).putShort(shortValue).array());
        }

        public Builder appendInt(int intValue) {
            return append(ByteBuffer.allocate(Integer.BYTES).putInt(intValue).array());
        }

        public Builder appendLong(long longValue) {
            return append(ByteBuffer.allocate(Long.BYTES).putLong(longValue).array());
        }

        public Builder appendFloat(float floatValue) {
            return append(ByteBuffer.allocate(Float.BYTES).putFloat(floatValue).array());
        }

        public Builder appendDouble(double doubleValue) {
            return append(ByteBuffer.allocate(Double.BYTES).putDouble(doubleValue).array());
        }

        public int length() {
            return length;
        }

        public ByteString build() {
            if (parts.size() == 1) {
                return ByteString.wrap(parts.get(0));
            }
            
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

        public char readChar() {
            return ByteBuffer.wrap(read(Character.BYTES)).getChar();
        }

        public short readShort() {
            return ByteBuffer.wrap(read(Short.BYTES)).getShort();
        }

        public int readInt() {
            return ByteBuffer.wrap(read(Integer.BYTES)).getInt();
        }

        public long readLong() {
            return ByteBuffer.wrap(read(Long.BYTES)).getLong();
        }

        public float readFloat() {
            return ByteBuffer.wrap(read(Float.BYTES)).getFloat();
        }

        public double readDouble() {
            return ByteBuffer.wrap(read(Double.BYTES)).getDouble();
        }

    }

}
