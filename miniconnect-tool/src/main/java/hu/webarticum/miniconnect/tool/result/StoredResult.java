package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;

public class StoredResult implements MiniResult, Serializable {

    private static final long serialVersionUID = -8548902380126828570L;


    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final boolean hasResultSet;

    private final StoredResultSetData resultSetData;


    public StoredResult() {
        this(true, "", "", false, new StoredResultSetData());
    }

    public StoredResult(String errorCode, String errorMessage) {
        this(false, errorCode, errorMessage, false, new StoredResultSetData());
    }

    public StoredResult(StoredResultSetData resultSetData) {
        this(true, "", "", true, resultSetData);
    }

    public StoredResult(
            boolean success,
            String errorCode,
            String errorMessage,
            boolean hasResultSet,
            StoredResultSetData resultSetData) {

        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hasResultSet = hasResultSet;
        this.resultSetData = resultSetData;
    }

    public static StoredResult of(MiniResult result) throws IOException {
        return new StoredResult(
                result.success(),
                result.errorCode(),
                result.errorMessage(),
                result.hasResultSet(),
                StoredResultSetData.of(result));
    }


    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public List<String> warnings() {
        return new ArrayList<>();
    }

    @Override
    public boolean hasResultSet() {
        return hasResultSet;
    }

    @Override
    public MiniResultSet resultSet() {
        return new StoredResultSet(resultSetData);
    }

}
