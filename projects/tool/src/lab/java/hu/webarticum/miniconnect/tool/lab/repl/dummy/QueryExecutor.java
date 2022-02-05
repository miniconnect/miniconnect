package hu.webarticum.miniconnect.tool.lab.repl.dummy;

import hu.webarticum.miniconnect.api.MiniResult;

public interface QueryExecutor {

    public MiniResult execute(String query);
    
}
