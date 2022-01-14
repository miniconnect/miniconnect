package hu.webarticum.miniconnect.transfer.lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.PacketExchanger;
import hu.webarticum.miniconnect.transfer.PacketTarget;
import hu.webarticum.miniconnect.transfer.SocketServer;
import hu.webarticum.miniconnect.util.data.ByteString;

public class SimpleServerMain {
    
    private static final int DEFAULT_PORT = 9999;
    
    
    public static void main(String[] args) throws IOException {
        int port = readPort(args);

        System.out.println();
        System.out.println("Start listening on port " + port);
        System.out.println("Press ENTER to exit");
        System.out.println();
        ServerSocket serverSocket = new ServerSocket(port);
        Supplier<PacketExchanger> exchanger = () -> SimpleServerMain::handlePacket;
        SocketServer server = new SocketServer(serverSocket, exchanger);
        Thread serverThread = new Thread(server::listen);
        serverThread.start();
        
        System.in.read();
        server.close();
        
        System.out.println("Server stopped");
    }

    private static int readPort(String[] args) throws IOException {
        if (args.length >= 1) {
            return Integer.parseInt(args[0]);
        }

        String portString = readInput("Port [" + DEFAULT_PORT + "]: ");
        return portString.isEmpty() ? DEFAULT_PORT : Integer.parseInt(portString);
    }

    private static String readInput(String prompt) throws IOException {
        System.out.print(prompt);
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    private static void handlePacket(Packet request, PacketTarget responseTarget) {
        String input = request.payload().toString();
        String output = input.toUpperCase().replace(' ', '_');
        System.out.println("Incoming request (input: " + input + ", output: " + output + ")");
        Packet response = Packet.of(ByteString.empty(), ByteString.of(output));
        responseTarget.receive(response);
    }
    
}
