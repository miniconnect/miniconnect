package hu.webarticum.miniconnect.tool.lab.dummy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.tool.result.StoredResultSetData;

public class DescribeQueryExecutor implements QueryExecutor {

    private static final Pattern DESCRIBE_PATTERN = Pattern.compile(
            "^\\s*(?:DESCRIBE|EXPLAIN|SHOW\\s+COLUMNS\\s+(?:FROM|IN))\\s+" + // NOSONAR
            "(?<table>\\w+|([`\"])(?:[^`\"\\\\]|``|\"\"|\\\\.)+\\2)\\s*(?:;\\s*)?$",
            Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);


    @Override
    public MiniResult execute(String query) {
        Matcher matcher = DESCRIBE_PATTERN.matcher(query);
        if (!matcher.matches()) {
            return null;
        }

        String table = QueryUtil.unescapeIdentifier(matcher.group("table"));
        if (!table.equals("data")) {
            return new StoredResult("00002", "02", String.format("Unknown table: %s", table));
        }

        StoredResultSetData resultSetData = new StoredResultSetData(
                Structure.getMetaColumnHeaders(), Structure.getColumnData());

        return new StoredResult(resultSetData);
    }

}
