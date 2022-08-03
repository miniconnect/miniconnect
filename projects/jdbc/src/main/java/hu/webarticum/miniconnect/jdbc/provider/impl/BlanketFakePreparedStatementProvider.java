package hu.webarticum.miniconnect.jdbc.provider.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.ParameterDefinition;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.regexbee.Bee;
import hu.webarticum.regexbee.BeeFragment;
import hu.webarticum.regexbee.common.StringLiteralFragment;

public class BlanketFakePreparedStatementProvider implements PreparedStatementProvider {
    
    private static final BeeFragment SINGLE_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("'")
            .withEscaping('\\', true)
            .build();
    
    private static final BeeFragment DOUBLE_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("\"")
            .withEscaping('\\', true)
            .build();
    
    private static final BeeFragment TWODOLLARS_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("$$")
            .withoutAnyEscaping()
            .build();

    private static final Pattern STRING_OR_QUESTION_MARK_PATTERN =
            SINGLE_QUOTED_FRAGMENT
            .or(DOUBLE_QUOTED_FRAGMENT)
            .or(TWODOLLARS_QUOTED_FRAGMENT)
            .or(Bee.fixed("?"))
            .toPattern();
    
    
    private final DatabaseProvider databaseProvider;

    private final MiniSession session;
    
    private final String sql;
    
    private final String[] sqlParts;
    
    private final ImmutableList<ParameterDefinition> parameters;
    
    
    public BlanketFakePreparedStatementProvider(
            DatabaseProvider databaseProvider, MiniSession session, String sql) {
        this.databaseProvider = databaseProvider;
        this.session = session;
        this.sql = sql;
        this.sqlParts = compileSql(sql);
        this.parameters = ImmutableList.fill(sqlParts.length - 1, i -> new ParameterDefinition()); // TODO
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
    public String sql() {
        return sql;
    }
    
    @Override
    public ImmutableList<ParameterDefinition> parameters() {
        return parameters;
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
            
            // TODO: LOB objects
            
            resultBuilder.append(sqlParts[i]);
            resultBuilder.append(databaseProvider.stringifyValue(parameters.get(i)));
        }
        resultBuilder.append(sqlParts[expectedParameterCount]);
        
        return resultBuilder.toString();
    }
    
    @Override
    public void close() {
        // nothing to do
    }
    
}
