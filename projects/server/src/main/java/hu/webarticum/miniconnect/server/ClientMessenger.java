package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;

public class ClientMessenger implements Messenger, Closeable {
    
    private final long sessionId;
    
    private final SocketClient socketClient;

    private final MessageDecoder decoder = new MessageDecoder();
    
    private final MessageEncoder encoder = new MessageEncoder();

    // TODO: When to remove the listening consumer? (lapsed listener problem)
    //       Do some of these:
    //         - use a weak map --> require Messenger clients to keep a hard reference to consumers
    //         - maximum concurrent number of consumers
    //         - use a lifetime
    //       Temporary solution: map with maximum capacity of 2
    //       Source of the LinkedHashMap idea:
    //         https://stackoverflow.com/a/11469731/3948862
    private final Map<Integer, Consumer<Response>> exchangeResponseConsumers =
            Collections.unmodifiableMap(new LinkedHashMap<Integer, Consumer<Response>>(
                    2, 0.7f, false) {
                private static final long serialVersionUID = 1L;
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Consumer<Response>> eldest) {
                    return size() > 2;
                }
            });
    
    
    public ClientMessenger(long sessionId, Socket socket) {
        this.sessionId = sessionId;
        this.socketClient = new SocketClient(socket, this::acceptResponsePacket);
    }
    

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof SessionMessage) {
            long requestSessionId = ((SessionMessage) request).sessionId();
            if (requestSessionId != sessionId) {
                throw new IllegalArgumentException(String.format(
                        "Alien session id: %d (expected: %d)", sessionId, requestSessionId));
            }
            if (responseConsumer != null && request instanceof ExchangeMessage) {
                int exchangeId = ((ExchangeMessage) request).exchangeId();
                exchangeResponseConsumers.put(exchangeId, responseConsumer);
            }
        }
        socketClient.send(encoder.encode(request));
    }
    
    public void acceptResponsePacket(Packet packet) {
        Response response = (Response) decoder.decode(packet);
        if (response instanceof SessionMessage) {
            long requestSessionId = ((SessionMessage) response).sessionId();
            if (requestSessionId == sessionId) {
                if (response instanceof ExchangeMessage) {
                    int exchangeId = ((ExchangeMessage) response).exchangeId();
                    Consumer<Response> responseConsumer = exchangeResponseConsumers.get(exchangeId);
                    responseConsumer.accept(response);
                }
            } else {
                // TODO alien response, what to do?
            }
        } else {
            // TODO non-session response, what to do
        }
    }

    @Override
    public void close() {
        socketClient.close();
    }

}
