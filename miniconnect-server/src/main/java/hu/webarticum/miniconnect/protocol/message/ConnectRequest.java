package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public class ConnectRequest implements Request {

    private static final Type TYPE = Request.Type.CONNECT;
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder().append(TYPE.flag()).build();
    }

}
