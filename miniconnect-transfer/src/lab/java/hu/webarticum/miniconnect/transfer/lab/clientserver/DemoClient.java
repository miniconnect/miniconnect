package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.client.AbstractTransactionalBlockClient;

public class DemoClient extends AbstractTransactionalBlockClient<DemoRequest, DemoResponse> {
    
    private final AtomicInteger queryIdCounter = new AtomicInteger(1);
    

    public DemoClient(BlockSource source, BlockTarget target) {
        super(source, target);
    }


    @Override
    protected Block encodeRequest(DemoRequest request) {
        return request.encode();
    }
    
    @Override
    protected DemoResponse parseResponse(Block block) {
        return new DemoResponse(block);
    }
    
    protected void acceptStandaloneResponse(DemoResponse demoResponse) {
        System.err.println("Unexpected response: " + demoResponse);
    }
    
    public String query(String query) throws IOException {
        int queryId = queryIdCounter.getAndIncrement();
        DemoRequest request = new DemoRequest(queryId, query);
        
        DemoResponse response = sendAndWaitForResponse(
                request,
                r -> r.getQueryId() == queryId);
        
        return response.getResult();
    }
    
}
