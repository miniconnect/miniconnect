package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public final class ResultResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int queryId;

    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final ImmutableList<String> warnings;

    private final boolean hasResultSet;

    private final ImmutableList<ColumnHeaderData> columnHeaders;


    public ResultResponse(
            long sessionId,
            int queryId,
            boolean success,
            String errorCode,
            String errorMessage,
            ImmutableList<String> warnings,
            boolean hasResultSet,
            ImmutableList<ColumnHeaderData> columnHeaders) {

        this.sessionId = sessionId;
        this.queryId = queryId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.warnings = warnings;
        this.hasResultSet = hasResultSet;
        this.columnHeaders = columnHeaders;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
    }

    public boolean success() {
        return success;
    }

    public String errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public ImmutableList<String> warnings() {
        return warnings;
    }

    public boolean hasResultSet() {
        return hasResultSet;
    }

    public ImmutableList<ColumnHeaderData> columnHeaders() {
        return columnHeaders;
    }


    public static class ColumnHeaderData {

        private final String name;

        private final String type;

        private final ImmutableMap<String, ByteString> properties;


        public ColumnHeaderData(
                String name,
                String type,
                ImmutableMap<String, ByteString> properties) {

            this.name = name;
            this.type = type;
            this.properties = properties;
        }


        public String name() {
            return name;
        }

        public String type() {
            return type;
        }

        public ImmutableMap<String, ByteString> properties() {
            return properties;
        }

    }

}
