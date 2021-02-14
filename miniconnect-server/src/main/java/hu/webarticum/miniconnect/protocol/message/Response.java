package hu.webarticum.miniconnect.protocol.message;

import hu.webarticum.miniconnect.protocol.common.ByteFlagEnum;
import hu.webarticum.miniconnect.protocol.common.ByteString;

public interface Response extends Message {

    public enum Type implements ByteFlagEnum {
        
        // TODO: [CONNECTION_]READY ?
        
        STATUS((byte) 'S') {

            @Override
            StatusResponse decode(ByteString content) {
                return StatusResponse.decode(content);
            }

        },

        RESULT((byte) 'R') {

            @Override
            ResultResponse decode(ByteString content) {
                return ResultResponse.decode(content);
            }

        },
        
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

        abstract Response decode(ByteString content);
        
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
