package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.ServerSocket;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.PacketExchanger;
import hu.webarticum.miniconnect.transfer.PacketTarget;
import hu.webarticum.miniconnect.transfer.SocketServer;

public class MessengerServer implements Closeable {
    
    private final Messenger messenger;
    
    private final SocketServer socketServer;
    
    private final MessageDecoder decoder = new MessageDecoder();
    
    private final MessageEncoder encoder = new MessageEncoder();
    

    public MessengerServer(ServerSocket serverSocket, Messenger messenger) {
        this.messenger = messenger;
        this.socketServer = new SocketServer(serverSocket, this::createExchanger);
    }
    
    
    private PacketExchanger createExchanger() {
        return this::handle;
    }

    private void handle(Packet packet, PacketTarget responseTarget) {
        Request request = (Request) decoder.decode(packet);
        messenger.accept(
                request,
                response -> responseTarget.receive(encoder.encode(response)));
    }

    @Override
    public void close() {
        socketServer.close();
    }
    
}
