package hu.webarticum.miniconnect.messenger.message.response;

import java.util.Objects;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ToStringBuilder;

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

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, rowIndex, columnIndex, offset, content);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ResultSetValuePartResponse)) {
            return false;
        }
        
        ResultSetValuePartResponse otherResultSetValuePartResponse =
                (ResultSetValuePartResponse) other;
        return
                sessionId == otherResultSetValuePartResponse.sessionId &&
                exchangeId == otherResultSetValuePartResponse.exchangeId &&
                rowIndex == otherResultSetValuePartResponse.rowIndex &&
                columnIndex == otherResultSetValuePartResponse.columnIndex &&
                offset == otherResultSetValuePartResponse.offset &&
                content.equals(otherResultSetValuePartResponse.content);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("rowIndex", rowIndex)
                .add("columnIndex", columnIndex)
                .add("offset", offset)
                .add("content", content.toArrayString())
                .build();
    }

}
