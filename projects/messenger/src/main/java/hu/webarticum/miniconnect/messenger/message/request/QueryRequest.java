package hu.webarticum.miniconnect.messenger.message.request;

import java.util.Objects;

import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.util.data.ToStringBuilder;

public final class QueryRequest implements Request, ExchangeMessage {

    private final long sessionId;

    // FIXME: int? (long? String? byte[]?)
    private final int exchangeId;

    private final String query;


    public QueryRequest(long sessionId, int exchangeId, String query) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.query = Objects.requireNonNull(query);
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int exchangeId() {
        return exchangeId;
    }

    public String query() {
        return query;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, query);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof QueryRequest)) {
            return false;
        }
        
        QueryRequest otherQueryRequest = (QueryRequest) other;
        return
                sessionId == otherQueryRequest.sessionId &&
                exchangeId == otherQueryRequest.exchangeId &&
                query.equals(otherQueryRequest.query);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("query", query)
                .build();
    }

}
