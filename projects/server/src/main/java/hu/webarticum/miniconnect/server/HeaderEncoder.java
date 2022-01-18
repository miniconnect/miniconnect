package hu.webarticum.miniconnect.server;

import java.nio.ByteBuffer;

import hu.webarticum.miniconnect.util.data.ByteString;

public class HeaderEncoder {

    public ByteString encode(HeaderData headerData) {
        int length = (Integer.BYTES * 2) + Long.BYTES;
        byte[] bytes = ByteBuffer.allocate(length)
                .put((byte) headerData.messageType().symbol())
                .putLong(headerData.sessionId())
                .putInt(headerData.exchangeId())
                .array();
        return ByteString.wrap(bytes);
    }
    
}
