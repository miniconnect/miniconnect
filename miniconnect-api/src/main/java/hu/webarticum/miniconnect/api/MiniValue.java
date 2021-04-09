package hu.webarticum.miniconnect.api;

import java.io.InputStream;

import hu.webarticum.miniconnect.util.data.ByteString;

// FIXME: rename?
// FIXME: Closeable? closeStorage?(blob)
public interface MiniValue {

    public boolean isNull();

    // FIXME: public ByteString retrieve(long start, int length); ?

    // TODO: long
    public int contentLength();

    // TODO: immutable access
    public ByteString content();

    public InputStream inputStream();

}
