package hu.webarticum.miniconnect.server.lob;

import java.io.IOException;

import hu.webarticum.miniconnect.util.data.ByteString;

public class MemoryAsynchronousLobAccess extends AbstractAsynchronousLobAccess {
    
    private final byte[] content;
    

    public MemoryAsynchronousLobAccess(int length) {
        super((long) length);
        this.content = new byte[length];
    }


    @Override
    public ByteString get() throws IOException {
        return get(0, content.length);
    }

    @Override
    protected ByteString loadPart(long start, int length) throws IOException {
        return ByteString.of(content, (int) start, length);
    }

    @Override
    protected void savePart(long start, ByteString part) throws IOException {
        part.extractTo(content, (int) start, 0, part.length());
    }

    @Override
    protected void checkClosed() {
        // nothing to do
    }
    
}
