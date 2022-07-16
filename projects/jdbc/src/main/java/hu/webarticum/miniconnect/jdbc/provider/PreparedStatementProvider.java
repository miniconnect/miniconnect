package hu.webarticum.miniconnect.jdbc.provider;

import java.util.List;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.jdbc.ParameterDefinition;
import hu.webarticum.miniconnect.jdbc.ParameterValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public interface PreparedStatementProvider extends AutoCloseable {
    
    public ImmutableList<ParameterDefinition> parameters();

    public MiniResult execute(List<ParameterValue> parameters);
    
    @Override
    public void close();
    
}
