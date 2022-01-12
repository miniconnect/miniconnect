package hu.webarticum.miniconnect.transfer;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SocketServer implements Closeable {
    
    private static final int MIN_THREAD_COUNT = 1;
    
    private static final int MAX_THREAD_COUNT = 64;
    
    private static final int THREAD_TIMEOUT_SECONDS = 60;
    
    private static final int CLOSE_TIMEOUT_SECONDS = 20;
    

    private final ServerSocket serverSocket;
    
    private final ExecutorService executorService;
    
    private final Supplier<PacketExchanger> exchangerFactory;
    
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
        } catch (IOException e) {
            if (closed) {
                return;
            } else {
                throw new UncheckedIOException(e);
            }
        }
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
        if (closed) {
            sendCloseMessage(clientSocket);
        }
    }
    
    private void sendCloseMessage(Socket clientSocket) {
        try {
            clientSocket.getOutputStream().write(TransferConstants.CLOSE_BYTE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private void closeClientSocket(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (Exception e) {
            // nothing to do
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
    
}
