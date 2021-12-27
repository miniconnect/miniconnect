package hu.webarticum.miniconnect.messenger.message.response;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.messenger.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public final class ResultSetRowsResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int exchangeId;

    private final long rowOffset;

    private final ImmutableList<Integer> nullables;

    private final ImmutableMap<Integer, Integer> fixedSizes;

    private final ImmutableList<ImmutableList<CellData>> rows;


    public ResultSetRowsResponse(
            long sessionId,
            int exchangeId,
            long rowOffset,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes,
            ImmutableList<ImmutableList<CellData>> rows) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.rowOffset = rowOffset;
        this.nullables = nullables;
        this.fixedSizes = fixedSizes;
        this.rows = rows;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    public long rowOffset() {
        return rowOffset;
    }

    public ImmutableList<Integer> nullables() {
        return nullables;
    }

    public ImmutableMap<Integer, Integer> fixedSizes() {
        return fixedSizes;
    }

    public ImmutableList<ImmutableList<CellData>> rows() {
        return rows;
    }


    public static final class CellData {

        private final boolean isNull;

        private final long fullLength;

        private final ByteString content;


        public CellData(boolean isNull, long fullLength, ByteString content) {
            this.isNull = isNull;
            this.fullLength = fullLength;
            this.content = content;
        }
        
        public static CellData of(MiniValue value) {
            return of(value.isNull(), value.contentAccess());
        }

        public static CellData of(boolean isNull, MiniContentAccess contentAccess) {
            return new CellData(isNull, contentAccess.length(), contentAccess.get());
        }


        public boolean isNull() {
            return isNull;
        }

        public long fullLength() {
            return fullLength;
        }

        public ByteString content() {
            return content;
        }
        
    }

}
