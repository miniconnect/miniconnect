package hu.webarticum.miniconnect.api;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public interface MiniValueDefinition {

    // TODO: specify a default set of built-in type names (correlating to jdbc types)
    public String type();

    public ImmutableMap<String, ByteString> properties();
}
