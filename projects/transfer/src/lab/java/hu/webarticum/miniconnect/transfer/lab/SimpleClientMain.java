package hu.webarticum.miniconnect.transfer.lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;

public class SimpleClientMain {
    
    private static final String DEFAULT_HOST = "localhost";
    
    private static final int DEFAULT_PORT = 54321;
    

    public static void main(String[] args) throws IOException {
        String host = readHost(args);
        int port = readPort(args);
        
        System.out.println();
        System.out.println("Connect to " + host + ":" + port + " ...");
        System.out.println();
        System.out.println("Submit any non-empty input.");
        System.out.println("The server will transform it like:");
        System.out.println("  'lorem ipsum' --> 'LOREM_IPSUM'");
        System.out.println("Submit empty input to exit");
        
        Socket socket = new Socket(host, port);
        Consumer<Packet> consumer = SimpleClientMain::consume;
        try (SocketClient client = new SocketClient(socket, consumer)) {
            while (true) {
                System.out.println();
                String input = readInput("Input: ");
                if (input.isEmpty()) {
                    break;
                }
                client.send(Packet.of(ByteString.empty(), ByteString.of(input)));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            System.out.println();
            System.out.println("Client closed");
        }
    }
    
    private static String readHost(String[] args) throws IOException {
        if (args.length >= 1) {
            return args[0];
        }
        
        String hostInput = readInput("Host [" + DEFAULT_HOST + "]: ");
        return hostInput.isEmpty() ? DEFAULT_HOST : hostInput;
    }

    private static int readPort(String[] args) throws IOException {
        if (args.length >= 2) {
            return Integer.parseInt(args[1]);
        }

        String portString = readInput("Port [" + DEFAULT_PORT + "]: ");
        return portString.isEmpty() ? DEFAULT_PORT : Integer.parseInt(portString);
    }
    
    private static String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    private static void consume(Packet response) {
        String output = response.payload().toString();
        System.out.println("Output: " + output);
    }
    
}
