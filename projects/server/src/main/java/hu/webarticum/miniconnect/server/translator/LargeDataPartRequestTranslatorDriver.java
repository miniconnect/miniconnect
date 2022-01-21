package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

class LargeDataPartRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        long offset = reader.readLong();
        ByteString content = ByteString.wrap(TranslatorUtil.readSized(reader));
        return new LargeDataPartRequest(
                headerData.sessionId(),
                headerData.exchangeId(),
                offset,
                content);
    }

    @Override
    public Packet encode(Message message) {
        LargeDataPartRequest largeDataPartRequest = (LargeDataPartRequest) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.builder()
                .appendLong(largeDataPartRequest.offset())
                .append(TranslatorUtil.encodeByteString(largeDataPartRequest.content()))
                .build();
        return Packet.of(header, payload);
    }

}
