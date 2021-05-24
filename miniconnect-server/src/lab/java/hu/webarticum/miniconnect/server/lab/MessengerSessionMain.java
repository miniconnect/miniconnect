package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.server.lab.dummy.DummyServer;
import hu.webarticum.miniconnect.server.surface.MessengerSession;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class MessengerSessionMain {

    public static void main(String[] args) throws IOException {
        long sessionId = 1L;
        try (MiniSession session = new MessengerSession(sessionId, new DummyServer())) {
            new ReplRunner(new SqlRepl(session, System.out), System.in).run(); // NOSONAR
        }
    }

}
