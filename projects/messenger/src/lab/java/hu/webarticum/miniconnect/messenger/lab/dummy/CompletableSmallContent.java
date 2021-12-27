package hu.webarticum.miniconnect.messenger.lab.dummy;

import java.util.SortedMap;
import java.util.TreeMap;

import hu.webarticum.miniconnect.util.data.ByteString;

public class CompletableSmallContent {

    private final ByteString.Builder builder = ByteString.builder();
    
    private final SortedMap<Integer, ByteString> subsequentParts = new TreeMap<>();

    private int length = -1;
    
    
    public void setLength(int length) {
        if (this.length >= 0) {
            throw new IllegalStateException("Length is already set");
        }
        
        this.length = length;
    }
    
    public void put(int offset, ByteString part) {
        int position = builder.length();
        
        if (offset < position) {
            throw new IllegalStateException("Already filled position");
        }
        
        if (offset > position) {
            subsequentParts.put(offset, part);
            return;
        }
        
        builder.append(part);
        while (!subsequentParts.isEmpty()) {
            Integer nextKey = subsequentParts.firstKey();
            
            if (nextKey < position) {
                throw new IllegalStateException("Already filled position");
            } else if (nextKey > position) {
                break;
            }
            
            ByteString nextContent = subsequentParts.get(nextKey);
            builder.append(nextContent);
            
            subsequentParts.remove(nextKey);
        }
    }

    public boolean completed() {
        return length >= 0 && builder.length() >= length;
    }

    public ByteString content() {
        if (!completed()) {
            throw new IllegalStateException("Incomplete content");
        }
        
        return builder.build();
    }
    
}
