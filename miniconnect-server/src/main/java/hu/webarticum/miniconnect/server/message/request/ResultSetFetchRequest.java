package hu.webarticum.miniconnect.server.message.request;

public final class ResultSetFetchRequest implements Request {

    private final int queryId;

    private final long maxRowCount;


    public ResultSetFetchRequest(int queryId, long maxRowCount) {
        this.queryId = queryId;
        this.maxRowCount = maxRowCount;
    }


    public int queryId() {
        return queryId;
    }

    public long maxRowCount() {
        return maxRowCount;
    }

}
