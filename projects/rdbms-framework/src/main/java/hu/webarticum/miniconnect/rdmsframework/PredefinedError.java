package hu.webarticum.miniconnect.rdmsframework;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniErrorException;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.impl.result.StoredError;
import hu.webarticum.miniconnect.impl.result.StoredResult;

public enum PredefinedError {
    
    NO_ERROR(0, "00000", ""),

    QUERY_TYPE_NOT_FOUND(1, "00001", "Unknown query type: '%s'"),
    
    TABLE_NOT_FOUND(2, "00002", "No such table: '%s'"),
    
    COLUMN_NOT_FOUND(3, "00003", "No column '%2$s' in table '%1$s'"),
    
    SCHEMA_NOT_FOUND(4, "00004", "No such schema: '%s'"),
    
    SCHEMA_NOT_SELECTED(5, "00005", "No schema is selected"),

    TABLE_READONLY(6, "00006", "Table is read-only: '%s'"),

    COLUMN_COUNT_NOT_MATCHING(7, "00007", "%d values expected, but %d given"),

    COLUMN_MISSING(8, "00008", "Missing column: '%s'"),
    
    COLUMN_POSITION_INVALID(9, "00009", "Invalid column position: %d"),
    
    TABLE_ALIAS_DUPLICATED(10, "00010", "Duplicated table alias: '%s'"),
    
    COLUMN_VALUE_NOT_UNIQUE(11, "00011", "Already existing value given for unique column '%s': '%s'"),
    
    COLUMN_VALUE_NULL(12, "00012", "NULL value for non nullable column '%s'"),
    
    QUERY_INTERRUPTED(99, "00099", "Query was interrupted"),
    
    OTHER_ERROR(100, "00100", "Unexpected error occured"),
    
    ;
    

    private final int code;
    
    private final String sqlState;
    
    private final String messageFormat;
    
    
    private PredefinedError(int code, String sqlState, String messageFormat) {
        this.code = code;
        this.sqlState = sqlState;
        this.messageFormat = messageFormat;
    }
    
    
    public int code() {
        return code;
    }

    public String sqlState() {
        return sqlState;
    }

    public String message(Object... parameters) {
        return parameters.length > 0 ? String.format(messageFormat, parameters) : messageFormat;
    }
    
    public MiniError toError(Object... messageParameters) {
        return new StoredError(code, sqlState, message(messageParameters));
    }

    public MiniResult toResult(Object... messageParameters) {
        return new StoredResult(toError(messageParameters));
    }

    public MiniErrorException toException(Object... messageParameters) {
        return new MiniErrorException(toError(messageParameters));
    }

}
