package hu.webarticum.miniconnect.tool.lab.dummy;

import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.tool.result.StoredColumnHeader;
import hu.webarticum.miniconnect.tool.result.XxxValueEncoder;


public final class Structure {

    private Structure() {
        // static class
    }


    public static List<MiniColumnHeader> getMetaColumnHeaders() {
        List<MiniColumnHeader> result = new ArrayList<>();
        result.add(new StoredColumnHeader("Field", String.class.getName()));
        result.add(new StoredColumnHeader("Type", String.class.getName()));
        result.add(new StoredColumnHeader("Null", String.class.getName()));
        result.add(new StoredColumnHeader("Key", String.class.getName()));
        result.add(new StoredColumnHeader("Default", String.class.getName()));
        result.add(new StoredColumnHeader("Extra", String.class.getName()));
        return result;
    }

    public static List<MiniColumnHeader> getColumnHeaders() {
        List<MiniColumnHeader> result = new ArrayList<>();
        result.add(new StoredColumnHeader("id", Integer.class.getName()));
        result.add(new StoredColumnHeader("label", String.class.getName()));
        result.add(new StoredColumnHeader("description", String.class.getName()));
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

        XxxValueEncoder stringEncoder = new XxxValueEncoder(String.class);

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
        XxxValueEncoder intEncoder = new XxxValueEncoder(Integer.class);
        XxxValueEncoder stringEncoder = new XxxValueEncoder(String.class);

        List<MiniValue> row = new ArrayList<>(3);
        row.add(intEncoder.encode(id));
        row.add(stringEncoder.encode(label));
        row.add(stringEncoder.encode(desription));
        return row;
    }

}
