package hu.webarticum.miniconnect.api;

import java.io.IOException;

import hu.webarticum.miniconnect.util.data.ByteString;

public interface MiniValue {

    public MiniValueDefinition definition();

    public boolean isNull();
    
    public boolean isLob();

    public long length();

    public ByteString content();
    
    public MiniLobAccess lobAccess() throws IOException;

}
