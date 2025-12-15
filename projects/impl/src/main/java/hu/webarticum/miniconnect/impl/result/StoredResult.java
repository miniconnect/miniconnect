package hu.webarticum.miniconnect.impl.result;

import java.io.IOException;
import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.lang.ImmutableList;

public final class StoredResult implements MiniResult, Serializable {

    private static final long serialVersionUID = 1L;


    private final boolean success;

    private final StoredError error;

    private final ImmutableList<StoredError> warnings;

    private final boolean hasResultSet;

    private final StoredResultSetData resultSetData;


    private StoredResult(
            boolean success,
            StoredError error,
            ImmutableList<StoredError> warnings,
            boolean hasResultSet,
            StoredResultSetData resultSetData) {
        this.success = success;
        this.error = error;
        this.warnings = warnings;
        this.hasResultSet = hasResultSet;
        this.resultSetData = resultSetData;
    }

    public static StoredResult of(
            boolean success,
            StoredError error,
            ImmutableList<StoredError> warnings,
            boolean hasResultSet,
            StoredResultSetData resultSetData) {
        return new StoredResult(success, error, warnings, hasResultSet, resultSetData);
    }

    public static StoredResult ofSuccess() {
        return of(true, StoredError.PLACEHOLDER, ImmutableList.empty(), false, StoredResultSetData.empty());
    }

    public static StoredResult ofError(StoredError error) {
        return of(false, error, ImmutableList.empty(), false, StoredResultSetData.empty());
    }

    public static StoredResult of(StoredResultSetData resultSetData) {
        return of(true, StoredError.PLACEHOLDER, ImmutableList.empty(), true, resultSetData);
    }

    public static StoredResult from(
            boolean success,
            MiniError error,
            ImmutableList<MiniError> warnings,
            boolean hasResultSet,
            MiniResultSet resultSet) throws IOException {
        return StoredResult.of(success, StoredError.from(error),
                warnings.map(w -> StoredError.from(w)),
                hasResultSet,
                StoredResultSetData.from(resultSet));
    }

    public static StoredResult from(MiniResult result) throws IOException {
        if (result instanceof StoredResult) {
            return (StoredResult) result;
        }

        return from(result.success(), result.error(), result.warnings(), result.hasResultSet(), result.resultSet());
    }

    public static StoredResult fromError(MiniError error) {
        return of(false, StoredError.from(error), ImmutableList.empty(), false, StoredResultSetData.empty());
    }


    @Override
    public boolean success() {
        return success;
    }

    @Override
    public MiniError error() {
        return error;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableList<MiniError> warnings() {
        return (ImmutableList<MiniError>) (ImmutableList<?>) warnings;
    }

    @Override
    public boolean hasResultSet() {
        return hasResultSet;
    }

    @Override
    public MiniResultSet resultSet() {
        return StoredResultSet.of(resultSetData);
    }

}
