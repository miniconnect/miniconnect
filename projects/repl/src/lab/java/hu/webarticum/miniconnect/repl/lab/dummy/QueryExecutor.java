package hu.webarticum.miniconnect.repl.lab.dummy;

import hu.webarticum.miniconnect.api.MiniResult;

public interface QueryExecutor {

    public MiniResult execute(String query);
    
}
