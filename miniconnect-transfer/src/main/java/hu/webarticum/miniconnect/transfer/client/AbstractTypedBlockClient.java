package hu.webarticum.miniconnect.transfer.client;

import java.io.IOException;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;

public abstract class AbstractTypedBlockClient<Q, R> extends AbstractBlockClient {

    protected AbstractTypedBlockClient(BlockSource source, BlockTarget target) {
        super(source, target);
    }

    
    @Override
    protected void acceptBlockInternal(Block block) {
        acceptResponseInternal(decodeResponseInternal(block));
    }
    
    protected void sendRequestInternal(Q request) throws IOException {
        sendBlockInternal(encodeRequestInternal(request));
    }
    
    protected abstract void acceptResponseInternal(R response);

    protected abstract Block encodeRequestInternal(Q request);
    
    protected abstract R decodeResponseInternal(Block block);
    
}
