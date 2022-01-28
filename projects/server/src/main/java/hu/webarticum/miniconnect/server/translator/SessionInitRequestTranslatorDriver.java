package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.SessionInitRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

class SessionInitRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        return new SessionInitRequest();
    }

    @Override
    public Packet encode(Message message) {
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.empty();
        return Packet.of(header, payload);
    }

}
