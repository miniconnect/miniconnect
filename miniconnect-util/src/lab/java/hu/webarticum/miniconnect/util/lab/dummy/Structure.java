package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import hu.webarticum.miniconnect.api.MiniValue;


public final class Structure {
    
    private Structure() {
        // static class
    }
    

    public static List<String> getMetaColumnNames() {
        return Arrays.asList("Field", "Type", "Null", "Key", "Default", "Extra");
    }

    public static List<String> getColumnNames() {
        return getColumnData().stream()
                .map(row -> row.get(0).asString())
                .collect(Collectors.toList());
    }
    
    public static List<List<MiniValue>> getColumnData() {
        List<List<MiniValue>> result = new ArrayList<>(3);
        result.add(createColumnData("id", "int(11)", false, "PRI", null, "auto_increment"));
        result.add(createColumnData("label", "varchar(100)", false, "", "", ""));
        result.add(createColumnData("description", "text", true, "", null, ""));
        return result;
    }
    
    private static List<MiniValue> createColumnData(
            String field,
            String type,
            boolean isNullable,
            String key,
            String defaultValue,
            String extra) {
        
        List<MiniValue> row = new ArrayList<>(6);
        row.add(new StringValue(field));
        row.add(new StringValue(type));
        row.add(new StringValue(isNullable ? "YES" : "NO"));
        row.add(new StringValue(key));
        row.add(defaultValue != null ? new StringValue(defaultValue) : new NullValue());
        row.add(new StringValue(extra));
        return row;
    }

    public static List<List<MiniValue>> getRows() {
        List<List<MiniValue>> result = new ArrayList<>();

        result.add(createRow(1, "hello", "Lorem ipsum"));
        result.add(createRow(2, "xxx", "AAAAAAAAAAAA BBBBBBBB"));
        
        return result;
    }
    
    private static List<MiniValue> createRow(int id, String label, String desription) {
        List<MiniValue> row = new ArrayList<>(3);
        row.add(new LongValue(id));
        row.add(new StringValue(label));
        row.add(new StringValue(desription));
        return row;
    }
    
}
