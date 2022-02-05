package hu.webarticum.miniconnect.server;

import hu.webarticum.miniconnect.lang.ByteString;

public class HeaderEncoder {

    public ByteString encode(HeaderData headerData) {
        return ByteString.builder()
                .append((byte) headerData.messageType().symbol())
                .appendLong(headerData.sessionId())
                .appendInt(headerData.exchangeId())
                .build();
    }
    
}
