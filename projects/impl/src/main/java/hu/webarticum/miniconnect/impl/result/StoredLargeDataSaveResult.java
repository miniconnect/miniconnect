package hu.webarticum.miniconnect.impl.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;

public final class StoredLargeDataSaveResult implements MiniLargeDataSaveResult, Serializable {

    private static final long serialVersionUID = 1L;


    private final boolean success;

    private final StoredError error;


    private StoredLargeDataSaveResult(boolean success, StoredError error) {
        this.success = success;
        this.error = error;
    }

    public static StoredLargeDataSaveResult of(boolean success, StoredError error) {
        return new StoredLargeDataSaveResult(success, error);
    }

    public static StoredLargeDataSaveResult ofSuccess() {
        return of(true, StoredError.PLACEHOLDER);
    }

    public static StoredLargeDataSaveResult ofError(StoredError error) {
        return new StoredLargeDataSaveResult(false, error);
    }

    public static StoredLargeDataSaveResult fromError(MiniError error) {
        return of(false, StoredError.from(error));
    }


    @Override
    public boolean success() {
        return success;
    }

    @Override
    public MiniError error() {
        return error;
    }

}
