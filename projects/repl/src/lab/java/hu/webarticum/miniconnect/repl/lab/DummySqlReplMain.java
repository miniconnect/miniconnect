package hu.webarticum.miniconnect.repl.lab;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.repl.Repl;
import hu.webarticum.miniconnect.repl.PlainReplRunner;
import hu.webarticum.miniconnect.repl.SqlRepl;
import hu.webarticum.miniconnect.repl.lab.dummy.DummySession;

public class DummySqlReplMain {

    public static void main(String[] args) {
        try (MiniSession session = new DummySession()) {
            Repl repl = new SqlRepl(session);
            new PlainReplRunner(System.in, System.out).run(repl); // NOSONAR System.out is necessary
        }
    }

}
