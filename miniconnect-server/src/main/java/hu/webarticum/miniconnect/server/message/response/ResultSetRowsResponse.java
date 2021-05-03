package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.server.message.SessionMessage;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public final class ResultSetRowsResponse implements Response, SessionMessage {

    private final long sessionId;

    private final int queryId;

    private final long rowOffset;

    private final ImmutableList<Integer> nullables;

    private final ImmutableMap<Integer, Integer> fixedSizes;

    private final ImmutableList<ImmutableList<CellData>> rows;


    public ResultSetRowsResponse(
            long sessionId,
            int queryId,
            long rowOffset,
            ImmutableList<Integer> nullables,
            ImmutableMap<Integer, Integer> fixedSizes,
            ImmutableList<ImmutableList<CellData>> rows) {

        this.sessionId = sessionId;
        this.queryId = queryId;
        this.rowOffset = rowOffset;
        this.nullables = nullables;
        this.fixedSizes = fixedSizes;
        this.rows = rows;
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
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
