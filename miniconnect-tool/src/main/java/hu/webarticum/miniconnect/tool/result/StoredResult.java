package hu.webarticum.miniconnect.tool.result;

import java.io.IOException;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class StoredResult implements MiniResult, Serializable {

    private static final long serialVersionUID = 1L;


    private final boolean success;

    private final StoredError error;

    private final ImmutableList<MiniError> warnings;

    private final boolean hasResultSet;

    private final StoredResultSetData resultSetData;


    public StoredResult() {
        this(
                true, StoredError.PLACEHOLDER, ImmutableList.empty(),
                false, new StoredResultSetData());
    }

    public StoredResult(MiniError error) {
        this(
                false, StoredError.of(error), ImmutableList.empty(),
                false, new StoredResultSetData());
    }

    public StoredResult(StoredResultSetData resultSetData) {
        this(
                true, StoredError.PLACEHOLDER, ImmutableList.empty(),
                true, resultSetData);
    }

    public StoredResult(
            boolean success,
            StoredError error,
            ImmutableList<StoredError> warnings,
            boolean hasResultSet,
            StoredResultSetData resultSetData) {

        this.success = success;
        this.error = error;
        this.warnings = warnings.map(e -> e);
        this.hasResultSet = hasResultSet;
        this.resultSetData = resultSetData;
    }

    public static StoredResult of(MiniResult result) throws IOException {
        if (result instanceof StoredResult) {
            return (StoredResult) result;
        }
        
        return new StoredResult(
                result.success(),
                StoredError.of(result.error()),
                result.warnings().map(StoredError::of),
                result.hasResultSet(),
                StoredResultSetData.of(result));
    }


    @Override
    public boolean success() {
        return success;
    }

    @Override
    public MiniError error() {
        return error;
    }

    @Override
    public ImmutableList<MiniError> warnings() {
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
