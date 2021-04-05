package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class ResultResponse {

    private final int queryId;

    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final ImmutableList<String> warnings;

    private final boolean hasResultSet;

    private final ImmutableList<StoredColumnHeader> columnHeaders;


    public ResultResponse(
            int queryId,
            boolean success,
            String errorCode,
            String errorMessage,
            ImmutableList<String> warnings,
            boolean hasResultSet,
            ImmutableList<StoredColumnHeader> columnHeaders) {

        this.queryId = queryId;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.warnings = warnings;
        this.hasResultSet = hasResultSet;
        this.columnHeaders = columnHeaders;
    }


    public int getQueryId() {
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

    public ImmutableList<StoredColumnHeader> columnHeaders() {
        return columnHeaders;
    }

}
