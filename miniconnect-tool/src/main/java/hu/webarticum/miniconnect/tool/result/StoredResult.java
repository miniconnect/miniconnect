package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class StoredResult implements MiniResult, Serializable {

    private static final long serialVersionUID = -8548902380126828570L;


    private final boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final ImmutableList<String> warnings;

    private final boolean hasResultSet;

    private final StoredResultSetData resultSetData;


    public StoredResult() {
        this(
                true, "", "", ImmutableList.empty(),
                false, new StoredResultSetData());
    }

    public StoredResult(String errorCode, String errorMessage) {
        this(
                false, errorCode, errorMessage, ImmutableList.empty(),
                false, new StoredResultSetData());
    }

    public StoredResult(StoredResultSetData resultSetData) {
        this(
                true, "", "", ImmutableList.empty(),
                true, resultSetData);
    }

    public StoredResult(
            boolean success,
            String errorCode,
            String errorMessage,
            ImmutableList<String> warnings,
            boolean hasResultSet,
            StoredResultSetData resultSetData) {

        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.warnings = warnings;
        this.hasResultSet = hasResultSet;
        this.resultSetData = resultSetData;
    }

    public static StoredResult of(MiniResult result) throws IOException {
        return new StoredResult(
                result.success(),
                result.errorCode(),
                result.errorMessage(),
                result.warnings(),
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
    public ImmutableList<String> warnings() {
        return warnings;
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
