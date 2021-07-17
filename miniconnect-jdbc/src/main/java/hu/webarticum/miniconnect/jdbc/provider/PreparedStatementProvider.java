package hu.webarticum.miniconnect.jdbc.provider;

import java.util.List;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.jdbc.ParameterValue;

public interface PreparedStatementProvider extends AutoCloseable {

    public MiniResult execute(List<ParameterValue> parameters);
    
    @Override
    public void close();
    
}
