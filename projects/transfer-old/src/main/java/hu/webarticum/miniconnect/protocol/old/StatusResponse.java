package hu.webarticum.miniconnect.protocol.old;

import hu.webarticum.miniconnect.transfer.old.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class StatusResponse implements SessionResponse {

    private static final Type TYPE = Response.Type.STATUS;
    
    
    public enum Status {
        
        CONNECTED, CLOSED;
        
        
        public static Status of(byte flag) {
            int typeIndex = Byte.toUnsignedInt(flag);
            Status[] types = Status.values();
            if (typeIndex >= types.length) {
                throw new IllegalArgumentException(String.format(
                        "Unknown status index: %d",
                        typeIndex));
            }
            
            return types[typeIndex];
        }
        

        public byte flag() {
            return (byte) ordinal();
        }

    }
    
    
    private final int sessionId;
    
    private final Status status;

    
    public StatusResponse(int sessionId, Status status) {
        this.sessionId = sessionId;
        this.status = status;
    }
    
    static StatusResponse decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);

        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        Status status = Status.of(reader.read());
        
        return new StatusResponse(sessionId, status);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    public Status status() {
        return status;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .append(status.flag())
                .build();
    }

}
