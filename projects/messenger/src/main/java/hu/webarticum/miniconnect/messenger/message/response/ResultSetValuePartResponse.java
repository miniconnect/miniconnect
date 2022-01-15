package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.data.ByteString;

public final class ResultSetValuePartResponse implements Response, ExchangeMessage {

    private final long sessionId;

    private final int exchangeId;

    private final long rowIndex;

    private final int columnIndex;

    private final long offset;

    private final ByteString content;


    public ResultSetValuePartResponse(
            long sessionId,
            int exchangeId,
            long rowIndex,
            int columnIndex,
            long offset,
            ByteString content) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.offset = offset;
        this.content = content;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    public long rowIndex() {
        return rowIndex;
    }

    public int columnIndex() {
        return columnIndex;
    }

    public long offset() {
        return offset;
    }

    public ByteString content() {
        return content;
    }

}
