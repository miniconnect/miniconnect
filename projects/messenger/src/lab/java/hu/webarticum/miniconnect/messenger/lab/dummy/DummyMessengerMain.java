package hu.webarticum.miniconnect.messenger.lab.dummy;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.repl.PlainReplRunner;
import hu.webarticum.miniconnect.repl.Repl;
import hu.webarticum.miniconnect.repl.SqlRepl;

public class DummyMessengerMain {

    public static void main(String[] args) {
        MiniSessionManager sessionManager = new MessengerSessionManager(new DummyMessenger());
        try (MiniSession session = sessionManager.openSession()) {
            Repl repl = new SqlRepl(session);
            new PlainReplRunner(System.in, System.out).run(repl); // NOSONAR System.out is necessary
        }
    }

}
