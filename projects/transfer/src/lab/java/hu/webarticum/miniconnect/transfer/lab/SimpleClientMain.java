package hu.webarticum.miniconnect.transfer.lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;
import hu.webarticum.miniconnect.util.data.ByteString;

public class SimpleClientMain {

    public static void main(String[] args) throws IOException {
        String host = readInput("Host: ");
        int port = Integer.parseInt(readInput("Port: "));
        System.out.println("Connect to " + host + ":" + port);
        System.out.println();
        
        Socket socket = new Socket(host, port);
        Consumer<Packet> consumer = SimpleClientMain::consume;
        try (SocketClient client = new SocketClient(socket, consumer)) {
            while (true) {
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
                System.out.println();
            }
        } finally {
            System.out.println("Client closed");
        }
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
