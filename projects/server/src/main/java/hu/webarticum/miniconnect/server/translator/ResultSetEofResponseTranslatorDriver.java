package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

class ResultSetEofResponseTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        long endOffset = reader.readLong();
        return new ResultSetEofResponse(
                headerData.sessionId(),
                headerData.exchangeId(),
                endOffset);
    }

    @Override
    public Packet encode(Message message) {
        ResultSetEofResponse resultSetEofResponse = (ResultSetEofResponse) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.builder()
                .appendLong(resultSetEofResponse.endOffset())
                .build();
        return Packet.of(header, payload);
    }

}
