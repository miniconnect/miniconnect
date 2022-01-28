package hu.webarticum.miniconnect.messenger.adapter;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.Messenger;

public class MessengerSessionManager implements MiniSessionManager {
    
    private final Messenger messenger;
    

    public MessengerSessionManager(Messenger messenger) {
        this.messenger = messenger;
    }
    
    
    @Override
    public MiniSession openSession() {
        return new MessengerSession(messenger);
    }

}
