package hu.webarticum.miniconnect.jdbc.provider.h2;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;

public class H2DatabaseProvider implements DatabaseProvider {

    @Override
    public H2PreparedStatementProvider prepareStatement(MiniSession session, String sql) {
        return new H2PreparedStatementProvider(session, sql);
    }

    
}
