package hu.webarticum.miniconnect.server.translator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.lang.ByteString;

final class TranslatorUtil {

    private TranslatorUtil() {
        // utility class
    }
    

    public static boolean readBoolean(ByteString.Reader reader) {
        return reader.read() != (byte) 0;
    }
    
    public static String readString(ByteString.Reader reader) {
        return new String(readSized(reader), StandardCharsets.UTF_8);
    }
    
    public static byte[] readSized(ByteString.Reader reader) {
        int length = reader.readInt();
        return reader.read(length);
    }

    public static byte encodeBoolean(boolean content) {
        return content ? (byte) 1 : (byte) 0;
    }

    public static byte[] encodeString(String content) {
        return encodeSized(content.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] encodeByteString(ByteString content) {
        int length = content.length();
        return ByteBuffer.allocate(Integer.BYTES + length)
                .putInt(length)
                .put(content.asBuffer())
                .array();
    }
    
    public static byte[] encodeSized(byte[] content) {
        return ByteBuffer.allocate(Integer.BYTES + content.length)
                .putInt(content.length)
                .put(content)
                .array();
    }
    
}
