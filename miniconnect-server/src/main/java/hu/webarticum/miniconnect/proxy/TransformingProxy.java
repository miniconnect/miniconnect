package hu.webarticum.miniconnect.proxy;

import java.io.IOException;
import java.util.function.UnaryOperator;

import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.in.BlockInputChannel;
import hu.webarticum.miniconnect.protocol.channel.out.BlockOutputChannel;

public class TransformingProxy implements Runnable {

    private final BlockInputChannel inputChannel;
    
    private final BlockOutputChannel outputChannel;
    
    private final UnaryOperator<Block> transformer;


    public TransformingProxy(
            BlockInputChannel inputChannel,
            BlockOutputChannel outputChannel,
            UnaryOperator<Block> transformer) {
        
        this.inputChannel = inputChannel;
        this.outputChannel = outputChannel;
        this.transformer = transformer;
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
        Block transformedBlock = transformer.apply(block);
        outputChannel.send(transformedBlock);
    }

}
