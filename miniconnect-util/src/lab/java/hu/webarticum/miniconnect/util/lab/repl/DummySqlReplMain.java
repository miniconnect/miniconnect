package hu.webarticum.miniconnect.util.lab.repl;

import java.io.IOException;
import java.util.Properties;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.util.lab.dummy.DummyDriver;
import hu.webarticum.miniconnect.util.manager.MiniDriverManager;
import hu.webarticum.miniconnect.util.repl.Repl;
import hu.webarticum.miniconnect.util.repl.ReplRunner;
import hu.webarticum.miniconnect.util.repl.SqlRepl;

public class DummySqlReplMain {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName(DummyDriver.class.getName());
        try (MiniConnection connection = MiniDriverManager.openConnection("dummy", new Properties())) {
            Repl repl = new SqlRepl(
                    connection,
                    System.out, // NOSONAR
                    System.err); // NOSONAR
            new ReplRunner(repl, System.in).run();
        }
    }
    
}
