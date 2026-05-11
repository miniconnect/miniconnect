package hu.webarticum.miniconnect.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
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


    public ImmutableList<MiniValue> row() {
        return row;
    }

    @JsonValue
    public ImmutableMap<String, Object> rowMap() {
        Map<String, Object> resultBuilder = new HashMap<>();
        int rowSize = columnHeaders.size();
        for (int i = 0; i < rowSize; i++) {
            String columnLabel = columnHeaders.get(i).name();
            Object value = value(i);
            resultBuilder.put(columnLabel, value);
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    public ImmutableList<ResultField> getAll() {
        int size = row.size();
        List<ResultField> resultBuilder = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            resultBuilder.add(get(i));
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    public ResultField get(int zeroBasedIndex) {
        MiniValue value = row.get(zeroBasedIndex);
        ValueTranslator translator = valueTranslators.get(zeroBasedIndex);
        Object interpretedValue = value.isNull() ? null : translator.decode(value.contentAccess());
        return new ResultField(value, interpretedValue, converter);
    }

    public ResultField get(String columnLabel) {
        return get(indexOfColumnLabel(columnLabel));
    }

    public ImmutableList<Object> values() {
        int size = row.size();
        List<Object> resultBuilder = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            resultBuilder.add(value(i));
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    public Object value(int zeroBasedIndex) {
        MiniValue value = row.get(zeroBasedIndex);
        ValueTranslator translator = valueTranslators.get(zeroBasedIndex);
        return value.isNull() ? null : translator.decode(value.contentAccess());
    }

    public Object value(String columnLabel) {
        return value(indexOfColumnLabel(columnLabel));
    }

    private int indexOfColumnLabel(String columnLabel) {
        int i = 0;
        for (MiniColumnHeader columnHeader : columnHeaders) {
            if (columnHeader.name().equals(columnLabel)) {
                return i;
            }
            i++;
        }
        throw new IllegalArgumentException("No such column: " + columnLabel);
    }


}
