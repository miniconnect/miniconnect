package hu.webarticum.miniconnect.messenger.adapter;

import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class MessengerResult implements MiniResult {

    private final boolean success;

    private final MiniError error;

    private final ImmutableList<MiniError> warnings;

    private final boolean hasResultSet;

    private final MessengerResultSetCharger charger;


    public MessengerResult(ResultResponse resultResponse, MessengerResultSetCharger charger) {
        ResultResponse.ErrorData errorData = resultResponse.error();
        this.success = resultResponse.success();
        this.error = new StoredError(errorData.code(), errorData.sqlState(), errorData.message());
        this.warnings = resultResponse.warnings().map(
                e -> new StoredError(e.code(), e.sqlState(), e.message()));
        this.hasResultSet = resultResponse.hasResultSet();
        this.charger = charger;
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
        return charger.resultSet();
    }

}
