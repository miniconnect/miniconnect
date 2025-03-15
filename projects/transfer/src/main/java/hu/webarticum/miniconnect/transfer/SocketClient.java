package hu.webarticum.miniconnect.transfer;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient implements Closeable {
    
    private static final int CLOSE_TIMEOUT_SECONDS = 20;
    
    
    private final Socket socket;
    
    private final Consumer<Packet> consumer;
    
    private final Consumer<Throwable> errorHandler;
    
    private final Thread socketThread;
    
    private volatile boolean closed = false;
    

    public SocketClient(Socket socket, Consumer<Packet> consumer) {
        this(socket, consumer, null);
    }
    
    public SocketClient(Socket socket, Consumer<Packet> consumer, Consumer<Throwable> errorHandler) {
        this.socket = socket;
        this.consumer = consumer;
        this.errorHandler = errorHandler;
        this.socketThread = new Thread(this::run);
        this.socketThread.start();
    }
    
    public void send(Packet packet) {
        if (closed) {
            throw new IllegalStateException("Client was already closed");
        }
        
        try {
            sendInternal(packet);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private void sendInternal(Packet packet) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(TransferConstants.PACKET_BYTE);
        new PacketWriter().write(packet, out);
    }

    private void run() {
        try {
            runThrowing();
        } catch (Throwable e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            }
        }
    }

    private void runThrowing() {
        SocketPacketFetcher fetcher = new SocketPacketFetcher(socket);
        while (!closed) {
            Packet packet = fetcher.fetch();
            if (packet == null) {
                break;
            }
            
            consumer.accept(packet);
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

        IOException socketCloseException = null;
        try {
            finalizeSocket();
        } catch (IOException e) {
            socketCloseException = e;
        }
        
        try {
            socketThread.join(CLOSE_TIMEOUT_SECONDS * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (socketCloseException != null) {
            throw new UncheckedIOException(socketCloseException);
        }
    }
    
    private void finalizeSocket() throws IOException {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(TransferConstants.CLOSE_BYTE);
            out.flush();
        } finally {
            socket.close();
        }
    }
    
}
