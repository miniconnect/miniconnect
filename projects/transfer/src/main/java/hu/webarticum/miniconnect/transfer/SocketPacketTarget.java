package hu.webarticum.miniconnect.transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

public class SocketPacketTarget implements PacketTarget {
    
    private final Socket socket;
    
    private final PacketWriter packetWriter = new PacketWriter();
    
    
    public SocketPacketTarget(Socket socket) {
        this.socket = socket;
    }
    

    @Override
    public void receive(Packet packet) {
        OutputStream out;
        try {
            out = socket.getOutputStream();
            out.write(TransferConstants.PACKET_BYTE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        packetWriter.write(packet, out);
    }

}
