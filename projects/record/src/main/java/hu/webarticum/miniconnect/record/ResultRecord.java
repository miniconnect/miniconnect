package hu.webarticum.miniconnect.record;

import java.util.function.BiFunction;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class ResultRecord {
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<MiniValue> row;

    private final ValueInterpreterSupplier valueInterpreterSupplier;
    

    public ResultRecord(
            ImmutableList<MiniColumnHeader> columnHeaders,
            ImmutableList<MiniValue> row,
            Object valueInterpreter) {
        this(columnHeaders, row, (i, h) -> valueInterpreter);
    }

    public ResultRecord(
            ImmutableList<MiniColumnHeader> columnHeaders,
            ImmutableList<MiniValue> row,
            ValueInterpreterSupplier valueInterpreterSupplier) {
        this.columnHeaders = columnHeaders;
        this.row = row;
        this.valueInterpreterSupplier = valueInterpreterSupplier;
    }
    
    
    public ResultField get(int zeroBasedIndex) {
        MiniValue value = row.get(zeroBasedIndex);
        MiniColumnHeader columnHeader = columnHeaders.get(zeroBasedIndex);
        
        // FIXME
        Object valueInterpreter = valueInterpreterSupplier.get(zeroBasedIndex, columnHeader);

        // FIXME
        BiFunction<Object, Class<?>, Object> converter =
                (v, c) -> {
                    throw new UnsupportedOperationException("Conversion is not implemented yet");
                };
        
        // FIXME
        return new ResultField(
                value, String.class, value.contentAccess().get().toString(), converter);
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

    
    @FunctionalInterface
    public interface ValueInterpreterSupplier {
        
        public Object get(int columnIndex, MiniColumnHeader columnHeader);
        
    }
    
}
