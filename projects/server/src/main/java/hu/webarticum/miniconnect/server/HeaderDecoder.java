package hu.webarticum.miniconnect.server;

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
        ByteString.Reader reader = headerBytes.reader();
        char symbol = (char) reader.read();
        MessageType messageType = MessageType.ofSymbol(symbol);
        long sessionId = reader.readLong();
        int exchangeId = reader.readInt();
        return HeaderData.of(messageType, sessionId, exchangeId);
    }
    
}
