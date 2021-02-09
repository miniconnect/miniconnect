package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;
import hu.webarticum.miniconnect.protocol.util.ByteUtil;

public class ResultResponse implements SessionResponse {

    private static final Type TYPE = Response.Type.RESULT;
    
    
    private final int sessionId;
    
    // TODO
    
    
    public ResultResponse(int sessionId) {
        this.sessionId = sessionId;
    }
    
    static ResultResponse decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);

        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        
        return new ResultResponse(sessionId);
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