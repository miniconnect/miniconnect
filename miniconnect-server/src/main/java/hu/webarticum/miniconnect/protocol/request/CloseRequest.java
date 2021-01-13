package hu.webarticum.miniconnect.protocol.request;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public class CloseRequest implements Request {

    private static final Type TYPE = Request.Type.CLOSE;
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public ByteString encode() {
        byte[] contentBytes = { TYPE.flag() };
        return ByteString.wrap(contentBytes);
    }

}
