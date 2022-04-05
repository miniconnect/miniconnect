package hu.webarticum.miniconnect.rdmsframework.parser;

import hu.webarticum.miniconnect.rdmsframework.query.Query;

public interface SqlParser {

    public Query parse(String sql);
    
}
