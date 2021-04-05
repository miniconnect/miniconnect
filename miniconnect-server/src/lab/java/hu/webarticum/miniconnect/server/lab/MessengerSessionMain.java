package hu.webarticum.miniconnect.server.lab;

import hu.webarticum.miniconnect.server.surface.MessengerSession;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;

public class MessengerSessionMain {

    public static void main(String[] args) throws IOException {
        try (MiniSession session = new MessengerSession(null)) { // XXX
            MiniResult result = session.execute("SELECT * FROM table");
            System.out.println(String.format("success: %b", result.success()));
        }
    }

}
