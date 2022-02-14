package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.lang.ByteString;

public final class StreamUtil {
    
    private StreamUtil() {
        // utility class
    }
    

    public static byte read(InputStream in) {
        try {
            return new DataInputStream(in).readByte();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public static void write(OutputStream out, byte byteValue) {
        try {
            new DataOutputStream(out).writeByte(byteValue);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public static int readInt(InputStream in) {
        try {
            return new DataInputStream(in).readInt();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeInt(OutputStream out, int intValue) {
        try {
            new DataOutputStream(out).writeInt(intValue);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ByteString readFixedBytes(InputStream in, int length) {
        byte[] bytes = new byte[length];
        try {
            new DataInputStream(in).readFully(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ByteString.wrap(bytes);
    }

    public static ByteString readBytes(InputStream in) {
        int length = readInt(in);
        return readFixedBytes(in, length);
    }

    public static void writeFixedBytes(OutputStream out, ByteString bytes) {
        try {
            bytes.writeTo(out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeBytes(OutputStream out, ByteString bytes) {
        int length = bytes.length();
        writeInt(out, length);
        writeFixedBytes(out, bytes);
    }

    public static String readString(InputStream in) {
        ByteString bytes = readBytes(in);
        return bytes.toString(StandardCharsets.UTF_8);
    }

    public static void writeString(OutputStream out, String stringValue) {
        writeBytes(out, ByteString.of(stringValue));
    }
    
}
