package hu.webarticum.miniconnect.transfer.lab.util;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.util.ByteString;

public final class BlockUtil {
    
    private BlockUtil() {
        // utility class
    }
    

    public static String stringOf(Block block) {
        return block.content().toString(StandardCharsets.UTF_8);
    }

    public static Block blockOf(String string) {
        ByteString content = ByteString.wrap(string.getBytes(StandardCharsets.UTF_8));
        return new Block(content);
    }
    
}
