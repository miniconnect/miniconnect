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
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

public class ResultSetPrinter {

    // TODO: make these configurable
    private static final String NULL_PLACEHOLDER = "[NULL]";

    private static final int ROWS_BUFFER_SIZE = 20;

    private static final int MAXIMUM_STRING_LENGTH = 20;
    
    private static final String STRING_OVERFLOW_ELLIPSIS = "...";

    
    public void print(ResultTable resultTable, AnsiAppendable out) throws IOException {
        ImmutableList<MiniColumnHeader> columnHeaders = resultTable.resultSet().columnHeaders();
        ImmutableList<String> columnNames = columnHeaders.map(MiniColumnHeader::name);
        List<ImmutableList<Object>> decodedRowsBuffer = new ArrayList<>();
        boolean foundAny = false;
        for (ResultRecord resultRecord : resultTable) {
            foundAny = true;
            ImmutableList<Object> decodedRow = resultRecord.getAll().map(ResultField::get);
            decodedRowsBuffer.add(decodedRow);
            if (decodedRowsBuffer.size() == ROWS_BUFFER_SIZE) {
                printDecodedRows(decodedRowsBuffer, columnNames, resultTable.valueTranslators(), out);
                decodedRowsBuffer.clear();
            }
        }
        if (!decodedRowsBuffer.isEmpty()) {
            printDecodedRows(decodedRowsBuffer, columnNames, resultTable.valueTranslators(), out);
        }
        if (!foundAny) {
            printNoRows(out);
        }
    }
    
    private void printNoRows(AnsiAppendable out) throws IOException {
        out.append("  Result set contains no rows!\n\n");
    }
    
    private void printDecodedRows(
            List<ImmutableList<Object>> decodedRows,
            ImmutableList<String> columnNames,
            ImmutableList<ValueTranslator> valueTranslators,
            AnsiAppendable out
            ) throws IOException {
        int columnCount = columnNames.size();
        int[] widths = new int[columnCount];
        boolean[] aligns = new boolean[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnName = columnNames.get(i);
            widths[i] = columnName.length();
        }
        
        if (!decodedRows.isEmpty()) {
            for (int i = 0; i < columnCount; i++) {
                if (shouldAlign(valueTranslators.get(i))) {
                    aligns[i] = true;
                }
            }
        }
        
        List<ImmutableList<ValueOutputHolder>> outputRows = new ArrayList<>(decodedRows.size());
        for (ImmutableList<Object> decodedRow : decodedRows) {
            ImmutableList<ValueOutputHolder> outputRow = decodedRow.map((i, v) -> outputOf(v, aligns[i]));
            outputRows.add(outputRow);
            for (int i = 0; i < columnCount; i++) {
                String stringValue = outputRow.get(i).plainString;
                widths[i] = Math.max(widths[i], stringValue.length());
            }
        }

        printLine(widths, '\u2500', '\u250C', '\u2510', '\u252C', out);
        
        printOutputRow(columnNames.map(this::outputOfHeader), widths, new boolean[columnCount], out);
        
        printLine(widths, '\u2500', '\u251C', '\u2524', '\u253C', out);
        
        for (ImmutableList<ValueOutputHolder> outputRow : outputRows) {
            printOutputRow(outputRow, widths, aligns, out);
        }
        
        printLine(widths, '\u2500', '\u2514', '\u2518', '\u2534', out);

        out.append('\n');
    }
    
    private boolean shouldAlign(ValueTranslator valueTranslator) {
        Class<?> clazz;
        try {
            clazz = Class.forName(valueTranslator.assuredClazzName());
        } catch (ClassNotFoundException e) {
            return false;
        }
        
        return Number.class.isAssignableFrom(clazz);
    }

    private void printLine(
            int[] widths, char inner, char left, char right, char cross, AnsiAppendable out) throws IOException {
        out.append("  ");
        boolean first = true;
        for (int width : widths) {
            if (first) {
                out.append(left);
                first = false;
            } else {
                out.append(cross);
            }
            out.append(inner);
            for (int i = 0; i < width; i++) {
                out.append(inner);
            }
            out.append(inner);
        }
        out.append(right);
        out.append('\n');
    }

    private void printOutputRow(
            ImmutableList<ValueOutputHolder> outputRow,
            int[] widths,
            boolean[] aligns,
            AnsiAppendable out
            ) throws IOException {
        out.append("  ");
        for (int i = 0; i < widths.length; i++) {
            out.append("\u2502 ");
            printValueOutput(outputRow.get(i), widths[i], aligns[i], out);
            out.append(' ');
        }
        out.append('\u2502');
        out.append('\n');
    }
    
    private void printValueOutput(
            ValueOutputHolder valueOutputHolder, int width, boolean align, AnsiAppendable out) throws IOException {
        Object value = valueOutputHolder.value;
        int valueWidth = valueOutputHolder.plainString.length();
        int padWidth = Math.max(0, width - valueWidth);
        int leftPadWidth = (value != null && align) ? alignValueOutput(valueOutputHolder, padWidth) : 0;
        int rightPadWidth = padWidth - leftPadWidth;
        out.append(spaces(leftPadWidth));
        out.appendAnsi(valueOutputHolder.ansiString);
        out.append(spaces(rightPadWidth));
    }
    
    // TODO: add support for aligned decimals
    private int alignValueOutput(ValueOutputHolder valueOutputHolder, int padWidth) {
        return padWidth;
    }
    
    private String spaces(int length) {
        StringBuilder resultBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            resultBuilder.append(' ');
        }
        return resultBuilder.toString();
    }

    private ValueOutputHolder outputOfHeader(String headerName) {
        return new ValueOutputHolder(headerName, headerName, AnsiUtil.formatAsHeader(headerName));
    }
    
    private ValueOutputHolder outputOf(Object value, boolean align) {
        String plainString = stringify(value);
        String ansiString = AnsiUtil.escapeText(plainString);
        if (value == null) {
            ansiString = AnsiUtil.formatAsNone(ansiString);
        } else if (align) {
            ansiString = AnsiUtil.formatAsNumber(ansiString);
        }
        return new ValueOutputHolder(value, plainString, ansiString);
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
        
        int innerLength = Math.max(1, MAXIMUM_STRING_LENGTH - STRING_OVERFLOW_ELLIPSIS.length());
        return stringValue.substring(0, innerLength) + STRING_OVERFLOW_ELLIPSIS;
    }
    
    
    private static class ValueOutputHolder {
        
        private final Object value;
        
        private final String plainString;
        
        private final String ansiString;
        
        
        private ValueOutputHolder(Object value, String plainString, String ansiString) {
            this.value = value;
            this.plainString = plainString;
            this.ansiString = ansiString;
        }
        
    }

}
