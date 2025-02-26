package hu.webarticum.miniconnect.transfer;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServer implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    
    private static final int MIN_THREAD_COUNT = 1;
    
    private static final int MAX_THREAD_COUNT = 64;
    
    private static final int THREAD_TIMEOUT_SECONDS = 60;
    
    private static final int CLOSE_TIMEOUT_SECONDS = 20;
    

    private final ServerSocket serverSocket;
    
    private final ExecutorService executorService;
    
    private final Supplier<PacketExchanger> exchangerFactory;
    
    private final Set<Socket> clientSockets = Collections.newSetFromMap(new IdentityHashMap<>());
    
    private final Object clientSocketsLock = new Object();
    
    private volatile boolean closed = false;
    
    
    public SocketServer(ServerSocket serverSocket, Supplier<PacketExchanger> exchangerFactory) {
        this.serverSocket = serverSocket;
        this.executorService = new ThreadPoolExecutor(
                MIN_THREAD_COUNT,
                MAX_THREAD_COUNT,
                THREAD_TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                new SynchronousQueue<>());
        this.exchangerFactory = exchangerFactory;
    }
    
    
    public void listen() {
        while (!closed) {
            acceptNextClient();
        }
    }
    
    private void acceptNextClient() {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            clientSocket.setTcpNoDelay(true);
        } catch (IOException e) {
            if (closed) {
                return;
            } else {
                throw new UncheckedIOException(e);
            }
        }
        registerClientSocket(clientSocket);
        executorService.execute(() -> runClientSocket(clientSocket));
    }

    private void runClientSocket(Socket clientSocket) {
        try {
            runClientSocketThrowing(clientSocket);
        } finally {
            closeClientSocket(clientSocket);
        }
    }

    private void runClientSocketThrowing(Socket clientSocket) {
        SocketPacketFetcher fetcher = new SocketPacketFetcher(clientSocket);
        while (!closed) {
            Packet packet = fetcher.fetch();
            if (packet == null) {
                break;
            }
            
            PacketExchanger exchanger = exchangerFactory.get();
            PacketTarget responseTarget = new SocketPacketTarget(clientSocket);
            exchanger.handle(packet, responseTarget);
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }

        closeAllRegisteredClientSockets();

        IOException serverSocketCloseException = null;
        try {
            serverSocket.close();
        } catch (IOException e) {
            serverSocketCloseException = e;
        } catch (Exception e) {
            // nothing to do
        }
        
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(CLOSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // nothing to do
        }
        
        if (serverSocketCloseException != null) {
            throw new UncheckedIOException(serverSocketCloseException);
        }
    }

    private void registerClientSocket(Socket clientSocket) {
        synchronized (clientSocketsLock) {
            clientSockets.add(clientSocket);
            logger.info("Client accepted: {}", clientSocket);
        }
    }
    
    private void closeAllRegisteredClientSockets() {
        List<Socket> clientSocketsToClose;
        synchronized (clientSocketsLock) {
            clientSocketsToClose = new ArrayList<>(clientSockets);
        }
        for (Socket clientSocket : clientSocketsToClose) {
            closeClientSocket(clientSocket);
        }
    }

    private void closeClientSocket(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (Exception e) {
            // nothing to do
        }
        synchronized (clientSocketsLock) {
            clientSockets.remove(clientSocket);
        }
    }

}
