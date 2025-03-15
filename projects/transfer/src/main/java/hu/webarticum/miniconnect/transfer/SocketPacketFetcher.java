package hu.webarticum.miniconnect.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

public class SocketPacketFetcher {
    
    private final PacketReader packetReader = new PacketReader();
    
    private final Socket socket;
    
    
    public SocketPacketFetcher(Socket socket) {
        this.socket = socket;
    }

    public Packet fetch() {
        try {
            return fetchThrowing();
        } catch (IOException e) {
            if (socket.isClosed()) {
                return null;
            }
            throw new UncheckedIOException(e);
        }
    }
    
    private Packet fetchThrowing() throws IOException {
        InputStream in = socket.getInputStream();
        int optionalControlByte = in.read();
        if (optionalControlByte == -1) {
            throw new IOException("Unexpected end of input stream");
        }
        
        byte controlByte = (byte) optionalControlByte;
        if (controlByte == TransferConstants.PACKET_BYTE) {
            return packetReader.read(in);
        } else if (controlByte == TransferConstants.CLOSE_BYTE) {
            return null;
        } else {
            throw new IOException(String.format("Invalid control byte: 0x%02X", controlByte));
        }
    }

}
