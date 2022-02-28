package hu.webarticum.miniconnect.repl.lab.dummy;

import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.record.translator.IntTranslator;
import hu.webarticum.miniconnect.record.translator.StringTranslator;


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
        
        StringTranslator stringTranslator = StringTranslator.utf8Instance();
        
        List<MiniValue> row = new ArrayList<>(6);
        row.add(stringTranslator.encodeFully(field));
        row.add(stringTranslator.encodeFully(type));
        row.add(stringTranslator.encodeFully(isNullable ? "YES" : "NO"));
        row.add(stringTranslator.encodeFully(key));
        row.add(stringTranslator.encodeFully(defaultValue));
        row.add(stringTranslator.encodeFully(extra));
        return row;
    }

    public static List<List<MiniValue>> getRows() {
        List<List<MiniValue>> result = new ArrayList<>();

        result.add(createRow(1, "hello", "Lorem ipsum"));
        result.add(createRow(2, "xxx", "AAAAAAAAAAAA BBBBBBBB"));

        return result;
    }

    private static List<MiniValue> createRow(int id, String label, String desription) {
        IntTranslator intTranslator = IntTranslator.instance();
        StringTranslator stringTranslator = StringTranslator.utf8Instance();

        List<MiniValue> row = new ArrayList<>(3);
        row.add(intTranslator.encodeFully(id));
        row.add(stringTranslator.encodeFully(label));
        row.add(stringTranslator.encodeFully(desription));
        return row;
    }

}
