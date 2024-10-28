package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;

public final class SessionInitResponse implements Response, SessionMessage {

    private static final long serialVersionUID = -3866586038374504295L;
    
    
    private final long sessionId;


    public SessionInitResponse(long sessionId) {
        this.sessionId = sessionId;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(sessionId);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof SessionInitResponse)) {
            return false;
        }
        
        SessionInitResponse otherLargeDataSaveResponse = (SessionInitResponse) other;
        return sessionId == otherLargeDataSaveResponse.sessionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .build();
    }

}
