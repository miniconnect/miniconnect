package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.net.Socket;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.SessionInitResponse;
import hu.webarticum.miniconnect.server.translator.DefaultMessageTranslator;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.SocketClient;

public class ClientMessenger implements Messenger, Closeable {
    
    private final SocketClient socketClient;

    private final MessageDecoder decoder;
    
    private final MessageEncoder encoder;

    private final Map<Consumer<Response>, ExchangeIdentity> exchangeResponseConsumers =
            new WeakHashMap<>();
    
    private final Object exchangeResponseConsumersLock = new Object();

    private final Map<Consumer<Response>, Instant> sessionInitConsumers =
            new WeakHashMap<>();
    
    private final Object sessionInitConsumersLock = new Object();
    

    public ClientMessenger(Socket socket) {
        this(socket, new DefaultMessageTranslator());
    }

    public ClientMessenger(Socket socket, MessageTranslator translator) {
        this(socket, translator, translator);
    }
    
    public ClientMessenger(
            Socket socket,
            MessageDecoder decoder,
            MessageEncoder encoder) {
        this.socketClient = new SocketClient(socket, this::acceptResponsePacket);
        this.decoder = decoder;
        this.encoder = encoder;
    }
    

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof ExchangeMessage) {
            ExchangeMessage exchangeMessage = (ExchangeMessage) request;
            ExchangeIdentity exchangeIdentity = 
                    new ExchangeIdentity(exchangeMessage.sessionId(), exchangeMessage.exchangeId());
            synchronized (exchangeResponseConsumersLock) {
                exchangeResponseConsumers.put(responseConsumer, exchangeIdentity);
            }
        } else if (request instanceof SessionInitRequest) {
            synchronized (sessionInitConsumersLock) {
                sessionInitConsumers.put(responseConsumer, Instant.now());
            }
        }
        socketClient.send(encoder.encode(request));
    }
    
    public void acceptResponsePacket(Packet packet) {
        Response response = (Response) decoder.decode(packet);
        if (response instanceof SessionMessage) {
            long responseSessionId = ((SessionMessage) response).sessionId();
            if (response instanceof ExchangeMessage) {
                ExchangeMessage exchangeMessage = (ExchangeMessage) response;
                int exchangeId = exchangeMessage.exchangeId();
                ExchangeIdentity exchangeIdentity =
                        new ExchangeIdentity(responseSessionId, exchangeId);
                Consumer<Response> responseConsumer = findResponseConsumer(exchangeIdentity);
                if (responseConsumer != null) {
                    responseConsumer.accept(response);
                } else {
                    // TODO unexpected exchange id, what to do?
                }
            } else if (response instanceof SessionInitResponse) {
                Consumer<Response> oldestConsumer = null;
                synchronized (sessionInitConsumersLock) {
                    Instant oldestInstant = null;
                    for (
                            Map.Entry<Consumer<Response>, Instant> entry :
                            sessionInitConsumers.entrySet()) {
                        Instant instant = entry.getValue();
                        if (oldestInstant == null || instant.isBefore(oldestInstant)) {
                            oldestInstant = instant;
                            oldestConsumer = entry.getKey();
                        }
                    }
                    if (oldestConsumer != null) {
                        sessionInitConsumers.remove(oldestConsumer);
                    }
                }
                if (oldestConsumer != null) {
                    oldestConsumer.accept(response);
                }
            } else {
                // TODO handle open session!
                // TODO handle other non-exchange
            }
        } else {
            // TODO non-session response, what to do
        }
    }
    
    private Consumer<Response> findResponseConsumer(ExchangeIdentity exchangeIdentity) {
        synchronized (exchangeResponseConsumersLock) {
            for (Map.Entry<Consumer<Response>, ExchangeIdentity> entry :
                    exchangeResponseConsumers.entrySet()) {
                if (entry.getValue().equals(exchangeIdentity)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public void close() {
        socketClient.close();
    }
    
    
    private class ExchangeIdentity {
        
        private final long sessionId;
        
        private final int exchangeId;
        
        
        public ExchangeIdentity(long sessionId, int exchangeId) {
            this.sessionId = sessionId;
            this.exchangeId = exchangeId;
        }
        
        
        @Override
        public int hashCode() {
            return Objects.hash(sessionId, exchangeId);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof ExchangeIdentity)) {
                return false;
            }
            
            ExchangeIdentity otherExchangeIdentity = (ExchangeIdentity) other;
            return
                    sessionId == otherExchangeIdentity.sessionId &&
                    exchangeId == otherExchangeIdentity.exchangeId;
        }
        
    }

}
