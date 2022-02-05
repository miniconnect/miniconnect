package hu.webarticum.miniconnect.tool.lab.repl;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.tool.lab.repl.dummy.DummySession;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

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
