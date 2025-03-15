package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.SessionCloseResponse;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;

class SessionCloseResponseTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        return new SessionCloseResponse(
                headerData.sessionId(),
                headerData.exchangeId());
    }

    @Override
    public Packet encode(Message message) {
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.empty();
        return Packet.of(header, payload);
    }

}
