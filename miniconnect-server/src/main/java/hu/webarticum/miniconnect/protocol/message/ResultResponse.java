package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;
import hu.webarticum.miniconnect.protocol.util.ByteUtil;
import hu.webarticum.miniconnect.util.result.StoredResult;
import serialization.Serialization;

public class ResultResponse implements SessionResponse {

    private static final Type TYPE = Response.Type.RESULT;
    
    
    private final int sessionId;

    private final int queryId;
    
    private final StoredResult storedResult; // XXX
    
    
    public ResultResponse(int sessionId, int queryId, StoredResult storedResult) {
        this.sessionId = sessionId;
        this.queryId = queryId;
        this.storedResult = storedResult;
    }
    
    static ResultResponse decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);

        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        int queryId = ByteUtil.bytesToInt(reader.read(4));
        StoredResult storedResult = Serialization.deserialize(reader.readRemaining());
        
        return new ResultResponse(sessionId, queryId, storedResult);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .append(ByteUtil.intToBytes(queryId))
                .append(Serialization.serialize(storedResult))
                .build();
    }

}
