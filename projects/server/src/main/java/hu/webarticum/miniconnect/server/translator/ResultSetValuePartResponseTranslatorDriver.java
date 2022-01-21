package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

class ResultSetValuePartResponseTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        long rowIndex = reader.readLong();
        int columnIndex = reader.readInt();
        long offset = reader.readLong();
        ByteString content = ByteString.wrap(TranslatorUtil.readSized(reader));
        return new ResultSetValuePartResponse(
                headerData.sessionId(),
                headerData.exchangeId(),
                rowIndex,
                columnIndex,
                offset,
                content);
    }

    @Override
    public Packet encode(Message message) {
        ResultSetValuePartResponse resultSetValuePartResponse =
                (ResultSetValuePartResponse) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.builder()
                .appendLong(resultSetValuePartResponse.rowIndex())
                .appendInt(resultSetValuePartResponse.columnIndex())
                .appendLong(resultSetValuePartResponse.offset())
                .append(TranslatorUtil.encodeByteString(resultSetValuePartResponse.content()))
                .build();
        return Packet.of(header, payload);
    }

}
