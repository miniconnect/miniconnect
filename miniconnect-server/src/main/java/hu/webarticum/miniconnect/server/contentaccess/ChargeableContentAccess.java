package hu.webarticum.miniconnect.server.contentaccess;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public interface ChargeableContentAccess extends MiniContentAccess {

    public void accept(long start, ByteString part);
    
}
