package hu.webarticum.miniconnect.transfer.channel.socket;

import java.io.IOException;
import java.net.Socket;

import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.channel.CloseableChannel;
import hu.webarticum.miniconnect.transfer.channel.lazysinglestream.LazySingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.channel.lazysinglestream.LazySingleStreamBlockTarget;

public class SocketChannel implements CloseableChannel {

    private final Socket socket;
    
    private final BlockSource source;
    
    private final BlockTarget target;
    
    
    public SocketChannel(Socket socket) {
        this.socket = socket;
        this.source = new LazySingleStreamBlockSource(socket::getInputStream);
        this.target = new LazySingleStreamBlockTarget(socket::getOutputStream);
    }
    
    
    @Override
    public BlockSource source() {
        return source;
    }

    @Override
    public BlockTarget target() {
        return target;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}
