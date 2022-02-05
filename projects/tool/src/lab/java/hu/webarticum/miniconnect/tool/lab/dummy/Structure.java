package hu.webarticum.miniconnect.tool.lab.dummy;

import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.tool.result.StoredValueDefinition;
import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;


public final class Structure {
    
    private static final StoredValueDefinition STRING_DEFINITION =
            new StoredValueDefinition(String.class.getName());

    private static final StoredValueDefinition INTEGER_DEFINITION =
            new StoredValueDefinition(Integer.class.getName());
    

    private Structure() {
        // static class
    }


    public static List<MiniColumnHeader> getMetaColumnHeaders() {
        List<MiniColumnHeader> result = new ArrayList<>();
        result.add(new StoredColumnHeader("Field", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("Type", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("Null", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("Key", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("Default", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("Extra", false, STRING_DEFINITION));
        return result;
    }

    public static List<MiniColumnHeader> getColumnHeaders() {
        List<MiniColumnHeader> result = new ArrayList<>();
        result.add(new StoredColumnHeader("id", false, INTEGER_DEFINITION));
        result.add(new StoredColumnHeader("label", false, STRING_DEFINITION));
        result.add(new StoredColumnHeader("description", false, STRING_DEFINITION));
        return result;
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

        DefaultValueInterpreter stringEncoder = new DefaultValueInterpreter(String.class);

        List<MiniValue> row = new ArrayList<>(6);
        row.add(stringEncoder.encode(field));
        row.add(stringEncoder.encode(type));
        row.add(stringEncoder.encode(isNullable ? "YES" : "NO"));
        row.add(stringEncoder.encode(key));
        row.add(stringEncoder.encode(defaultValue));
        row.add(stringEncoder.encode(extra));
        return row;
    }

    public static List<List<MiniValue>> getRows() {
        List<List<MiniValue>> result = new ArrayList<>();

        result.add(createRow(1, "hello", "Lorem ipsum"));
        result.add(createRow(2, "xxx", "AAAAAAAAAAAA BBBBBBBB"));

        return result;
    }

    private static List<MiniValue> createRow(int id, String label, String desription) {
        DefaultValueInterpreter intEncoder = new DefaultValueInterpreter(Integer.class);
        DefaultValueInterpreter stringEncoder = new DefaultValueInterpreter(String.class);

        List<MiniValue> row = new ArrayList<>(3);
        row.add(intEncoder.encode(id));
        row.add(stringEncoder.encode(label));
        row.add(stringEncoder.encode(desription));
        return row;
    }

}
