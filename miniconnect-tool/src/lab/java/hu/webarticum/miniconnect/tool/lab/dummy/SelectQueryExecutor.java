package hu.webarticum.miniconnect.tool.lab.dummy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResult;
import hu.webarticum.miniconnect.tool.result.StoredResultSetData;
import hu.webarticum.regexbee.Bee;

public class SelectQueryExecutor implements QueryExecutor {
    
    private static final String SQLCODE_UNKNOWN_TABLE = "42S02";

    private static final Pattern SELECT_PATTERN = Bee
            .then(Bee.BEGIN)
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("SELECT").then(Bee.WHITESPACE.more()))
            .then(Bee.fixed("*").then(Bee.WHITESPACE.more()))
            .then(Bee.fixed("FROM").then(Bee.WHITESPACE.more()))
            .then(Bee.IDENTIFIER
                    .or(Bee.fixed("`")
                            .then(Bee.simple("(?:[^`\\\\]|``|\\\\.)+")) // FIXME: escape support?
                            .then(Bee.fixed("`")))
                    .or(Bee.fixed("\"")
                            .then(Bee.simple("(?:[^\"\\\\]|\"\"|\\\\.)+"))
                            .then(Bee.fixed("\"")))
                    .as("table"))
            .then(Bee.WHITESPACE.any().then(Bee.fixed(";")).optional())
            .then(Bee.WHITESPACE.any())
            .then(Bee.END)
            .toPattern(Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    
    
    @Override
    public MiniResult execute(String query) {
        Matcher matcher = SELECT_PATTERN.matcher(query);
        if (!matcher.matches()) {
            return null;
        }

        String table = QueryUtil.unescapeIdentifier(matcher.group("table"));
        if (!table.equals("data")) {
            return new StoredResult(new StoredError(
                    2, SQLCODE_UNKNOWN_TABLE, String.format("Unknown table: %s", table)));
        }

        StoredResultSetData resultSetData = new StoredResultSetData(
                Structure.getColumnHeaders(), Structure.getRows());

        return new StoredResult(resultSetData);
    }

}
