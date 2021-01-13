package hu.webarticum.miniconnect.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.in.BlockInputChannel;
import hu.webarticum.miniconnect.protocol.channel.out.BlockOutputChannel;
import hu.webarticum.miniconnect.protocol.common.ByteString;

public class Server implements Runnable {
    
    private final MiniConnection connection;
    
    private final BlockInputChannel inputChannel;
    
    private final BlockOutputChannel outputChannel;
    

    public Server(
            MiniConnection connection,
            BlockInputChannel inputChannel,
            BlockOutputChannel outputChannel) {
        
        this.connection = connection;
        this.inputChannel = inputChannel;
        this.outputChannel = outputChannel;
    }


    @Override
    public void run() {
        while (iterate());
    }
    
    private boolean iterate() {
        try {
            iterateThrowing();
        } catch (IOException e) {
            // XXX
            return false;
        }
        return true;
    }
    
    // XXX
    private void iterateThrowing() throws IOException {
        Block block = inputChannel.fetch();
        
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Received content: ");
        for (byte contentByte : block.content().extract()) {
            messageBuilder.append(contentByte < 16 ? "0" : "");
            messageBuilder.append(Integer.toString(contentByte, 16));
            messageBuilder.append(' ');
        }
        String message = messageBuilder.toString();
        
        System.out.println(message);
        //outputChannel.send(new Block(ByteString.wrap(message.getBytes())));
    }
    
}
