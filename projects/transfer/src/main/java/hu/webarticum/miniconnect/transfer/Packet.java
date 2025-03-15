package hu.webarticum.miniconnect.transfer;

import hu.webarticum.miniconnect.lang.ByteString;

public final class Packet {

    private final ByteString header;

    private final ByteString payload;

    
    private Packet(ByteString header, ByteString payload) {
        this.header = header;
        this.payload = payload;
    }
    
    public static Packet of(ByteString header, ByteString payload) {
        return new Packet(header, payload);
    }
    
    
    public ByteString header() {
        return header;
    }

    public ByteString payload() {
        return payload;
    }
    

    @Override
    public int hashCode() {
        return header.hashCode() ^ payload.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof Packet)) {
            return false;
        }
        
        Packet otherPacket = (Packet) other;
        return header.equals(otherPacket.header) && payload.equals(otherPacket.payload);
    }
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("{header");
        resultBuilder.append(header.toArrayString());
        resultBuilder.append(", payload");
        resultBuilder.append(payload.toArrayString());
        resultBuilder.append('}');
        return resultBuilder.toString();
    }
    
}
