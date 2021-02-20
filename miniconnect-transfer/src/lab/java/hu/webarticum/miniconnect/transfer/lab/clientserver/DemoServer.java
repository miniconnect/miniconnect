package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.server.AbstractBlockServer;
import hu.webarticum.miniconnect.transfer.server.ClientConnector;
import hu.webarticum.miniconnect.transfer.util.ByteString;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;

public class DemoServer extends AbstractBlockServer {
    
    private final UnaryOperator<String> transformer;
    

    public DemoServer(UnaryOperator<String> transformer) {
        this.transformer = transformer;
    }
    
    
    @Override
    protected void acceptBlock(ClientConnector connector, Block block) {
        ByteString.Reader reader = block.content().reader();
        
        int queryId = ByteUtil.bytesToInt(reader.read(4));
        String query = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        
        String answer = transformer.apply(query);
        ByteString result = ByteString.builder()
                .append(ByteUtil.intToBytes(queryId))
                .append(answer.getBytes(StandardCharsets.UTF_8))
                .build();
        
        try {
            connector.sendBlock(new Block(result));
        } catch (IOException e) {
            
            // XXX
            e.printStackTrace();
            
        }
    }

}
