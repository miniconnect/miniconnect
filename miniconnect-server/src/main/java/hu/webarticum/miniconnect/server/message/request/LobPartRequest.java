package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.server.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;

public final class LobPartRequest implements Request, SessionMessage {

    private final long sessionId;

    private final int lobId;

    private final long offset;

    private final ByteString content;


    public LobPartRequest(long sessionId, int lobId, long offset, ByteString content) {
        this.sessionId = sessionId;
        this.lobId = lobId;
        this.offset = offset;
        this.content = content;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int lobId() {
        return lobId;
    }

    public long offset() {
        return offset;
    }

    public ByteString content() {
        return content;
    }

}
