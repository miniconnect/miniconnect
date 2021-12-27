package hu.webarticum.miniconnect.messenger.adapter;

import java.io.Closeable;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;

public class MessengerValue implements MiniValue, Closeable {
    
    private final MiniValueDefinition definition;
    
    private final MiniContentAccess contentAccess;
    
    
    private boolean wasContentAccessRequested;
    

    public MessengerValue(MiniValueDefinition definition, MiniContentAccess contentAccess) {
        this.definition = definition;
        this.contentAccess = contentAccess;
    }
    
    
    @Override
    public MiniValueDefinition definition() {
        return definition;
    }
    
    @Override
    public boolean isNull() {
        return false;
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
