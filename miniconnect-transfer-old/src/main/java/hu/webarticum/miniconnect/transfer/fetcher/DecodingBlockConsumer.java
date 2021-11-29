package hu.webarticum.miniconnect.transfer.fetcher;

import java.util.function.Consumer;
import java.util.function.Function;

import hu.webarticum.miniconnect.transfer.Block;

public class DecodingBlockConsumer<T> implements Consumer<Block> {
    
    private final Function<Block, T> decoder;
    
    private final Consumer<T> consumer;
    

    public DecodingBlockConsumer(Function<Block, T> decoder, Consumer<T> consumer) {
        this.decoder = decoder;
        this.consumer = consumer;
    }
    
    
    @Override
    public void accept(Block block) {
        consumer.accept(decoder.apply(block));
    }

}
