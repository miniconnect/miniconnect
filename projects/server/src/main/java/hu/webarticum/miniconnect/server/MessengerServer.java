package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.ServerSocket;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.server.translator.DefaultMessageTranslator;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.PacketExchanger;
import hu.webarticum.miniconnect.transfer.PacketTarget;
import hu.webarticum.miniconnect.transfer.SocketServer;

public class MessengerServer implements Closeable {
    
    private final SocketServer socketServer;
    
    private final Messenger messenger;
    
    private final MessageDecoder decoder;
    
    private final MessageEncoder encoder;
    

    public MessengerServer(ServerSocket serverSocket, Messenger messenger) {
        this(serverSocket, messenger, new DefaultMessageTranslator());
    }

    public MessengerServer(
            ServerSocket serverSocket,
            Messenger messenger,
            MessageTranslator translator) {
        this(serverSocket, messenger, translator, translator);
    }
    
    public MessengerServer(
            ServerSocket serverSocket,
            Messenger messenger,
            MessageDecoder decoder,
            MessageEncoder encoder) {
        this.socketServer = new SocketServer(serverSocket, this::createExchanger);
        this.messenger = messenger;
        this.decoder = decoder;
        this.encoder = encoder;
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
