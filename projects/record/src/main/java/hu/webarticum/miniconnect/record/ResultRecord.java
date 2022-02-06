package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class ResultRecord {
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<MiniValue> row;

    // TODO
    private final Object valueInterpreter;
    

    public ResultRecord(
            ImmutableList<MiniColumnHeader> columnHeaders,
            ImmutableList<MiniValue> row,
            Object valueInterpreter) {
        this.columnHeaders = columnHeaders;
        this.row = row;
        this.valueInterpreter = valueInterpreter;
    }
    
    
    public ResultField get(int zeroBasedIndex) {
        return new ResultField(row.get(zeroBasedIndex), valueInterpreter);
    }

    public ResultField get(String columnLabel) {
        int i = 0;
        for (MiniColumnHeader columnHeader : columnHeaders) {
            if (columnHeader.name().equals(columnLabel)) {
                return get(i);
            }
            i++;
        }
        throw new IllegalArgumentException("No such column: " + columnLabel);
    }

}
