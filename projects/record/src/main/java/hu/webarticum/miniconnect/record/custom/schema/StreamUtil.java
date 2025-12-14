package hu.webarticum.miniconnect.record.custom.schema;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.lang.ByteString;

public final class StreamUtil {

    private StreamUtil() {
        // utility class
    }


    public static byte read(InputStream in) {
        return (byte) readUnsigned(in);
    }

    private static int readUnsigned(InputStream in) {
        int b;
        try {
            b = in.read();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (b == -1) {
            throw new UncheckedIOException(new EOFException());
        }
        return b;
    }

    public static void write(OutputStream out, byte byteValue) {
        try {
            new DataOutputStream(out).writeByte(byteValue);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static int readInt(InputStream in) {
        int b1 = readUnsigned(in);
        int b2 = readUnsigned(in);
        int b3 = readUnsigned(in);
        int b4 = readUnsigned(in);
        return ((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
    }

    public static void writeInt(OutputStream out, int intValue) {
        try {
            new DataOutputStream(out).writeInt(intValue);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ByteString readFixedBytes(InputStream in, int length) {
        byte[] byteArray;
        try {
            byteArray = readByteArray(in, length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ByteString.wrap(byteArray);
    }

    private static byte[] readByteArray(InputStream in, int length) throws IOException {
        byte[] result = new byte[length];
        int pos = 0;
        while (pos < length) {
            int count = in.read(result, pos, length - pos);
            if (count < 0) {
                throw new EOFException();
            }
            pos += count;
        }
        return result;
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

    public static Object readObject(InputStream in) {
        ByteString bytes = StreamUtil.readBytes(in);
        try {
            return new ObjectInputStream(bytes.inputStream()).readObject();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeObject(OutputStream out, Object value) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(buffer).writeObject(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        ByteString bytes = ByteString.wrap(buffer.toByteArray());
        writeBytes(out, bytes);
    }

}
