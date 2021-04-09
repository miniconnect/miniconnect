package hu.webarticum.miniconnect.tool.lab.repl;

import java.io.IOException;
import java.util.Properties;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.tool.lab.dummy.DummyDriver;
import hu.webarticum.miniconnect.tool.manager.DriverManager;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class DummySqlReplMain {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName(DummyDriver.class.getName());
        try (MiniConnection connection = DriverManager.openConnection("dummy", new Properties())) {
            try (MiniSession session = connection.openSession()) {
                Repl repl = new SqlRepl(
                        session,
                        System.out); // NOSONAR
                new ReplRunner(repl, System.in).run();
            }
        }
    }

}
