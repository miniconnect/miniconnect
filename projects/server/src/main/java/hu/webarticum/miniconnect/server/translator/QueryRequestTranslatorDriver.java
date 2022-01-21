package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

class QueryRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        String query = TranslatorUtil.readString(reader);
        return new QueryRequest(
                headerData.sessionId(),
                headerData.exchangeId(),
                query);
    }

    @Override
    public Packet encode(Message message) {
        QueryRequest queryRequest = (QueryRequest) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.builder()
                .append(TranslatorUtil.encodeString(queryRequest.query()))
                .build();
        return Packet.of(header, payload);
    }

}
