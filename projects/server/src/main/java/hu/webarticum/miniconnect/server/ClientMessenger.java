package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.Socket;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.server.translator.DefaultMessageTranslator;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;

public class ClientMessenger implements Messenger, Closeable {
    
    private final long sessionId;
    
    private final SocketClient socketClient;

    private final MessageDecoder decoder;
    
    private final MessageEncoder encoder;

    private final WeakHashMap<Consumer<Response>, Integer> exchangeResponseConsumers =
            new WeakHashMap<>();
    

    public ClientMessenger(long sessionId, Socket socket) {
        this(sessionId, socket, new DefaultMessageTranslator());
    }

    public ClientMessenger(long sessionId, Socket socket, MessageTranslator translator) {
        this(sessionId, socket, translator, translator);
    }
    
    public ClientMessenger(
            long sessionId,
            Socket socket,
            MessageDecoder decoder,
            MessageEncoder encoder) {
        this.sessionId = sessionId;
        this.socketClient = new SocketClient(socket, this::acceptResponsePacket);
        this.decoder = decoder;
        this.encoder = encoder;
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
                exchangeResponseConsumers.put(responseConsumer, exchangeId);
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
                    Consumer<Response> responseConsumer = findResponseConsumer(exchangeId);
                    if (responseConsumer != null) {
                        responseConsumer.accept(response);
                    } else {
                        // TODO unexpected exchange id, what to do?
                    }
                }
            } else {
                // TODO alien response, what to do?
            }
        } else {
            // TODO non-session response, what to do
        }
    }
    
    private Consumer<Response> findResponseConsumer(int exchangeId) {
        for (Map.Entry<Consumer<Response>, Integer> entry : exchangeResponseConsumers.entrySet()) {
            if (entry.getValue() == exchangeId) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void close() {
        socketClient.close();
    }

}
