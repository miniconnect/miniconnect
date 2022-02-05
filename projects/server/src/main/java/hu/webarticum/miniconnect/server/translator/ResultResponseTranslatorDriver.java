package hu.webarticum.miniconnect.server.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ColumnHeaderData;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse.ErrorData;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

class ResultResponseTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData header, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        boolean success = TranslatorUtil.readBoolean(reader);
        ErrorData error = readErrorData(reader);
        ImmutableList<ErrorData> warnings = readWarnings(reader);
        boolean hasResultSet = TranslatorUtil.readBoolean(reader);
        ImmutableList<ColumnHeaderData> columnHeaders = readColumnHeaders(reader);
        return new ResultResponse(
                header.sessionId(),
                header.exchangeId(),
                success,
                error,
                warnings,
                hasResultSet,
                columnHeaders);
    }

    private ImmutableList<ErrorData> readWarnings(ByteString.Reader reader) {
        int errorsSize = reader.readInt();
        List<ErrorData> warningsBuilder = new ArrayList<>(errorsSize);
        for (int i = 0; i < errorsSize; i++) {
            warningsBuilder.add(readErrorData(reader));
        }
        return new ImmutableList<>(warningsBuilder);
    }

    private ErrorData readErrorData(ByteString.Reader reader) {
        int code = reader.readInt();
        String sqlState = TranslatorUtil.readString(reader);
        String message = TranslatorUtil.readString(reader);
        return new ErrorData(code, sqlState, message);
    }

    private ImmutableList<ColumnHeaderData> readColumnHeaders(ByteString.Reader reader) {
        int columnsSize = reader.readInt();
        List<ColumnHeaderData> columnHeadersBuilder = new ArrayList<>(columnsSize);
        for (int i = 0; i < columnsSize; i++) {
            columnHeadersBuilder.add(readColumnHeaderData(reader));
        }
        return new ImmutableList<>(columnHeadersBuilder);
    }

    private ColumnHeaderData readColumnHeaderData(ByteString.Reader reader) {
        String name = TranslatorUtil.readString(reader);
        boolean isNullable = TranslatorUtil.readBoolean(reader);
        String type = TranslatorUtil.readString(reader);
        ImmutableMap<String, ByteString> properties = readProperties(reader);
        return new ColumnHeaderData(name, isNullable, type, properties);
    }

    private ImmutableMap<String, ByteString> readProperties(ByteString.Reader reader) {
        int propertiesSize = reader.readInt();
        Map<String, ByteString> propertiesBuilder = new HashMap<>(propertiesSize);
        for (int i = 0; i < propertiesSize; i++) {
            String key = TranslatorUtil.readString(reader);
            ByteString value = ByteString.wrap(TranslatorUtil.readSized(reader));
            propertiesBuilder.put(key, value);
        }
        return new ImmutableMap<>(propertiesBuilder);
    }

    @Override
    public Packet encode(Message message) {
        ResultResponse resultResponse = (ResultResponse) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ByteString.Builder payloadBuilder = ByteString.builder();
        payloadBuilder.append(TranslatorUtil.encodeBoolean(resultResponse.success()));
        appendErrorData(payloadBuilder, resultResponse.error());
        appendWarnings(payloadBuilder, resultResponse.warnings());
        payloadBuilder.append(TranslatorUtil.encodeBoolean(resultResponse.hasResultSet()));
        appendColumnHeaders(payloadBuilder, resultResponse.columnHeaders());
        return Packet.of(header, payloadBuilder.build());
    }

    private void appendWarnings(
            ByteString.Builder payloadBuilder,
            ImmutableList<ErrorData> warnings) {
        payloadBuilder.appendInt(warnings.size());
        for (ErrorData error : warnings) {
            appendErrorData(payloadBuilder, error);
        }
    }

    private void appendErrorData(ByteString.Builder payloadBuilder, ErrorData error) {
        payloadBuilder.appendInt(error.code());
        payloadBuilder.append(TranslatorUtil.encodeString(error.sqlState()));
        payloadBuilder.append(TranslatorUtil.encodeString(error.message()));
    }

    private void appendColumnHeaders(
            ByteString.Builder payloadBuilder,
            ImmutableList<ColumnHeaderData> columnHeaders) {
        payloadBuilder.appendInt(columnHeaders.size());
        for (ColumnHeaderData header : columnHeaders) {
            appendColumnHeaderData(payloadBuilder, header);
        }
    }

    private void appendColumnHeaderData(
            ByteString.Builder payloadBuilder,
            ColumnHeaderData header) {
        payloadBuilder.append(TranslatorUtil.encodeString(header.name()));
        payloadBuilder.append(TranslatorUtil.encodeBoolean(header.isNullable()));
        payloadBuilder.append(TranslatorUtil.encodeString(header.type()));
        appendProperties(payloadBuilder, header.properties());
    }

    private void appendProperties(
            ByteString.Builder payloadBuilder,
            ImmutableMap<String, ByteString> properties) {
        payloadBuilder.appendInt(properties.size());
        for (Map.Entry<String, ByteString> entry : properties.entrySet()) {
            payloadBuilder.append(TranslatorUtil.encodeString(entry.getKey()));
            payloadBuilder.append(TranslatorUtil.encodeByteString(entry.getValue()));
        }
    }

}
