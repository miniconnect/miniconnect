package hu.webarticum.miniconnect.util.lab.repl;

import java.io.IOException;
import java.util.Properties;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.util.lab.dummy.DummyDriver;
import hu.webarticum.miniconnect.util.manager.MiniDriverManager;
import hu.webarticum.miniconnect.util.repl.Repl;
import hu.webarticum.miniconnect.util.repl.ReplRunner;
import hu.webarticum.miniconnect.util.repl.SqlRepl;

public class DummySqlReplMain {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName(DummyDriver.class.getName());
        try (MiniSession session = MiniDriverManager.openSession("dummy", new Properties())) {
            Repl repl = new SqlRepl(
                    session,
                    System.out, // NOSONAR
                    System.err); // NOSONAR
            new ReplRunner(repl, System.in).run();
        }
    }
    
}
