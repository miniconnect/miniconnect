package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;

public final class LobRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int id;

    private final long length;


    public LobRequest(long sessionId, int id, long length) {
        this.sessionId = sessionId;
        this.id = id;
        this.length = length;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int id() {
        return id;
    }

    public long length() {
        return length;
    }

}
