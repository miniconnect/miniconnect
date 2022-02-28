package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.ResultField;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;

public class ResultSetPrinter {

    // TODO: make these configurable
    private static final String NULL_PLACEHOLDER = "[NULL]";

    private static final int ROWS_BUFFER_SIZE = 20;

    private static final int MAXIMUM_STRING_LENGTH = 20;
    
    private static final String STRING_OVERFLOW_ELLIPSIS = "...";

    
    public void print(ResultTable resultTable, Appendable out) throws IOException {
        out.append('\n');
        ImmutableList<MiniColumnHeader> columnHeaders = resultTable.resultSet().columnHeaders();
        ImmutableList<String> columnNames = columnHeaders.map(MiniColumnHeader::name);
        List<ImmutableList<Object>> decodedRowsBuffer = new ArrayList<>();
        boolean foundAny = false;
        for (ResultRecord record : resultTable) {
            ImmutableList<Object> decodedRow = record.getAll().map(ResultField::get);
            decodedRowsBuffer.add(decodedRow);
            if (decodedRowsBuffer.size() == ROWS_BUFFER_SIZE) {
                printDecodedRows(decodedRowsBuffer, columnNames, out);
                decodedRowsBuffer.clear();
            }
        }
        if (!decodedRowsBuffer.isEmpty()) {
            printDecodedRows(decodedRowsBuffer, columnNames, out);
        }
        if (!foundAny) {
            printNoRows(out);
        }
    }
    
    private void printNoRows(Appendable out) throws IOException {
        out.append("  Result contains no rows!\n\n");
    }
    
    private void printDecodedRows(
            List<ImmutableList<Object>> decodedRows,
            ImmutableList<String> columnNames,
            Appendable out
            ) throws IOException {
        int columnCount = columnNames.size();
        int[] widths = new int[columnCount];
        boolean[] aligns = new boolean[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnName = columnNames.get(i);
            widths[i] = columnName.length();
        }
        if (!decodedRows.isEmpty()) {
            ImmutableList<Object> firstDecodedRow = decodedRows.get(0);
            for (int i = 0; i < columnCount; i++) {
                if (firstDecodedRow.get(i) instanceof Number) {
                    aligns[i] = true;
                }
            }
        }
        
        List<ImmutableList<String>> stringRows = new ArrayList<>(decodedRows.size());
        for (ImmutableList<Object> decodedRow : decodedRows) {
            ImmutableList<String> stringRow = decodedRow.map(this::stringify);
            stringRows.add(stringRow);
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
    
    private String stringify(Object value) {
        if (value == null) {
            return NULL_PLACEHOLDER;
        } else if (
                value instanceof Float ||
                value instanceof Double ||
                value instanceof BigDecimal) {
            return String.format("%.3f", value);
        } else if (
                value instanceof Number ||
                value instanceof Temporal) {
            return value.toString();
        } else {
            return shortenString(value.toString());
        }
    }
    
    private String shortenString(String stringValue) {
        int length = stringValue.length();
        if (length <= MAXIMUM_STRING_LENGTH) {
            return stringValue;
        }
        
        int innerLength =
                Math.max(1, MAXIMUM_STRING_LENGTH - STRING_OVERFLOW_ELLIPSIS.length());
        return stringValue.substring(0, innerLength) + STRING_OVERFLOW_ELLIPSIS;
    }

}
