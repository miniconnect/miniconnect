package hu.webarticum.miniconnect.server.lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSessionManager;
import hu.webarticum.miniconnect.jdbcadapter.JdbcLargeDataPutter;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.impl.SessionManagerMessenger;
import hu.webarticum.miniconnect.server.MessengerServer;

public class DatabaseServerDemoMain {

    private static final int DEFAULT_SERVER_PORT = 54321;
    
    private static final String DEFAULT_JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    
    private static final String DEFAULT_JDBC_USERNAME = "";
    
    private static final String DEFAULT_JDBC_PASSWORD = "";
    
    private static final String DEFAULT_SET_STATEMENT = "SET @%s = ?";
    

    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort = readInt("Port", argOrDefault(args, 0, DEFAULT_SERVER_PORT));
        String jdbcUrl = readInput("Database URL", argOrDefault(args, 1, DEFAULT_JDBC_URL));
        String jdbcUsername =
                readInput("Database username", argOrDefault(args, 2, DEFAULT_JDBC_USERNAME));
        String jdbcPassword =
                readInput("Database password", argOrDefault(args, 3, DEFAULT_JDBC_PASSWORD));
        String setStatement =
                readInput("Set statement", argOrDefault(args, 4, DEFAULT_SET_STATEMENT));
        runServer(serverPort, jdbcUrl, jdbcUsername, jdbcPassword, setStatement);
    }

    private static void runServer(
            int serverPort,
            String jdbcUrl,
            String jdbcUsername,
            String jdbcPassword,
            String jdbcSetStatement
            ) throws IOException, InterruptedException {
        Supplier<JdbcLargeDataPutter> largeDataPutterFactory =
                () -> new SimpleJdbcLargeDataPutter(jdbcSetStatement);
        MiniSessionManager sessionManager =
                new JdbcAdapterSessionManager(jdbcUrl, jdbcUsername, jdbcPassword, largeDataPutterFactory);
        Messenger messenger = new SessionManagerMessenger(sessionManager);
        System.out.println();
        System.out.println(
                "Starting server on port " + serverPort + " over database " + jdbcUrl + "...");
        Thread serverThread;
        try (MessengerServer server = new MessengerServer(messenger, serverPort)) {
            serverThread = new Thread(server::listen);
            serverThread.start();
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.out.println("Stopping server...");
        }
        serverThread.join();
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
