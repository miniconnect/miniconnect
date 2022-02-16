package hu.webarticum.miniconnect.record.lob.index;

import java.util.Objects;

import hu.webarticum.miniconnect.util.ToStringBuilder;

public class PositionIndexInterval {

    private final PositionIndexEntry start;
    
    private final PositionIndexEntry end;

    
    private PositionIndexInterval(PositionIndexEntry start, final PositionIndexEntry end) {
        this.start = start;
        this.end = end;
    }

    public static PositionIndexInterval of(PositionIndexEntry start, final PositionIndexEntry end) {
        return new PositionIndexInterval(start, end);
    }


    public PositionIndexEntry start() {
        return start;
    }

    public PositionIndexEntry end() {
        return end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof PositionIndexInterval)) {
            return false;
        }
        
        PositionIndexInterval otherPositionIndexInterval = (PositionIndexInterval) other;
        return
                start.equals(otherPositionIndexInterval.start) &&
                end.equals(otherPositionIndexInterval.end);
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("start", start)
                .add("end", end)
                .build();
    }
    
}
