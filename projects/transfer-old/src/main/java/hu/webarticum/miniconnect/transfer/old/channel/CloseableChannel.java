package hu.webarticum.miniconnect.transfer.old.channel;

import java.io.Closeable;

public interface CloseableChannel extends Closeable {

    public BlockSource source();

    public BlockTarget target();
    
}
