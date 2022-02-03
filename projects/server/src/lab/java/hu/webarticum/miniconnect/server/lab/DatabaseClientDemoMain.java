package hu.webarticum.miniconnect.server.lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.server.ClientMessenger;
import hu.webarticum.miniconnect.server.ServerConstants;
import hu.webarticum.miniconnect.tool.repl.Repl;
import hu.webarticum.miniconnect.tool.repl.ReplRunner;
import hu.webarticum.miniconnect.tool.repl.SqlRepl;

public class DatabaseClientDemoMain {

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = ServerConstants.DEFAULT_PORT;
    
    
    public static void main(String[] args) throws IOException {
        String host = readInput("Host", argOrDefault(args, 0, DEFAULT_HOST));
        int port = readInt("Port", argOrDefault(args, 0, DEFAULT_PORT));
        runClientRepl(host, port);
    }
    
    private static void runClientRepl(String host, int port) {
        try (ClientMessenger clientMessenger = new ClientMessenger(host, port)) {
            MiniSessionManager sessionManager = new MessengerSessionManager(clientMessenger);
            try (MiniSession session = sessionManager.openSession()) {
                Repl repl = new SqlRepl(
                        session,
                        System.out); // NOSONAR
                new ReplRunner(repl, System.in).run();
            }
        }
    }

    private static Object argOrDefault(String[] args, int argIndex, Object defaultValue) {
        if (args.length > argIndex) {
            return args[argIndex];
        }
        return defaultValue;
    }

    private static int readInt(String name, Object defaultValue) throws IOException {
        while (true) {
            String input = readInput(name, defaultValue);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // continue
            }
        }
    }
    
    private static String readInput(String name, Object defaultValue) throws IOException {
        String prompt = name + " [" + defaultValue + "]: ";
        String value = readInput(prompt);
        return value.isEmpty() ? defaultValue.toString() : value;
    }
    
    private static String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
}
