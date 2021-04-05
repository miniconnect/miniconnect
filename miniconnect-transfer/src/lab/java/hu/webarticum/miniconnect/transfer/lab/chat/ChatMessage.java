package hu.webarticum.miniconnect.transfer.lab.chat;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class ChatMessage {

    private final String senderName;
    
    private final String message;
    

    public ChatMessage(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }
    
    public static ChatMessage decode(Block block) {
        ByteString.Reader reader = block.content().reader();
        
        int senderNameLength = ByteUtil.bytesToInt(reader.read(4));
        String senderName = new String(reader.read(senderNameLength), StandardCharsets.UTF_8);
        String message = new String(reader.readRemaining(), StandardCharsets.UTF_8);
        
        return new ChatMessage(senderName, message);
    }


    public String senderName() {
        return senderName;
    }

    public String message() {
        return message;
    }
    
    public Block encode() {
        byte[] senderNameBytes = senderName.getBytes(StandardCharsets.UTF_8);
        ByteString content = ByteString.builder()
                .append(ByteUtil.intToBytes(senderNameBytes.length))
                .append(ByteString.wrap(senderNameBytes))
                .append(ByteString.wrap(message.getBytes(StandardCharsets.UTF_8)))
                .build();
        
        return Block.dataOf(content);
    }
    
}
