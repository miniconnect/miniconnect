package hu.webarticum.miniconnect.transfer;

@FunctionalInterface
public interface PacketExchanger {

    public void handle(Packet packet, PacketTarget responseTarget);
    
}
