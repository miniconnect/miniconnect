package hu.webarticum.miniconnect.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

/**
 * Packet header decoder.
 * 
 * <p>Deserializes a {@link Packet} from its standard byte representation.</p>
 * 
 * <p>It is separated from {@link MessageDecoder} because it is more shared.
 * More application can be interested in decoding and reading headers
 * than other parts of the message content.</p>
 * 
 * @see HeaderEncoder
 * @see HeaderData
 */
public class HeaderDecoder {

    public HeaderData decode(ByteString headerBytes) {
        try (DataInputStream in = new DataInputStream(headerBytes.inputStream())) {
            char symbol = (char) (byte) in.read();
            MessageType messageType = MessageType.ofSymbol(symbol);
            long sessionId = in.readLong();
            int exchangeId = in.readInt();
            return HeaderData.of(messageType, sessionId, exchangeId);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
