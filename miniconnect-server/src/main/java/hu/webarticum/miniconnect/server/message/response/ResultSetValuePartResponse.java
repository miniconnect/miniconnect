package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;

public final class ResultSetValuePartResponse implements Response, SessionMessage {

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
