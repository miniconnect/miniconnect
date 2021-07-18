package hu.webarticum.miniconnect.tool.contentaccess;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public interface ChargeableContentAccess extends MiniContentAccess {

    public void accept(long start, ByteString part);
    
    public boolean isCompleted();
    
}
