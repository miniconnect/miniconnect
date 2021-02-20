package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteString;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;

public class DemoResponse {
    
    private final int queryId;
    
    private final String result;
    
    
    public DemoResponse(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        this.queryId = ByteUtil.bytesToInt(reader.read(4));
        this.result = new String(reader.readRemaining(), StandardCharsets.UTF_8);
    }

    public DemoResponse(int queryId, String result) {
        this.queryId = queryId;
        this.result = result;
    }
    
    
    public int getQueryId() {
        return queryId;
    }

    public String getResult() {
        return result;
    }
    
    public Block encode() {
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(queryId))
                .append(ByteString.wrap(result.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return new Block(content);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%s", queryId, result);
    }
    
}