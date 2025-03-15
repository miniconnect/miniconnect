package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;

class LargeDataSaveResponseTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        boolean success = TranslatorUtil.readBoolean(reader);
        int errorCode = reader.readInt();
        String sqlState = TranslatorUtil.readString(reader);
        String errorMessage = TranslatorUtil.readString(reader);
        return new LargeDataSaveResponse(
                headerData.sessionId(),
                headerData.exchangeId(),
                success,
                errorCode,
                sqlState,
                errorMessage);
    }

    @Override
    public Packet encode(Message message) {
        LargeDataSaveResponse largeDataSaveResponse = (LargeDataSaveResponse) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString payload = ByteString.builder()
                .append(TranslatorUtil.encodeBoolean(largeDataSaveResponse.success()))
                .appendInt(largeDataSaveResponse.errorCode())
                .append(TranslatorUtil.encodeString(largeDataSaveResponse.sqlState()))
                .append(TranslatorUtil.encodeString(largeDataSaveResponse.errorMessage()))
                .build();
        return Packet.of(header, payload);
    }

}
