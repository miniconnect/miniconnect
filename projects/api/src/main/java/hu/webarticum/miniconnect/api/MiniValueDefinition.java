package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public interface MiniValueDefinition {

    // TODO: specify a default set of built-in values (correlating to jdbc types)
    public String type();

    public ImmutableMap<String, ByteString> properties();
}
