package hu.webarticum.miniconnect.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.server.translator.DefaultMessageTranslator;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.transfer.PacketExchanger;
import hu.webarticum.miniconnect.transfer.PacketTarget;
import hu.webarticum.miniconnect.transfer.SocketServer;
import hu.webarticum.miniconnect.util.GlobalIdGenerator;

public class MessengerServer implements Closeable {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    
    private final Messenger messenger;
    
    private final MessageDecoder decoder;
    
    private final MessageEncoder encoder;
    
    private final SocketServer socketServer;
    

    public MessengerServer(Messenger messenger) {
        this(messenger, openServerSocket());
    }
    
    public MessengerServer(Messenger messenger, int serverPort) {
        this(messenger, openServerSocket(serverPort));
    }
    
    public MessengerServer(Messenger messenger, ServerSocket serverSocket) {
        this(messenger, new DefaultMessageTranslator(), serverSocket);
    }

    public MessengerServer(Messenger messenger, MessageTranslator translator) {
        this(messenger, translator, openServerSocket());
    }

    public MessengerServer(Messenger messenger, MessageTranslator translator, int serverPort) {
        this(messenger, translator, openServerSocket(serverPort));
    }
    
    public MessengerServer(
            Messenger messenger,
            MessageTranslator translator,
            ServerSocket serverSocket) {
        this(messenger, translator, translator, serverSocket);
    }

    public MessengerServer(Messenger messenger, MessageDecoder decoder, MessageEncoder encoder) {
        this(messenger, decoder, encoder, openServerSocket());
    }

    public MessengerServer(
            Messenger messenger,
            MessageDecoder decoder,
            MessageEncoder encoder,
            int serverPort) {
        this(messenger, decoder, encoder, openServerSocket(serverPort));
    }

    public MessengerServer(
            Messenger messenger,
            MessageDecoder decoder,
            MessageEncoder encoder,
            ServerSocket serverSocket) {
        this.messenger = messenger;
        this.decoder = decoder;
        this.encoder = encoder;
        this.socketServer = new SocketServer(serverSocket, this::createExchanger);
    }

    private static ServerSocket openServerSocket() {
        return openServerSocket(ServerConstants.DEFAULT_PORT);
    }
    
    private static ServerSocket openServerSocket(int serverPort) {
        logger.debug("Open server socket on port {}", serverPort);
        try {
            return new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void listen() {
        logger.info("Start listening");
        try {
            socketServer.listen();
        } catch (Exception e) {
            logger.error("Stop listening due error", e);
        }
    }
    
    private PacketExchanger createExchanger() {
        return this::handle;
    }

    private void handle(Packet packet, PacketTarget responseTarget) {
        String logId = GlobalIdGenerator.generate(logger.isTraceEnabled());
        logger.trace("[{}] Packet accepted", logId);
        Request request = (Request) decoder.decode(packet);
        logger.trace("[{}] Packet parsed to request: {}", logId, request);
        messenger.accept(
                request,
                response -> receiveResponse(response, responseTarget, logId));
    }
    
    private void receiveResponse(Response response, PacketTarget responseTarget, String logId) {
        logger.trace("[{}] Response received: {}", logId, response);
        responseTarget.receive(encoder.encode(response));
    }

    @Override
    public void close() {
        logger.info("Close server");
        socketServer.close();
    }
    
}
