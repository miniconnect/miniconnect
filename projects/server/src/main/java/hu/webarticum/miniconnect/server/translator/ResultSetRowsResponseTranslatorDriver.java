package hu.webarticum.miniconnect.server.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;

class ResultSetRowsResponseTranslatorDriver implements TranslatorDriver {

    /*
    rowOffset
    nullablesSize
    nullableItem1
    ...
    nullableItemN
    fixedSizesSize
    fixedSizeKey1
    fixedSizeValue1
    ... ...
    fixedSizeKeyN
    fixedSizeValueN
    rowsSize
    columnsSize
    <cellDatas>
    
    CellData:
    [isNull] (if nullable)
    [partial] (if not isNull and not fixedSize)
    [fullLength] (if not isNull and not fixedSize and not partial)
    [contentLength] (if not isNull and not fixedSize)
    content
    */
    
    @Override
    public Message decode(HeaderData headerData, ByteString payload) {
        ByteString.Reader reader = payload.reader();
        long rowOffset = reader.readLong();
        ImmutableList<Integer> nullables = readNullables(reader);
        ImmutableMap<Integer, Integer> fixedSizes = readFixedSizes(reader);
        ImmutableList<ImmutableList<CellData>> rows = readRows(reader, nullables, fixedSizes);
        return new ResultSetRowsResponse(
                headerData.sessionId(),
                headerData.exchangeId(),
                rowOffset,
                nullables,
                fixedSizes,
                rows);
    }
    
    private ImmutableList<Integer> readNullables(ByteString.Reader reader) {
        int nullablesSize = reader.readInt();
        List<Integer> nullablesBuilder = new ArrayList<>(nullablesSize);
        for (int i = 0; i < nullablesSize; i++) {
            nullablesBuilder.add(reader.readInt());
        }
        return new ImmutableList<>(nullablesBuilder);
    }

    private ImmutableMap<Integer, Integer> readFixedSizes(ByteString.Reader reader) {
        int fixedSizesSize = reader.readInt();
        Map<Integer, Integer> fixedSizesBuilder = new HashMap<>(fixedSizesSize);
        for (int i = 0; i < fixedSizesSize; i++) {
            int key = reader.readInt();
            int value = reader.readInt();
            fixedSizesBuilder.put(key, value);
        }
        return new ImmutableMap<>(fixedSizesBuilder);
    }
    
    private ImmutableList<ImmutableList<CellData>> readRows(
            ByteString.Reader reader,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes) {
        int rowsSize = reader.readInt();
        int columnsSize = reader.readInt();
        List<ImmutableList<CellData>> rowsBuilder = new ArrayList<>(rowsSize);
        for (int i = 0; i < rowsSize; i++) {
            rowsBuilder.add(readRow(reader, nullables, fixedSizes, columnsSize));
        }
        return new ImmutableList<>(rowsBuilder);
    }

    private ImmutableList<CellData> readRow(
            ByteString.Reader reader,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes,
            int columnsSize) {
        List<CellData> rowBuilder = new ArrayList<>(columnsSize);
        for (int i = 0; i < columnsSize; i++) {
            boolean nullable = nullables.contains(i);
            int fixedSize = fixedSizes.getOrDefault(i, -1);
            rowBuilder.add(readCell(reader, nullable, fixedSize));
        }
        return new ImmutableList<>(rowBuilder);
    }

    private CellData readCell(ByteString.Reader reader, boolean nullable, int fixedSize) {
        if (nullable) {
            boolean isNull = !TranslatorUtil.readBoolean(reader);
            if (isNull) {
                return new CellData(true, 0, ByteString.empty());
            }
        }
        long fullLength;
        int contentLength;
        if (fixedSize != -1) {
            fullLength = fixedSize;
            contentLength = fixedSize;
        } else {
            boolean partial = TranslatorUtil.readBoolean(reader);
            if (partial) {
                fullLength = reader.readLong();
                contentLength = reader.readInt();
            } else {
                contentLength = reader.readInt();
                fullLength = contentLength;
            }
        }
        byte[] content = reader.read(contentLength);
        return new CellData(false, fullLength, ByteString.wrap(content));
    }
    
    @Override
    public Packet encode(Message message) {
        ResultSetRowsResponse resultSetRowsResponse = (ResultSetRowsResponse) message;
        HeaderData headerData = HeaderData.ofMessage(message);
        ByteString header = new HeaderEncoder().encode(headerData);
        ImmutableList<Integer> nullables = resultSetRowsResponse.nullables();
        ImmutableMap<Integer, Integer> fixedSizes = resultSetRowsResponse.fixedSizes();
        ByteString.Builder payloadBuilder = ByteString.builder();
        payloadBuilder.appendLong(resultSetRowsResponse.rowOffset());
        appendNullables(payloadBuilder, nullables);
        appendFixedSizes(payloadBuilder, fixedSizes);
        appendRows(payloadBuilder, resultSetRowsResponse.rows(), nullables, fixedSizes);
        return Packet.of(header, payloadBuilder.build());
    }

    private void appendNullables(
            ByteString.Builder payloadBuilder,
            ImmutableList<Integer> nullables) {
        payloadBuilder.appendInt(nullables.size());
        for (int nullable : nullables) {
            payloadBuilder.appendInt(nullable);
        }
    }

    private void appendFixedSizes(
            ByteString.Builder payloadBuilder,
            ImmutableMap<Integer, Integer> fixedSizes) {
        payloadBuilder.appendInt(fixedSizes.size());
        for (Map.Entry<Integer, Integer> entry : fixedSizes.entrySet()) {
            payloadBuilder.appendInt(entry.getKey());
            payloadBuilder.appendInt(entry.getValue());
        }
    }

    private void appendRows(
            ByteString.Builder payloadBuilder,
            ImmutableList<ImmutableList<CellData>> rows,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes) {
        int rowsSize = rows.size();
        payloadBuilder.appendInt(rowsSize);
        int columnsSize = rowsSize > 0 ? rows.get(0).size() : 0;
        payloadBuilder.appendInt(columnsSize);
        for (ImmutableList<CellData> row : rows) {
            appendRow(payloadBuilder, row, columnsSize, nullables, fixedSizes);
        }
    }

    private void appendRow(
            ByteString.Builder payloadBuilder,
            ImmutableList<CellData> row,
            int columnsSize,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes) {
        for (int i = 0; i < columnsSize; i++) {
            boolean nullable = nullables.contains(i);
            int fixedSize = fixedSizes.getOrDefault(i, -1);
            appendCell(payloadBuilder, row.get(i), nullable, fixedSize);
        }
    }

    private void appendCell(
            ByteString.Builder payloadBuilder,
            CellData cellData,
            boolean nullable,
            int fixedSize) {
        if (nullable) {
            if (cellData.isNull()) {
                payloadBuilder.append(TranslatorUtil.encodeBoolean(false));
                return;
            } else {
                payloadBuilder.append(TranslatorUtil.encodeBoolean(true));
            }
        }
        ByteString content = cellData.content();
        if (fixedSize == -1) {
            long fullLength = cellData.fullLength();
            int contentLength = content.length();
            boolean partial = contentLength < fullLength;
            payloadBuilder.append(TranslatorUtil.encodeBoolean(partial));
            if (partial) {
                payloadBuilder.appendLong(fullLength);
            }
            payloadBuilder.appendInt(contentLength);
        }
        payloadBuilder.append(content);
    }

}
