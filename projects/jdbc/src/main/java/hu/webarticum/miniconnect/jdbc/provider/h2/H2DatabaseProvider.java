package hu.webarticum.miniconnect.jdbc.provider.h2;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;

public class H2DatabaseProvider implements DatabaseProvider {

    @Override
    public H2PreparedStatementProvider prepareStatement(MiniSession session, String sql) {
        return new H2PreparedStatementProvider(this, session, sql);
    }
    
    @Override
    public String stringifyValue(ParameterValue parameterValue) {
        Object value = parameterValue.value();
        if (value == null) {
            return "NULL";
        }
        
        return "'" + value + "'";
    }

}
