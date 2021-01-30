package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteString;

public interface Response extends Message {

    public enum Type {
        
        STATUS {

            @Override
            StatusResponse decode(ByteString content) {
                return StatusResponse.decode(content);
            }

        },

        RESULT {

            @Override
            ResultResponse decode(ByteString content) {
                return ResultResponse.decode(content);
            }

        },
        
        ;

        
        public static Type of(byte flag) {
            int typeIndex = Byte.toUnsignedInt(flag);
            Type[] types = Type.values();
            if (typeIndex >= types.length) {
                throw new IllegalArgumentException(String.format(
                        "Unknown response type index: %d",
                        typeIndex));
            }
            
            return types[typeIndex];
        }

        
        abstract Response decode(ByteString content);
        
        public byte flag() {
            return (byte) ordinal();
        }

    }

    public static Response decode(ByteString content) {
        if (content.isEmpty()) {
            throw new IllegalArgumentException("Block content is empty");
        }

        byte flag = content.byteAt(0);
        return Type.of(flag).decode(content);
    }
    
    
    public Type type();
    
}
