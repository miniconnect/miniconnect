package hu.webarticum.miniconnect.server.message.response;

public class ResultSetRowsResponse {

    private final int queryId;

    private final long rowOffset;

    // TODO: data of rows, including length of incomplete values


    public ResultSetRowsResponse(int queryId, long rowOffset) {
        this.queryId = queryId;
        this.rowOffset = rowOffset;
    }


    public int queryId() {
        return queryId;
    }

    public long rowOffset() {
        return rowOffset;
    }

}
