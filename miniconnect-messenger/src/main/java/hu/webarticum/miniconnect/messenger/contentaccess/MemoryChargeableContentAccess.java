package hu.webarticum.miniconnect.messenger.contentaccess;

import hu.webarticum.miniconnect.util.data.ByteString;

public class MemoryChargeableContentAccess extends AbstractChargeableContentAccess {
    
    private final byte[] content;
    

    public MemoryChargeableContentAccess(int length) {
        super((long) length);
        this.content = new byte[length];
    }


    @Override
    public boolean isLarge() {
        return false;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public ByteString get() {
        return get(0, content.length);
    }

    @Override
    protected ByteString loadPart(long start, int length) {
        return ByteString.of(content, (int) start, length);
    }

    @Override
    protected void savePart(long start, ByteString part) {
        part.extractTo(content, (int) start, 0, part.length());
    }

    @Override
    protected void checkClosed() {
        // nothing to do
    }
    
}
