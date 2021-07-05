package hu.webarticum.miniconnect.messenger.surface;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class MessengerResult implements MiniResult {

    private final boolean success;

    private final String sqlState;

    private final String errorCode;

    private final String errorMessage;

    private final ImmutableList<String> warnings;

    private final boolean hasResultSet;

    private final MessengerResultSetCharger charger;


    public MessengerResult(ResultResponse resultResponse, MessengerResultSetCharger charger) {
        this.success = resultResponse.success();
        this.sqlState = resultResponse.sqlState();
        this.errorCode = resultResponse.errorCode();
        this.errorMessage = resultResponse.errorMessage();
        this.warnings = resultResponse.warnings();
        this.hasResultSet = resultResponse.hasResultSet();
        this.charger = charger;
    }


    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String sqlState() {
        return sqlState;
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
        return charger.resultSet();
    }

}
