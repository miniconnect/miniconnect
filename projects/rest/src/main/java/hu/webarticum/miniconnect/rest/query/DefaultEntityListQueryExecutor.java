package hu.webarticum.miniconnect.rest.query;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;

public class DefaultEntityListQueryExecutor implements EntityListQueryExecutor {
    
    private final MiniSession session;
    
    private final String tableName;
    

    public DefaultEntityListQueryExecutor(MiniSession session, String tableName) {
        this.session = session;
        this.tableName = tableName;
    }
    
    
    public MiniResult execute(EntityListQuery query) {
        
        // TODO
        return session.execute("SELECT * FROM " + quoteIdentifier(tableName));
        
    }

    // FIXME
    private String quoteIdentifier(String name) {
        return "`" + name.replace("`", "``") + "`";
    }

}
