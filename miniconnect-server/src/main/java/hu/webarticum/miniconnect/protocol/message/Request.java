package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public interface Request extends Message {

    public enum Type {
        
        CONNECT {

            @Override
            ConnectRequest decode(ByteString content) {
                return new ConnectRequest();
            }

        },

        CLOSE {

            @Override
            CloseRequest decode(ByteString content) {
                return CloseRequest.decode(content);
            }

        },

        PING {

            @Override
            PingRequest decode(ByteString content) {
                return PingRequest.decode(content);
            }

        },

        // TODO: LOB -->  FIXME: initial + parts

        // TODO: LOB_FREE ?
        
        SQL {

            @Override
            SqlRequest decode(ByteString content) {
                return SqlRequest.decode(content);
            }

        },
        
        // TODO: FETCH_POS (?)
        
        // TODO: FETCH_HINT (?, READ_HINT?)
        
        ;

        
        public static Type of(byte flag) {
            int typeIndex = Byte.toUnsignedInt(flag);
            Type[] types = Type.values();
            if (typeIndex <= types.length) {
                throw new IllegalArgumentException(String.format(
                        "Unknown type index: %d",
                        typeIndex));
            }
            
            return types[typeIndex];
        }

        
        abstract Request decode(ByteString content);
        
        public byte flag() {
            return (byte) ordinal();
        }

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
