package hu.webarticum.miniconnect.record;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.converter.Converter;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public class ResultRecord {
    
    private final ImmutableList<MiniColumnHeader> columnHeaders;

    private final ImmutableList<MiniValue> row;
    
    private final ImmutableList<ValueTranslator> valueTranslators;
    
    private final Converter converter;
    

    public ResultRecord(
            ImmutableList<MiniColumnHeader> columnHeaders,
            ImmutableList<MiniValue> row,
            ImmutableList<ValueTranslator> valueTranslators,
            Converter converter) {
        this.columnHeaders = columnHeaders;
        this.row = row;
        this.valueTranslators = valueTranslators;
        this.converter = converter;
    }
    
    
    public ResultField get(int zeroBasedIndex) {
        MiniValue value = row.get(zeroBasedIndex);
        ValueTranslator translator = valueTranslators.get(zeroBasedIndex);
        Object interpretedValue = value.isNull() ? null : translator.decode(value.contentAccess());
        return new ResultField(value, interpretedValue, converter);
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
