package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;

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
            return new DummyResult(String.format("Unknown table: %s", table));
        }
        
        DummyResultSet resultSet = new DummyResultSet(
                Structure.getMetaColumnHeaders(), Structure.getColumnData());
        
        return new DummyResult(resultSet);
    }
    
}