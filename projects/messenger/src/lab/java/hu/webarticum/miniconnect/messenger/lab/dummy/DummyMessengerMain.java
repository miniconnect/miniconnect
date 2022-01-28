package hu.webarticum.miniconnect.messenger.lab.dummy;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class DummyMessengerMain {

    public static void main(String[] args) {
        MiniSessionManager sessionManager = new MessengerSessionManager(new DummyMessenger());
        try (MiniSession session = sessionManager.openSession()) {
            new ReplRunner(new SqlRepl(session, System.out), System.in).run(); // NOSONAR
        }
    }

}
