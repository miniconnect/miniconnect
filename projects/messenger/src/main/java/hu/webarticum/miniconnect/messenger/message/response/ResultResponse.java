package hu.webarticum.miniconnect.messenger.message.response;

import java.util.Objects;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.tool.result.StoredValueDefinition;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;
import hu.webarticum.miniconnect.util.data.ToStringBuilder;

public final class ResultResponse implements Response, ExchangeMessage {

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
        this.error = Objects.requireNonNull(error);
        this.warnings = Objects.requireNonNull(warnings);
        this.hasResultSet = hasResultSet;
        this.columnHeaders = Objects.requireNonNull(columnHeaders);
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

    @Override
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

    @Override
    public int hashCode() {
        return Objects.hash(
                sessionId, exchangeId, success, error, warnings, hasResultSet, columnHeaders);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ResultResponse)) {
            return false;
        }
        
        ResultResponse otherResultResponse = (ResultResponse) other;
        return
                sessionId == otherResultResponse.sessionId &&
                exchangeId == otherResultResponse.exchangeId &&
                success == otherResultResponse.success &&
                error.equals(otherResultResponse.error) &&
                warnings.equals(otherResultResponse.warnings) &&
                hasResultSet == otherResultResponse.hasResultSet &&
                columnHeaders.equals(otherResultResponse.columnHeaders);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("success", success)
                .add("error", error)
                .add("warnings", warnings)
                .add("hasResultSet", hasResultSet)
                .add("columnHeaders", columnHeaders)
                .build();
    }


    public static class ColumnHeaderData {

        private final String name;

        private final boolean isNullable;

        private final String type;

        private final ImmutableMap<String, ByteString> properties;


        public ColumnHeaderData(
                String name,
                boolean isNullable,
                String type,
                ImmutableMap<String, ByteString> properties) {
            this.name = Objects.requireNonNull(name);
            this.isNullable = isNullable;
            this.type = Objects.requireNonNull(type);
            this.properties = Objects.requireNonNull(properties);
        }
        
        public static ColumnHeaderData of(MiniColumnHeader header) {
            return new ColumnHeaderData(
                    header.name(),
                    header.isNullable(),
                    header.valueDefinition().type(),
                    header.valueDefinition().properties());
        }


        public String name() {
            return name;
        }

        public boolean isNullable() {
            return isNullable;
        }

        public String type() {
            return type;
        }

        public ImmutableMap<String, ByteString> properties() {
            return properties;
        }
        
        public MiniColumnHeader toMiniColumnHeader() {
            return new StoredColumnHeader(
                    name, isNullable, new StoredValueDefinition(type, properties));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, isNullable, type, properties);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof ColumnHeaderData)) {
                return false;
            }
            
            ColumnHeaderData otherColumnHeaderData = (ColumnHeaderData) other;
            return
                    name.equals(otherColumnHeaderData.name) &&
                    isNullable == otherColumnHeaderData.isNullable &&
                    type.equals(otherColumnHeaderData.type) &&
                    properties.equals(otherColumnHeaderData.properties);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("isNullable", isNullable)
                    .add("type", type)
                    .add("properties", properties)
                    .build();
        }

    }
    

    public static class ErrorData {

        private final int code;

        private final String sqlState;

        private final String message;

        
        public ErrorData(int code, String sqlState, String message) {
            this.code = code;
            this.sqlState = Objects.requireNonNull(sqlState);
            this.message = Objects.requireNonNull(message);
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

        @Override
        public int hashCode() {
            return Objects.hash(code, sqlState, message);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof ErrorData)) {
                return false;
            }
            
            ErrorData otherErrorData = (ErrorData) other;
            return
                    code == otherErrorData.code &&
                    sqlState.equals(otherErrorData.sqlState) &&
                    message.equals(otherErrorData.message);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("code", code)
                    .add("sqlState", sqlState)
                    .add("message", message)
                    .build();
        }

    }

}
