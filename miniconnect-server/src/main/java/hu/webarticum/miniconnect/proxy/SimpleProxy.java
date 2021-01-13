package hu.webarticum.miniconnect.proxy;

import java.io.IOException;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.in.BlockInputChannel;
import hu.webarticum.miniconnect.protocol.channel.out.BlockOutputChannel;

public class SimpleProxy implements Runnable {

    private final BlockInputChannel inputChannel;
    
    private final BlockOutputChannel outputChannel;


    public SimpleProxy(BlockInputChannel inputChannel, BlockOutputChannel outputChannel) {
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
    
    private void iterateThrowing() throws IOException {
        Block block = inputChannel.fetch();
        outputChannel.send(block);
    }

}
