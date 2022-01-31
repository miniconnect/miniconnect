package hu.webarticum.miniconnect.messenger.adapter;

import java.io.Closeable;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;

public class MessengerValue implements MiniValue, Closeable {
    
    private final MiniValueDefinition definition;
    
    private final boolean isNull;
    
    private final MiniContentAccess contentAccess;
    
    
    private boolean wasContentAccessRequested;
    

    public MessengerValue(
            MiniValueDefinition definition,
            boolean isNull,
            MiniContentAccess contentAccess) {
        this.definition = definition;
        this.isNull = isNull;
        this.contentAccess = contentAccess;
    }
    
    
    @Override
    public MiniValueDefinition definition() {
        return definition;
    }
    
    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public MiniContentAccess contentAccess() {
        wasContentAccessRequested = true;
        return contentAccess;
    }
    
    @Override
    public void close() {
        if (!wasContentAccessRequested) {
            contentAccess.close();
        }
    }

}
