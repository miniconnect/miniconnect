package hu.webarticum.miniconnect.jdbc.provider.impl;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.ParameterDefinition;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class FakePreparedStatementProvider implements PreparedStatementProvider {
    
    private final DatabaseProvider databaseProvider;

    private final MiniSession session;
    
    private final String sql;
    
    private final String[] sqlParts;
    
    private final ImmutableList<ParameterDefinition> parameters;
    
    private final ParameterValue[] parameterValues;
    
    
    public FakePreparedStatementProvider(DatabaseProvider databaseProvider, MiniSession session, String sql) {
        this.databaseProvider = databaseProvider;
        this.session = session;
        this.sql = sql;
        this.sqlParts = PreparedStatementUtil.compileSql(sql);
        int parameterCount = sqlParts.length - 1;
        this.parameters = ImmutableList.fill(parameterCount, i -> new ParameterDefinition());
        this.parameterValues = new ParameterValue[parameterCount];
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
    public void setParameterValue(int zeroBasedIndex, ParameterValue parameterValue) {
        PreparedStatementUtil.closeIfNecessary(parameterValues[zeroBasedIndex]);
        parameterValues[zeroBasedIndex] = parameterValue;
    }

    @Override
    public void clearParameterValues() {
        for (ParameterValue parameterValue : parameterValues) {
            PreparedStatementUtil.closeIfNecessary(parameterValue);
        }
    }
    
    @Override
    public MiniResult execute() {
        return session.execute(substitute());
    }

    private String substitute() {
        int expectedParameterCount = parameters.size();
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < parameterValues.length; i++) {
            resultBuilder.append(sqlParts[i]);
            resultBuilder.append(databaseProvider.stringifyValue(parameterValues[i]));
        }
        resultBuilder.append(sqlParts[expectedParameterCount]);
        return resultBuilder.toString();
    }
    
    @Override
    public void close() {
        // nothing to do
    }
    
}
