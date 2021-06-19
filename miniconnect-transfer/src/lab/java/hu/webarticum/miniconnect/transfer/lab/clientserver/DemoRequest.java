package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class DemoRequest {
    
    private final int exchangeId;
    
    private final String query;
    
    
    public DemoRequest(int exchangeId, String query) {
        this.exchangeId = exchangeId;
        this.query = query;
    }

    public static DemoRequest decode(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        int exchangeId = ByteUtil.bytesToInt(reader.read(4));
        String query = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        
        return new DemoRequest(exchangeId, query);
    }
    
    
    public int exchangeId() {
        return exchangeId;
    }

    public String query() {
        return query;
    }

    public Block encode() {
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(exchangeId))
                .append(ByteString.wrap(query.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return Block.dataOf(content);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%s", exchangeId, query);
    }
    
}