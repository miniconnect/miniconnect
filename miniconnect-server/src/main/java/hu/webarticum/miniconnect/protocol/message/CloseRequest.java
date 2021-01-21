package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;
import hu.webarticum.miniconnect.protocol.util.ByteUtil;

public class CloseRequest implements SessionRequest {

    private static final Type TYPE = Request.Type.CLOSE;
    
    
    private final int sessionId;

    
    public CloseRequest(int sessionId) {
        this.sessionId = sessionId;
    }
    
    static CloseRequest decode(ByteString content) {
        int sessionId = ByteUtil.bytesToInt(content.extract(1, 4));
        
        return new CloseRequest(sessionId);
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
        byte[] contentBytes = new byte[5];
        
        contentBytes[0] = TYPE.flag();
        
        byte[] sessionIdBytes = ByteUtil.intToBytes(sessionId);
        System.arraycopy(sessionIdBytes, 0, contentBytes, 1, sessionIdBytes.length);
        
        return ByteString.wrap(contentBytes);
    }

}
