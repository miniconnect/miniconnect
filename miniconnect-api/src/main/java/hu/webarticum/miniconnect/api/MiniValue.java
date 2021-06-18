package hu.webarticum.miniconnect.api;

import java.io.IOException;

import hu.webarticum.miniconnect.util.data.ByteString;

public interface MiniValue {

    public MiniValueDefinition definition();

    public boolean isNull();
    
    // TODO: remove
    public boolean isLob();

    // TODO: remove
    public long length();

    // TODO: remove
    public ByteString content();

    // TODO: public MiniContentAccess contentAccess() throws IOException;
    // FIXME: IOException -> UncheckedIOException ?
    public MiniLobAccess lobAccess() throws IOException;

}
