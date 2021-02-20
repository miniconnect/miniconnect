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
    protected void acceptBlock(Block block) {
        acceptResponse(decodeResponse(block));
    }
    
    protected void sendRequest(Q request) throws IOException {
        sendBlock(encodeRequest(request));
    }
    
    protected abstract void acceptResponse(R response);

    protected abstract Block encodeRequest(Q request);
    
    protected abstract R decodeResponse(Block block);
    
}
