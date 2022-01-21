package hu.webarticum.miniconnect.server.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.HeaderEncoder;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

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
            boolean isNull = TranslatorUtil.readBoolean(reader);
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
        ByteString payload = ByteString.builder()
                
                // TODO
                
                .build();
        return Packet.of(header, payload);
    }

}
