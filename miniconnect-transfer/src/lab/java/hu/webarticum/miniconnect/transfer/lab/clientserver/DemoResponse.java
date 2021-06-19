package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class DemoResponse {
    
    private final int exchangeId;
    
    private final String result;
    
    
    public DemoResponse(int exchangeId, String result) {
        this.exchangeId = exchangeId;
        this.result = result;
    }

    public static DemoResponse decode(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        int exchangeId = ByteUtil.bytesToInt(reader.read(4));
        String result = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        
        return new DemoResponse(exchangeId, result);
    }

    
    public int exchangeId() {
        return exchangeId;
    }

    public String result() {
        return result;
    }
    
    public Block encode() {
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(exchangeId))
                .append(ByteString.wrap(result.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return Block.dataOf(content);
    }
    
    @Override
    public String toString() {
        return String.format("%d:%s", exchangeId, result);
    }
    
}