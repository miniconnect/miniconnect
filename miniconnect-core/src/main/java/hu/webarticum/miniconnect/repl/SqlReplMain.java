package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.util.Properties;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.manager.MiniDriverManager;

public class SqlReplMain {
    
    public static void main(String[] args) throws IOException {
        try (MiniConnection connection = MiniDriverManager.openConnection(args[0], new Properties())) {
            Repl repl = new SqlRepl(
                    connection,
                    System.out, // NOSONAR
                    System.err); // NOSONAR
            new ReplRunner(repl, System.in).run();
        }
    }
    
}
