package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public interface ValueTranslator {

    public int length();
    
    public Object decode(MiniContentAccess contentAccess);

    public MiniContentAccess encode(Object value);
    
}
