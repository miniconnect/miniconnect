package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public interface MiniColumnHeader {

    public String name();

    public String type();

    public ImmutableMap<String, ByteString> properties();

}
