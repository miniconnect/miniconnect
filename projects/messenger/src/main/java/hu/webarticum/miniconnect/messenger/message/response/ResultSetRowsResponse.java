package hu.webarticum.miniconnect.messenger.message.response;

import java.io.Serializable;
import java.util.Objects;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.messenger.message.ExchangeMessage;

public final class ResultSetRowsResponse implements Response, ExchangeMessage {

    private static final long serialVersionUID = -5437671020483220931L;
    

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
        this.nullables = Objects.requireNonNull(nullables);
        this.fixedSizes = Objects.requireNonNull(fixedSizes);
        this.rows = Objects.requireNonNull(rows);
    }


    @Override
    public long sessionId() {
        return sessionId;
    }

    @Override
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

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, exchangeId, rowOffset, nullables, fixedSizes, rows);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof ResultSetRowsResponse)) {
            return false;
        }
        
        ResultSetRowsResponse otherResultSetRowsResponse = (ResultSetRowsResponse) other;
        return
                sessionId == otherResultSetRowsResponse.sessionId &&
                exchangeId == otherResultSetRowsResponse.exchangeId &&
                rowOffset == otherResultSetRowsResponse.rowOffset &&
                nullables.equals(otherResultSetRowsResponse.nullables) &&
                fixedSizes.equals(otherResultSetRowsResponse.fixedSizes) &&
                rows.equals(otherResultSetRowsResponse.rows);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .add("exchangeId", exchangeId)
                .add("rowOffset", rowOffset)
                .add("nullables", nullables)
                .add("fixedSizes", fixedSizes)
                .add("rows", rows)
                .build();
    }


    public static final class CellData implements Serializable {

        private static final long serialVersionUID = 3638994211688920120L;
        

        private final boolean isNull;

        private final long fullLength;

        private final ByteString content;


        public CellData(boolean isNull, long fullLength, ByteString content) {
            this.isNull = isNull;
            this.fullLength = fullLength;
            this.content = Objects.requireNonNull(content);
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

        @Override
        public int hashCode() {
            return Objects.hash(isNull, fullLength, content);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof CellData)) {
                return false;
            }
            
            CellData otherCellData = (CellData) other;
            return
                    isNull == otherCellData.isNull &&
                    fullLength == otherCellData.fullLength &&
                    content.equals(otherCellData.content);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("isNull", isNull)
                    .add("fullLength", fullLength)
                    .add("content", content)
                    .build();
        }

    }

}
