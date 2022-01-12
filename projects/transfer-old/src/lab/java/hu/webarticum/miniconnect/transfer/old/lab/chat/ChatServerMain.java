package hu.webarticum.miniconnect.transfer.old.lab.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.singlestream.SingleStreamBlockTarget;

public class ChatServerMain {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Listen port: ");
        int listenPort = Integer.parseInt(in.readLine());

        System.out.print("Nick name: ");
        String senderName = in.readLine();
        
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("Waiting for peer...");
            try (Socket socket = serverSocket.accept()) {
                System.out.println(String.format("Connected: %s", socket.getRemoteSocketAddress()));
                
                BlockSource source = new SingleStreamBlockSource(socket.getInputStream());
                BlockTarget target = new SingleStreamBlockTarget(socket.getOutputStream());
                
                Appendable out = System.out;
                
                try (ChatClient chatClient = ChatClient.start(in, out, senderName, source, target)) {
                    chatClient.loop();
                }
            }
        }
    }

}
