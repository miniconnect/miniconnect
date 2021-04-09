package hu.webarticum.miniconnect.server.message.response;

import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public final class ResultSetRowsResponse implements Response {

    private final int queryId;

    private final long rowOffset;

    private final ImmutableMap<Integer, Integer> fixedSizes;

    private final ImmutableList<ImmutableList<CellData>> rows;


    public ResultSetRowsResponse(
            int queryId,
            long rowOffset,
            ImmutableMap<Integer, Integer> fixedSizes,
            ImmutableList<ImmutableList<CellData>> rows) {

        this.queryId = queryId;
        this.rowOffset = rowOffset;
        this.fixedSizes = fixedSizes;
        this.rows = rows;
    }


    public int queryId() {
        return queryId;
    }

    public long rowOffset() {
        return rowOffset;
    }

    public ImmutableMap<Integer, Integer> fixedSizes() {
        return fixedSizes;
    }

    public ImmutableList<ImmutableList<CellData>> rows() {
        return rows;
    }


    public static final class CellData {

        private final long fullLength;

        private final ByteString content;


        public CellData(long fullLength, ByteString content) {
            this.fullLength = fullLength;
            this.content = content;
        }


        public long getFullLength() {
            return fullLength;
        }

        public ByteString getContent() {
            return content;
        }

    }

}
