package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class ResultRecord {

    private final ImmutableList<MiniValue> row;
    

    // TODO: value interpreter
    private ResultRecord(ImmutableList<MiniValue> row) {
        this.row = row;
    }
    
    public static ResultRecord of(ImmutableList<MiniValue> row) {
        return new ResultRecord(row);
    }
    
    
    public ResultField get(int zeroBasedIndex) {
        return ResultField.of(row.get(zeroBasedIndex));
    }

    // TODO: by label (headers?)
    
}
