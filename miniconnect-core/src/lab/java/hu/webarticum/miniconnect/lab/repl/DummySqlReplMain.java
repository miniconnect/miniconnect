package hu.webarticum.miniconnect.lab.repl;

import java.io.IOException;
import java.util.Properties;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.lab.dummy.DummyDriver;
import hu.webarticum.miniconnect.manager.MiniDriverManager;
import hu.webarticum.miniconnect.repl.Repl;
import hu.webarticum.miniconnect.repl.ReplRunner;
import hu.webarticum.miniconnect.repl.SqlRepl;

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
