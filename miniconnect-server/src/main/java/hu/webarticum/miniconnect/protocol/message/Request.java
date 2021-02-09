package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteFlagEnum;
import hu.webarticum.miniconnect.protocol.common.ByteString;

public interface Request extends Message {

    public enum Type implements ByteFlagEnum {
        
        // FIXME: CONNECT vs. INIT_SESSION
        // session init id?
        
        // FIXME: use dedicated bytes instead of ad hoc ordinals
        // e. g. ascii letters, such as Q for QUERY (SQL)
        
        CONNECT((byte) 'C') {

            @Override
            ConnectRequest decode(ByteString content) {
                return new ConnectRequest();
            }

        },

        CLOSE((byte) 'F') {

            @Override
            CloseRequest decode(ByteString content) {
                return CloseRequest.decode(content);
            }

        },

        PING((byte) 'P') {

            @Override
            PingRequest decode(ByteString content) {
                return PingRequest.decode(content);
            }

        },

        SQL((byte) 'Q') {

            @Override
            SqlRequest decode(ByteString content) {
                return SqlRequest.decode(content);
            }

        },

        LOB_INIT((byte) 'L') {

            @Override
            Request decode(ByteString content) {
                // TODO
                throw new UnsupportedOperationException();
            }

        },

        LOB_PART((byte) 'P') {

            @Override
            Request decode(ByteString content) {
                // TODO
                throw new UnsupportedOperationException();
            }

        },

        FETCH_HINT((byte) 'F') {

            @Override
            Request decode(ByteString content) {
                // TODO
                throw new UnsupportedOperationException();
            }

        }
        
        ;

        
        private final byte flag;
        
        
        private Type(byte flag) {
            this.flag = flag;
        }
        
        public static Type of(byte flag) {
            return ByteFlagEnum.find(Type.values(), flag);
        }


        @Override
        public byte flag() {
            return flag;
        }

        abstract Request decode(ByteString content);
        
    }
    
    
    public static Request decode(ByteString content) {
        if (content.isEmpty()) {
            throw new IllegalArgumentException("Block content is empty");
        }

        byte flag = content.byteAt(0);
        return Type.of(flag).decode(content);
    }
    
    
    public Type type();
    
}
