package hu.webarticum.miniconnect.transfer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.util.data.ByteString;

public class PacketReader {
    
    public Packet read(InputStream in) {
        try {
            return readInternal(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Packet readInternal(InputStream in) throws IOException {
        int optionalFirstByte = in.read();
        if (optionalFirstByte == -1) {
            throw new IOException("Unexpected EOF instead of first byte");
        }
        
        byte firstByte = (byte) optionalFirstByte;
        if (firstByte != TransferConstants.MAGIC_BYTE) {
            throw new IOException(String.format("Invalid first byte: 0x%02X", firstByte));
        }

        DataInputStream dataIn = new DataInputStream(in);
        ByteString header = readSizedPart(dataIn);
        ByteString payload = readSizedPart(dataIn);
        
        return Packet.of(header, payload);
    }
    
    private ByteString readSizedPart(DataInputStream dataIn) throws IOException {
        int size = dataIn.readInt();
        byte[] bytes = new byte[size];
        int remaining = size;
        while (remaining > 0) {
            int offset = size - remaining;
            int readLength = dataIn.read(bytes, offset, remaining);
            remaining -= readLength;
        }
        return ByteString.wrap(bytes);
    }
    
}
