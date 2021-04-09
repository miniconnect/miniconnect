package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.util.data.ByteString;

public final class ResultSetValuePartResponse implements Response {

    private final int queryId;

    private final long rowIndex;

    private final int columnIndex;

    private final long offset;

    private final ByteString content;


    public ResultSetValuePartResponse(
            int queryId, long rowIndex, int columnIndex, long offset, ByteString content) {

        this.queryId = queryId;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.offset = offset;
        this.content = content;
    }


    public int queryId() {
        return queryId;
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
