package hu.webarticum.miniconnect.jdbc.provider;

import hu.webarticum.miniconnect.api.MiniSession;

public interface DatabaseProvider {

    public PreparedStatementProvider prepareStatement(MiniSession session, String sql);
    
}
