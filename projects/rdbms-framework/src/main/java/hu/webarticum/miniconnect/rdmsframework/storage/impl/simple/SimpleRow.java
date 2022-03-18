package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.HashMap;
import java.util.Map;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Row;

public class SimpleRow implements Row {
    
    private final ImmutableList<String> columnNames;
    
    ImmutableList<Object> values;
    
    
    public SimpleRow(ImmutableList<String> columnNames, ImmutableList<Object> values) {
        this.columnNames = columnNames;
        this.values = values;
    }
    

    @Override
    public ImmutableList<String> columnNames() {
        return columnNames;
    }

    @Override
    public Object get(int columnPosition) {
        return values.get(columnPosition);
    }

    @Override
    public Object get(String columnName) {
        return values.get(columnNames.indexOf(columnName));
    }

    @Override
    public ImmutableList<Object> getAll() {
        return values;
    }

    @Override
    public ImmutableMap<String, Object> getMap() {
        return getMap(columnNames);
    }

    @Override
    public ImmutableMap<String, Object> getMap(ImmutableList<String> columnNames) {
        Map<String, Object> resultBuilder = new HashMap<>();
        for (String columnName : columnNames) {
            resultBuilder.put(columnName, get(columnName));
        }
        return ImmutableMap.fromMap(resultBuilder);
    }
    
}
