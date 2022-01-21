package hu.webarticum.miniconnect.server.translator;

import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

public class LargeDataHeadRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        int variableNameBytesLength = reader.readInt();
        byte[] variableNameBytes = reader.read(variableNameBytesLength);
        String variableName = new String(variableNameBytes, StandardCharsets.UTF_8);
        long length = reader.readLong();
        return new LargeDataHeadRequest(
                headerData.sessionId(),
                headerData.exchangeId(),
                variableName,
                length);
    }

    @Override
    public Packet encode(Message message) {
        LargeDataHeadRequest largeDataHeadRequest = (LargeDataHeadRequest) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        byte[] variableNameBytes = largeDataHeadRequest.variableName()
                .getBytes(StandardCharsets.UTF_8);
        ByteString payload = ByteString.builder()
                .appendInt(variableNameBytes.length)
                .append(variableNameBytes)
                .appendLong(largeDataHeadRequest.length())
                .build();
        return Packet.of(header, payload);
    }

}
