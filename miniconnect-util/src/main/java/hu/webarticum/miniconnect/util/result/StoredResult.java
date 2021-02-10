package hu.webarticum.miniconnect.util.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.value.StoredColumnHeader;
import hu.webarticum.miniconnect.util.value.StoredValue;

public class StoredResult implements MiniResult, Serializable {
    
    private static final long serialVersionUID = 1L;
    

    private final boolean success;
    
    private final String errorMessage;
    
    private final StoredResultSetData resultSetData;
    

    public StoredResult() {
        this(true, "", new StoredResultSetData());
    }

    public StoredResult(String errorMessage) {
        this(false, errorMessage, new StoredResultSetData());
    }

    public StoredResult(StoredResultSetData resultSetData) {
        this(true, "", resultSetData);
    }
    
    public StoredResult(
            boolean success, String errorMessage, StoredResultSetData resultSetData) {
        
        this.success = success;
        this.errorMessage = errorMessage;
        this.resultSetData = resultSetData;
    }
    
    // XXX
    public static StoredResult of(MiniResult result) {
        return new StoredResult(
                result.isSuccess(),
                result.errorMessage(),
                dataOf(result));
    }
    
    private static StoredResultSetData dataOf(MiniResult result) {
        MiniResultSet resultSet = result.resultSet();
        
        List<MiniColumnHeader> storedColumnHeaders = new ArrayList<>();
        for (MiniColumnHeader columnHeader : resultSet.columnHeaders()) {
            storedColumnHeaders.add(StoredColumnHeader.of(columnHeader));
        }
        
        List<List<MiniValue>> storedRows = new ArrayList<>();
        for (List<MiniValue> row : resultSet) {
            List<MiniValue> storedRow = row.stream()
                    .map(StoredValue::of)
                    .collect(Collectors.toList());
            storedRows.add(storedRow);
        }
        
        return new StoredResultSetData(storedColumnHeaders, storedRows);
    }
    
    
    @Override
    public boolean isSuccess() {
        return success;
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
    public MiniResultSet resultSet() {
        return new StoredResultSet(resultSetData);
    }

}
