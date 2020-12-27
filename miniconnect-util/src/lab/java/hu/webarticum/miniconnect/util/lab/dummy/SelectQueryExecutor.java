package hu.webarticum.miniconnect.util.lab.dummy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;

public class SelectQueryExecutor implements QueryExecutor {
    
    private static final Pattern SELECT_PATTERN = Pattern.compile(
            "^\\s*SELECT\\s+\\*\\s+FROM\\s+" + // NOSONAR
            "(?<table>\\w+|([`\"])(?:[^`\"\\\\]|``|\"\"|\\\\.)+\\2)\\s*(?:;\\s*)?$",
            Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    @Override
    public MiniResult execute(String query) {
        Matcher matcher = SELECT_PATTERN.matcher(query);
        if (!matcher.matches()) {
            return null;
        }

        String table = QueryUtil.unescapeIdentifier(matcher.group("table"));
        if (!table.equals("data")) {
            return new DummyResult(String.format("Unknown table: %s", table));
        }

        DummyResultSet resultSet = new DummyResultSet(
                Structure.getColumnNames(), Structure.getRows());
        
        return new DummyResult(resultSet);
    }

}
