package hu.webarticum.miniconnect.messenger.lab.dummy;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.messenger.surface.MessengerSession;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class DummyMessengerMain {

    public static void main(String[] args) throws IOException {
        long sessionId = 1L;
        try (MiniSession session = new MessengerSession(sessionId, new DummyMessenger())) {
            new ReplRunner(new SqlRepl(session, System.out), System.in).run(); // NOSONAR
        }
    }

}
