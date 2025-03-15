package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.lang.ImmutableList;

public interface PreparedStatementProvider extends AutoCloseable {

    public String sql();

    public ImmutableList<ParameterDefinition> parameters();
    
    public void setParameterValue(int zeroBasedIndex, ParameterValue parameterValue);

    public void clearParameterValues();

    public MiniResult execute();
    
    @Override
    public void close();
    
}
