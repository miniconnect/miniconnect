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
    
    private final Thread socketThread;
    
    private volatile boolean closed = false;
    
    
    public SocketClient(Socket socket, Consumer<Packet> consumer) {
        this.socket = socket;
        this.consumer = consumer;
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
    public void close() throws IOException {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        
        OutputStream out = socket.getOutputStream();
        out.write(TransferConstants.CLOSE_BYTE);
        out.flush();
        socket.close();
        
        try {
            socketThread.join(CLOSE_TIMEOUT_SECONDS * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
}
