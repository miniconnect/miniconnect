package hu.webarticum.miniconnect.protocol.old;

import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.tool.serialization.Serialization;
import hu.webarticum.miniconnect.transfer.old.util.ByteUtil;

public class ResultResponse implements SessionResponse {

    private static final Type TYPE = Response.Type.RESULT;


    private final int sessionId;

    private final int exchangeId;

    private final StoredResult storedResult; // XXX


    public ResultResponse(int sessionId, int exchangeId, StoredResult storedResult) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.storedResult = storedResult;
    }

    static ResultResponse decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);

        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        int exchangeId = ByteUtil.bytesToInt(reader.read(4));
        StoredResult storedResult = Serialization.deserialize(reader.readRemaining());

        return new ResultResponse(sessionId, exchangeId, storedResult);
    }


    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .append(ByteUtil.intToBytes(exchangeId))
                .append(Serialization.serialize(storedResult))
                .build();
    }

}
