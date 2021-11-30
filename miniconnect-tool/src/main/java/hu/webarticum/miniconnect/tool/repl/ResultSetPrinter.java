package hu.webarticum.miniconnect.tool.repl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.tool.result.DefaultValueInterpreter;
import hu.webarticum.miniconnect.tool.result.ValueInterpreter;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class ResultSetPrinter {

    private static final String NULL_PLACEHOLDER = "[NULL]";

    private static final int ROWS_BUFFER_SIZE = 20;

    
    public void print(MiniResultSet resultSet, Appendable out) throws IOException {
        out.append('\n');
        ImmutableList<MiniColumnHeader> columnHeaders = resultSet.columnHeaders();
        ImmutableList<String> columnNames = columnHeaders.map(MiniColumnHeader::name);
        ImmutableList<DefaultValueInterpreter> valueInterpreters = columnHeaders.map(
                h -> new DefaultValueInterpreter(h.valueDefinition()));
        List<ImmutableList<String>> stringRowsBuffer = new ArrayList<>();
        boolean foundAny = false;
        for (ImmutableList<MiniValue> row : resultSet) {
            foundAny = true;
            ImmutableList<String> stringRow = row.mapIndex(
                    (i, value) -> stringifyValue(value, valueInterpreters.get(i)));
            stringRowsBuffer.add(stringRow);
            if (stringRowsBuffer.size() == ROWS_BUFFER_SIZE) {
                printStringRows(stringRowsBuffer, columnNames, valueInterpreters, out);
                stringRowsBuffer.clear();
            }
        }
        if (!stringRowsBuffer.isEmpty()) {
            printStringRows(stringRowsBuffer, columnNames, valueInterpreters, out);
        }
        if (!foundAny) {
            printNoRows(out);
        }
    }
    
    private void printNoRows(Appendable out) throws IOException {
        out.append("  Result contains no rows!\n\n");
    }
    
    private void printStringRows(
            List<ImmutableList<String>> stringRows,
            ImmutableList<String> columnNames,
            ImmutableList<DefaultValueInterpreter> valueInterpreters,
            Appendable out
            ) throws IOException {
        int columnCount = columnNames.size();
        int[] widths = new int[columnCount];
        boolean[] aligns = new boolean[columnCount];
        List<DefaultValueInterpreter> encoders = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            String columnName = columnNames.get(i);
            DefaultValueInterpreter valueInterpreter = valueInterpreters.get(i);
            widths[i] = columnName.length();
            aligns[i] = Number.class.isAssignableFrom(valueInterpreter.type());
            encoders.add(valueInterpreter);
        }
        
        for (ImmutableList<String> stringRow : stringRows) {
            for (int i = 0; i < columnCount; i++) {
                String stringValue = stringRow.get(i);
                widths[i] = Math.max(widths[i], stringValue.length());
            }
        }

        printLine(widths, out);
        
        printStringRow(columnNames, widths, new boolean[columnCount], out);
        
        printLine(widths, out);
        
        for (ImmutableList<String> stringRow : stringRows) {
            printStringRow(stringRow, widths, aligns, out);
        }
        
        printLine(widths, out);

        out.append('\n');
    }

    private void printLine(int[] widths, Appendable out) throws IOException {
        out.append("  ");
        for (int width : widths) {
            out.append('+');
            out.append('-');
            for (int i = 0; i < width; i++) {
                out.append('-');
            }
            out.append('-');
        }
        out.append('+');
        out.append('\n');
    }

    private void printStringRow(
            ImmutableList<String> stringRow,
            int[] widths,
            boolean[] aligns,
            Appendable out
            ) throws IOException {

        out.append("  ");
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            boolean align = aligns[i];
            String format = "| %" + (align ? "" : "-") + width + "s ";
            String stringValue = stringRow.get(i);
            out.append(String.format(format, stringValue));
        }
        out.append('|');
        out.append('\n');
    }
    
    private String stringifyValue(MiniValue value, ValueInterpreter encoder) {
        if (!value.isNull()) {
            Object decoded = encoder.decode(value);
            if (
                    decoded instanceof Float ||
                    decoded instanceof Double ||
                    decoded instanceof BigDecimal) {
                return String.format("%.3f", decoded);
            } else {
                return decoded.toString();
            }
        } else {
            return NULL_PLACEHOLDER;
        }
    }

}
