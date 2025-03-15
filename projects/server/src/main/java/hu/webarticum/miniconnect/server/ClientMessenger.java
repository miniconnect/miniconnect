package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import hu.webarticum.miniconnect.util.GlobalIdGenerator;

public class ClientMessenger implements Messenger, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    
    private final SocketClient socketClient;

    private final MessageDecoder decoder;
    
    private final MessageEncoder encoder;

    private final Map<Consumer<Response>, ExchangeIdentity> exchangeResponseConsumers = new WeakHashMap<>();
    
    private final Object exchangeResponseConsumersLock = new Object();

    private final Map<Consumer<Response>, Instant> sessionInitConsumers = new WeakHashMap<>();
    
    private final Object sessionInitConsumersLock = new Object();
    

    public ClientMessenger(String host, int port) {
        this(openSocket(host, port), null);
    }

    public ClientMessenger(String host, int port, Consumer<Throwable> errorHandler) {
        this(openSocket(host, port), errorHandler);
    }
    
    public ClientMessenger(Socket socket, Consumer<Throwable> errorHandler) {
        this(socket, new DefaultMessageTranslator(), errorHandler);
    }

    public ClientMessenger(String host, int port, MessageTranslator translator, Consumer<Throwable> errorHandler) {
        this(openSocket(host, port), translator, errorHandler);
    }
    
    public ClientMessenger(Socket socket, MessageTranslator translator, Consumer<Throwable> errorHandler) {
        this(socket, translator, translator, errorHandler);
    }

    public ClientMessenger(
            String host, int port, MessageDecoder decoder, MessageEncoder encoder, Consumer<Throwable> errorHandler) {
        this(openSocket(host, port), decoder, encoder, errorHandler);
    }

    public ClientMessenger(
            Socket socket, MessageDecoder decoder, MessageEncoder encoder, Consumer<Throwable> errorHandler) {
        this.socketClient = new SocketClient(socket, this::acceptResponsePacket, errorHandler);
        this.decoder = decoder;
        this.encoder = encoder;
    }

    private static Socket openSocket(String host, int port) {
        logger.debug("Open client socket on {}:{}", host, port);
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        logger.trace("Request accepted: {}", request);
        
        if (responseConsumer == null) {
            // nothing to do
        } else if (request instanceof ExchangeMessage) {
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
        String logId = GlobalIdGenerator.generate(logger.isTraceEnabled());
        logger.trace("[{}] Response packet accepted", logId);
        
        Response response = (Response) decoder.decode(packet);
        logger.trace("[{}] Response parsed: {}", logId, response);
        
        if (response instanceof SessionMessage) {
            if (response instanceof ExchangeMessage) {
                logger.trace("[{}] Response is an ExchangeMessage", logId);
                long responseSessionId = ((SessionMessage) response).sessionId();
                ExchangeMessage exchangeMessage = (ExchangeMessage) response;
                int exchangeId = exchangeMessage.exchangeId();
                ExchangeIdentity exchangeIdentity = new ExchangeIdentity(responseSessionId, exchangeId);
                Consumer<Response> responseConsumer = findResponseConsumer(exchangeIdentity);
                if (responseConsumer != null) {
                    responseConsumer.accept(response);
                } else {
                    logger.trace("[{}] Unexpected exchange: {}/{}", logId, responseSessionId, exchangeId);
                }
            } else if (response instanceof SessionInitResponse) {
                logger.trace("[{}] Response is a SessionInitResponse", logId);
                acceptSessionInitResponse(response);
            } else {
                logger.debug("[{}] Stranger response, skip", logId);
            }
        } else {
            logger.debug("[{}] Unknown response type, skip", logId);
        }
    }
    
    private void acceptSessionInitResponse(Response response) {
        Consumer<Response> oldestConsumer = null;
        synchronized (sessionInitConsumersLock) {
            Instant oldestInstant = null;
            for (Map.Entry<Consumer<Response>, Instant> entry : sessionInitConsumers.entrySet()) {
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
    }
    
    private Consumer<Response> findResponseConsumer(ExchangeIdentity exchangeIdentity) {
        synchronized (exchangeResponseConsumersLock) {
            for (Map.Entry<Consumer<Response>, ExchangeIdentity> entry : exchangeResponseConsumers.entrySet()) {
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
            return (sessionId == otherExchangeIdentity.sessionId) && (exchangeId == otherExchangeIdentity.exchangeId);
        }
        
    }

}
