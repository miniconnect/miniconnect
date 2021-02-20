package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteString;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;

public class DemoRequest {
    
    private final int queryId;
    
    private final String query;
    
    
    public DemoRequest(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        this.queryId = ByteUtil.bytesToInt(reader.read(4));
        this.query = new String(reader.readRemaining(), StandardCharsets.UTF_8);
    }
    
    public DemoRequest(int queryId, String query) {
        this.queryId = queryId;
        this.query = query;
    }
    
    
    public int getQueryId() {
        return queryId;
    }

    public String getQuery() {
        return query;
    }

    public Block encode() {
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(queryId))
                .append(ByteString.wrap(query.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return new Block(content);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%s", queryId, query);
    }
    
}