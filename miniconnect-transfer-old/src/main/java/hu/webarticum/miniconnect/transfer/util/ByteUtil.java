package hu.webarticum.miniconnect.transfer.util;

import java.nio.ByteBuffer;

public final class ByteUtil {

    private ByteUtil() {
    }

    
    public static byte[] intToBytes(int number) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(number);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getInt();
    }
    
}
