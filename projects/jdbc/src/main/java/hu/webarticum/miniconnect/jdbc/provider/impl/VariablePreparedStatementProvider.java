package hu.webarticum.miniconnect.jdbc.provider.impl;

import java.util.UUID;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.ParameterDefinition;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.PreparedStatementProvider;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class VariablePreparedStatementProvider implements PreparedStatementProvider {

    private static final String VARIABLE_NAME_PREFIX = "mini_jdbc_param_";
    

    private final DatabaseProvider databaseProvider;

    private final MiniSession session;
    
    private final String generatedSql;

    private final ImmutableList<ParameterDefinition> parameters;

    private final ImmutableList<String> parameterVariableNames;
    
    private final ParameterValue[] parameterValues;
    

    public VariablePreparedStatementProvider(DatabaseProvider databaseProvider, MiniSession session, String sql) {
        this.databaseProvider = databaseProvider;
        this.session = session;
        String[] sqlParts = PreparedStatementUtil.compileSql(sql);
        int parameterCount = sqlParts.length - 1;
        int selfHash = getClass().hashCode();
        this.parameters = ImmutableList.fill(parameterCount, i -> new ParameterDefinition());
        this.parameterVariableNames = ImmutableList.fill(parameterCount, i -> generateVariableName(selfHash, i));
        this.parameterValues = new ParameterValue[parameterCount];
        this.generatedSql = generateQuery(sqlParts, parameterVariableNames);
    }

    private static String generateVariableName(int hash, int i) {
        String uuidString = UUID.randomUUID().toString();
        String normalizedUuidString = uuidString.replace('-', '_');
        return VARIABLE_NAME_PREFIX + hash + "_" + (i + 1) + "_" + normalizedUuidString;
    }
    
    private static String generateQuery(String[] sqlParts, ImmutableList<String> parameterVariableNames) {
        StringBuilder resultBuilder = new StringBuilder();
        int i = 0;
        for (String name : parameterVariableNames) {
            resultBuilder.append(sqlParts[i]);
            resultBuilder.append("@" + name);
            i++;
        }
        resultBuilder.append(sqlParts[i]);
        return resultBuilder.toString();
    }
    
    
    @Override
    public String sql() {
        return generatedSql;
    }

    @Override
    public ImmutableList<ParameterDefinition> parameters() {
        return parameters;
    }

    @Override
    public void setParameterValue(int zeroBasedIndex, ParameterValue parameterValue) {
        String parameterName = parameterVariableNames.get(zeroBasedIndex);
        PreparedStatementUtil.putVariable(session, databaseProvider, parameterName, parameterValue);
        PreparedStatementUtil.closeIfNecessary(parameterValues[zeroBasedIndex]);
        parameterValues[zeroBasedIndex] = parameterValue;
    }
    
    @Override
    public void clearParameterValues() {
        int i = 0;
        for (ParameterValue parameterValue : parameterValues) {
            PreparedStatementUtil.closeIfNecessary(parameterValue);
            String name = parameterVariableNames.get(i);
            session.execute("SET @" + name + " = NULL");
            i++;
        }
    }
    
    @Override
    public MiniResult execute() {
        return session.execute(generatedSql);
    }

    @Override
    public void close() {
        clearParameterValues();
    }

}
