package hu.webarticum.miniconnect.rdmsframework.execution;

import java.util.concurrent.Future;

public class ParsingSqlExecutor implements SqlExecutor {
    
    private final SqlParser parser;
    
    private final QueryExecutor executor;
    

    public ParsingSqlExecutor(SqlParser parser, QueryExecutor executor) {
        this.parser = parser;
        this.executor = executor;
    }
    
    
    @Override
    public Future<Object> execute(String sql) {
        Query query = parser.parse(sql);
        return executor.execute(query);
    }

}
