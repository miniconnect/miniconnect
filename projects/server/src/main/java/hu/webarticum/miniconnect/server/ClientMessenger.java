package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.Socket;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;

public class ClientMessenger implements Messenger, Closeable {
    
    private final SocketClient socketClient;

    private final MessageDecoder decoder = new MessageDecoder();
    
    private final MessageEncoder encoder = new MessageEncoder();
    
    
    public ClientMessenger(Socket socket) {
        this.socketClient = new SocketClient(socket, this::acceptResponsePacket);
    }
    

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        socketClient.send(encoder.encode(request));
        
        // TODO: how to detect responses to this request?
        //         (by exchange id...)
        // TODO: when to remove the listening consumer? 
        //         (maximum concurrent number + weak map + a long lifetime)
        
    }
    
    public void acceptResponsePacket(Packet packet) {
        Response reponse = (Response) decoder.decode(packet);
        
        // TODO (see accept)
        
    }

    @Override
    public void close() {
        socketClient.close();
    }

}
