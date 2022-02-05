package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;

class LargeDataHeadRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        String variableName = TranslatorUtil.readString(reader);
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
        ByteString payload = ByteString.builder()
                .append(TranslatorUtil.encodeString(largeDataHeadRequest.variableName()))
                .appendLong(largeDataHeadRequest.length())
                .build();
        return Packet.of(header, payload);
    }

}
