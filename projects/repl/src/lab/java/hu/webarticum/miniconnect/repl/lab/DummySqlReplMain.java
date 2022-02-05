package hu.webarticum.miniconnect.repl.lab;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.repl.Repl;
import hu.webarticum.miniconnect.repl.ReplRunner;
import hu.webarticum.miniconnect.repl.SqlRepl;
import hu.webarticum.miniconnect.repl.lab.dummy.DummySession;

public class DummySqlReplMain {

    public static void main(String[] args) throws IOException {
        try (MiniSession session = new DummySession()) {
            Repl repl = new SqlRepl(
                    session,
                    System.out); // NOSONAR
            new ReplRunner(repl, System.in).run();
        }
    }

}
