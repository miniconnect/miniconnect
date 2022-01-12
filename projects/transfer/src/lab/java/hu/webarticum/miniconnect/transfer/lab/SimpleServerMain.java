package hu.webarticum.miniconnect.transfer.lab;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Supplier;

import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.PacketExchanger;
import hu.webarticum.miniconnect.transfer.PacketTarget;
import hu.webarticum.miniconnect.transfer.SocketServer;
import hu.webarticum.miniconnect.util.data.ByteString;

public class SimpleServerMain {
    
    public static void main(String[] args) throws IOException {
        int port = 9999;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        
        System.out.println("Start listening on port " + port);
        ServerSocket serverSocket = new ServerSocket(port);
        Supplier<PacketExchanger> exchanger = () -> SimpleServerMain::handlePacket;
        SocketServer server = new SocketServer(serverSocket, exchanger);
        Thread serverThread = new Thread(server::listen);
        serverThread.start();
        
        System.in.read();
        server.close();
        
        System.out.println("Server stopped");
    }
    
    private static void handlePacket(Packet request, PacketTarget responseTarget) {
        String input = request.payload().toString();
        String output = input.toUpperCase().replace(' ', '_');
        System.out.println("Incoming request (input: " + input + ", output: " + output + ")");
        Packet response = Packet.of(ByteString.empty(), ByteString.of(output));
        responseTarget.receive(response);
    }
    
}
