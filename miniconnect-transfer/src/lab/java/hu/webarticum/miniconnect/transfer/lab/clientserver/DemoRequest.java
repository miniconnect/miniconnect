package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class DemoRequest {
    
    private final int queryId;
    
    private final String query;
    
    
    public DemoRequest(int queryId, String query) {
        this.queryId = queryId;
        this.query = query;
    }

    public static DemoRequest decode(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        int queryId = ByteUtil.bytesToInt(reader.read(4));
        String query = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        
        return new DemoRequest(queryId, query);
    }
    
    
    public int queryId() {
        return queryId;
    }

    public String query() {
        return query;
    }

    public Block encode() {
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(queryId))
                .append(ByteString.wrap(query.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return Block.dataOf(content);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%s", queryId, query);
    }
    
}