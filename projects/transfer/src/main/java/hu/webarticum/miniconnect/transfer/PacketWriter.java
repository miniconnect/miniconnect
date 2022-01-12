package hu.webarticum.miniconnect.transfer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.util.data.ByteString;

public class PacketWriter {

    public void write(Packet packet, OutputStream out) {
        try {
            writeInternal(packet, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public void writeInternal(Packet packet, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.write(TransferConstants.MAGIC_BYTE);
        writeSizedPart(packet.header(), dataOut);
        writeSizedPart(packet.payload(), dataOut);
    }
    
    private void writeSizedPart(ByteString content, DataOutputStream dataOut) throws IOException {
        dataOut.writeInt(content.length());
        content.writeTo(dataOut);
    }
    
}
