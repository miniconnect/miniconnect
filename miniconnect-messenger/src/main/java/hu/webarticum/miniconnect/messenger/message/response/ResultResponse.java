package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.tool.result.StoredValueDefinition;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public final class ResultResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final boolean success;

    private final ErrorData error;

    private final ImmutableList<ErrorData> warnings;

    private final boolean hasResultSet;

    private final ImmutableList<ColumnHeaderData> columnHeaders;


    public ResultResponse(
            long sessionId,
            int exchangeId,
            boolean success,
            ErrorData error,
            ImmutableList<ErrorData> warnings,
            boolean hasResultSet,
            ImmutableList<ColumnHeaderData> columnHeaders) {

        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.success = success;
        this.error = error;
        this.warnings = warnings;
        this.hasResultSet = hasResultSet;
        this.columnHeaders = columnHeaders;
    }
    
    public static ResultResponse of(MiniResult result, long sessionId, int exchangeId) {
        MiniError error = result.error();
        return new ResultResponse(
                sessionId,
                exchangeId,
                result.success(),
                ErrorData.of(error),
                result.warnings().map(ErrorData::of),
                result.hasResultSet(),
                result.resultSet().columnHeaders().map(ResultResponse.ColumnHeaderData::of));
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    public boolean success() {
        return success;
    }

    public ErrorData error() {
        return error;
    }

    public ImmutableList<ErrorData> warnings() {
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
        
        public static ColumnHeaderData of(MiniColumnHeader header) {
            return new ColumnHeaderData(
                    header.name(),
                    header.valueDefinition().type(),
                    header.valueDefinition().properties());
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
        
        public MiniColumnHeader toMiniColumnHeader() {
            return new StoredColumnHeader(name, new StoredValueDefinition(type, properties));
        }

    }
    

    public static class ErrorData {

        private final int code;

        private final String sqlState;

        private final String message;

        
        public ErrorData(int code, String sqlState, String message) {
            this.code = code;
            this.sqlState = sqlState;
            this.message = message;
        }
        
        public static ErrorData of(MiniError error) {
            return new ErrorData(error.code(), error.sqlState(), error.message());
        }
        
        
        public int code() {
            return code;
        }

        public String sqlState() {
            return sqlState;
        }

        public String message() {
            return message;
        }

    }

}
