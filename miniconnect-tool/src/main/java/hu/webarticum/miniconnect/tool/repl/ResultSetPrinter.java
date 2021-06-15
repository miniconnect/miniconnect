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
        List<ImmutableList<MiniValue>> rowsBuffer = new ArrayList<>();
        ImmutableList<MiniValue> row;
        boolean foundAny = false;
        while ((row = resultSet.fetch()) != null) {
            foundAny = true;
            rowsBuffer.add(row);
            if (rowsBuffer.size() == ROWS_BUFFER_SIZE) {
                printRows(rowsBuffer, resultSet.columnHeaders(), out);
                rowsBuffer.clear();
            }
        }
        if (!rowsBuffer.isEmpty()) {
            printRows(rowsBuffer, resultSet.columnHeaders(), out);
        }
        if (!foundAny) {
            printNoRows(out);
        }
    }
    
    private void printNoRows(Appendable out) throws IOException {
        out.append("  Result contains no rows!\n\n");
    }
    
    private void printRows(
            List<ImmutableList<MiniValue>> rows,
            ImmutableList<MiniColumnHeader> columnHeaders,
            Appendable out
            ) throws IOException {
        
        int columnCount = columnHeaders.size();
        int[] widths = new int[columnCount];
        boolean[] aligns = new boolean[columnCount];
        List<ValueInterpreter> encoders = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            MiniColumnHeader columnHeader = columnHeaders.get(i);
            DefaultValueInterpreter encoder = new DefaultValueInterpreter(
                    columnHeader.valueDefinition());
            widths[i] = columnHeader.name().length();
            aligns[i] = Number.class.isAssignableFrom(encoder.type());
            encoders.add(encoder);
        }
        
        List<List<String>> stringRows = new ArrayList<>();
        for (ImmutableList<MiniValue> row : rows) {
            List<String> stringRow = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                MiniValue value = row.get(i);
                String stringValue = stringifyValue(value, encoders.get(i));
                stringRow.add(stringValue);
                widths[i] = Math.max(widths[i], stringValue.length());
            }
            stringRows.add(stringRow);
        }

        printLine(widths, out);
        
        List<String> headerNames = new ArrayList<>();
        for (MiniColumnHeader columnHeader : columnHeaders) {
            headerNames.add(columnHeader.name());
        }
        printStringRow(headerNames, widths, new boolean[columnCount], out);
        
        printLine(widths, out);
        
        for (List<String> stringRow : stringRows) {
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
            List<String> stringRow,
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

    public String stringifyValue(MiniValue value, ValueInterpreter encoder) {
        if (!value.isNull()) {
            Object decoded = encoder.decode(value);
            if (decoded instanceof Float || decoded instanceof Double || decoded instanceof BigDecimal) {
                return String.format("%.3f", decoded);
            } else {
                return decoded.toString();
            }
        } else {
            return NULL_PLACEHOLDER;
        }
    }

}
