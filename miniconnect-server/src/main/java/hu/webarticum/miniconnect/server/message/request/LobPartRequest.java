package hu.webarticum.miniconnect.server.message.request;

import hu.webarticum.miniconnect.util.data.ByteString;

public final class LobPartRequest implements Request {

    private final int lobId;

    private final long offset;

    private final ByteString content;


    public LobPartRequest(int lobId, long offset, ByteString content) {
        this.lobId = lobId;
        this.offset = offset;
        this.content = content;
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
