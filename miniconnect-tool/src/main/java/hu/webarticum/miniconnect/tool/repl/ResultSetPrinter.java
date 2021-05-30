package hu.webarticum.miniconnect.tool.repl;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.tool.result.DefaultValueEncoder;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class ResultSetPrinter {

    private static final String NULL_PLACEHOLDER = "[NULL]";


    public void print(MiniResultSet resultSet, Appendable out) throws IOException {


        // TODO


        out.append('\n');

        out.append("---------------------\n");

        ImmutableList<MiniColumnHeader> columnHeaders = resultSet.columnHeaders();
        for (MiniColumnHeader columnHeader : columnHeaders) {
            out.append(columnHeader.name() + " | ");
        }
        out.append('\n');

        out.append("---------------------\n");

        int rowLength = columnHeaders.size();
        for (
                ImmutableList<MiniValue> row = resultSet.fetch();
                row != null;
                row = resultSet.fetch()) {

            for (int i = 0; i < rowLength; i++) {
                MiniValue value = row.get(i);
                MiniColumnHeader columnHeader = columnHeaders.get(i);
                out.append(stringifyValue(columnHeader, value));
                out.append(", ");
            }
            out.append('\n');
        }

        out.append('\n');
    }

    public String stringifyValue(MiniColumnHeader columnHeader, MiniValue value) {
        if (!value.isNull()) {
            return new DefaultValueEncoder(columnHeader).decode(value).toString();
        } else {
            return NULL_PLACEHOLDER;
        }
    }

}
