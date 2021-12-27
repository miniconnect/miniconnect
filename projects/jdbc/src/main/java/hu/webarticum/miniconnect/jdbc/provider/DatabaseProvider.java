package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.ParameterValue;

public interface DatabaseProvider {

    public PreparedStatementProvider prepareStatement(MiniSession session, String sql);
    
    public String stringifyValue(ParameterValue parameterValue);
    
}
