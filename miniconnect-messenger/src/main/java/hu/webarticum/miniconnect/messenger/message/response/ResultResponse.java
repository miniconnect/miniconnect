package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
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

    private final String errorCode;

    private final String errorMessage;

    private final ImmutableList<String> warnings;

    private final boolean hasResultSet;

    private final ImmutableList<ColumnHeaderData> columnHeaders;


    // TODO: builder
    public ResultResponse(
            long sessionId,
            int exchangeId,
            boolean success,
            String errorCode,
            String errorMessage,
            ImmutableList<String> warnings,
            boolean hasResultSet,
            ImmutableList<ColumnHeaderData> columnHeaders) {

        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
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

    public int exchangeId() {
        return exchangeId;
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


        public ColumnHeaderData(MiniColumnHeader header) {
            this(
                    header.name(),
                    header.valueDefinition().type(),
                    header.valueDefinition().properties());
        }
        
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
        
        public MiniColumnHeader toMiniColumnHeader() {
            return new StoredColumnHeader(name, new StoredValueDefinition(type, properties));
        }

    }

}
