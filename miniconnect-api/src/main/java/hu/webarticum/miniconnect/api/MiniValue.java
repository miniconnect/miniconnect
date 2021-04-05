package hu.webarticum.miniconnect.api;

import java.io.InputStream;

// FIXME: rename?
public interface MiniValue {

    public boolean isNull();

    // FIXME: public byte[] retrieve(int start, int length); ?

    // TODO: long
    public int contentLength();

    // TODO: immutable access
    public byte[] content();

    public InputStream inputStream();

}
