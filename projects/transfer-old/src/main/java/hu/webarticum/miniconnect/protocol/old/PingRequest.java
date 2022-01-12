package hu.webarticum.miniconnect.protocol.old;

import hu.webarticum.miniconnect.transfer.old.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class PingRequest implements SessionRequest {

    private static final Type TYPE = Request.Type.PING;
    
    
    private final int sessionId;

    
    public PingRequest(int sessionId) {
        this.sessionId = sessionId;
    }
    
    static PingRequest decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);

        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        
        return new PingRequest(sessionId);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .build();
    }

}
