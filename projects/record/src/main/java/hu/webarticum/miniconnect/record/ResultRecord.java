package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.converter.Converter;
import hu.webarticum.miniconnect.record.decoder.ValueDecoder;

public class ResultRecord {
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<MiniValue> row;
    
    private final ImmutableList<ValueDecoder> valueDecoders;
    
    private final Converter converter;
    

    public ResultRecord(
            ImmutableList<MiniColumnHeader> columnHeaders,
            ImmutableList<MiniValue> row,
            ImmutableList<ValueDecoder> valueDecoders,
            Converter converter) {
        this.columnHeaders = columnHeaders;
        this.row = row;
        this.valueDecoders = valueDecoders;
        this.converter = converter;
    }
    
    
    public ResultField get(int zeroBasedIndex) {
        MiniValue value = row.get(zeroBasedIndex);
        
        // TODO
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

}
