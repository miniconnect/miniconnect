package hu.webarticum.miniconnect.server.message.request;

public class QueryRequest implements Request {

    private final int id;

    private final String query;

    private final long maxRowCount;


    public QueryRequest(int id, String query, long maxRowCount) {
        this.id = id;
        this.query = query;
        this.maxRowCount = maxRowCount;
    }


    public int id() {
        return id;
    }

    public String query() {
        return query;
    }

    public long maxRowCount() {
        return maxRowCount;
    }

}
