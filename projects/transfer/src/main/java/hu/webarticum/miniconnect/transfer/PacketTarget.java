package hu.webarticum.miniconnect.transfer;

@FunctionalInterface
public interface PacketTarget {

    public void receive(Packet packet);
    
}
