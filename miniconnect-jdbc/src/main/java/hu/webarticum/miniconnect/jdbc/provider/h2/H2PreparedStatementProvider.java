package hu.webarticum.miniconnect.jdbc.provider.h2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;

public class H2PreparedStatementProvider implements PreparedStatementProvider {
    
    private static final Pattern STRING_OR_QUESTION_MARK_PATTERN =
            Pattern.compile("'(?:[^']|'')*'|\"(?:[^\"]|\"\")*\"|\\?");
    

    private final MiniSession session;
    
    private final String[] sqlParts;
    
    
    public H2PreparedStatementProvider(MiniSession session, String sql) {
        this.session = session;
        this.sqlParts = compileSql(sql);
    }
    
    private static String[] compileSql(String sql) {
        Matcher matcher = STRING_OR_QUESTION_MARK_PATTERN.matcher(sql);
        List<Integer> positions = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group().charAt(0) == '?') {
                positions.add(matcher.start());
            }
        }
        int questionMarkCount = positions.size();
        String[] result = new String[questionMarkCount + 1];
        int continuingPosition = 0;
        for (int i = 0; i < questionMarkCount; i++) {
            int position = positions.get(i);
            result[i] = sql.substring(continuingPosition, position);
            continuingPosition = position + 1;
        }
        result[questionMarkCount] = sql.substring(continuingPosition);
        return result;
    }


    @Override
    public MiniResult execute(List<ParameterValue> parameters) {
        return session.execute(substitute(parameters));
    }
    
    private String substitute(List<ParameterValue> parameters) {
        int expectedParameterCount = sqlParts.length - 1;
        int givenParameterCount = parameters.size();
        if (givenParameterCount != expectedParameterCount) {
            throw new IllegalArgumentException(String.format(
                    "Expected parameter count: %d, but %d given",
                    expectedParameterCount, givenParameterCount));
        }
        
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < expectedParameterCount; i++) {
            resultBuilder.append(sqlParts[i]);
            resultBuilder.append(stringifyValue(parameters.get(i)));
        }
        resultBuilder.append(sqlParts[expectedParameterCount]);
        
        return resultBuilder.toString();
    }
    
    // TODO
    private String stringifyValue(ParameterValue parameterValue) {
        Object value = parameterValue.value();
        if (value == null) {
            return "NULL";
        }
        
        return "'" + value + "'";
    }

    @Override
    public void close() {
        // nothing to do
    }
    
}
