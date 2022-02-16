package hu.webarticum.miniconnect.record.lob.index;

import hu.webarticum.miniconnect.util.ToStringBuilder;

public class PositionIndexEntry {
    
    private final long highLevelPosition;
    
    private final long lowLevelPosition;

    
    private PositionIndexEntry(long highLevelPosition, long lowLevelPosition) {
        this.highLevelPosition = highLevelPosition;
        this.lowLevelPosition = lowLevelPosition;
    }

    public PositionIndexEntry of(long highLevelPosition, long lowLevelPosition) {
        return new PositionIndexEntry(highLevelPosition, lowLevelPosition);
    }


    public long highLevelPosition() {
        return highLevelPosition;
    }

    public long lowLevelPosition() {
        return lowLevelPosition;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(highLevelPosition) ^ Long.hashCode(lowLevelPosition);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof PositionIndexEntry)) {
            return false;
        }

        PositionIndexEntry otherPositionIndexEntry = (PositionIndexEntry) other;
        return
                highLevelPosition == otherPositionIndexEntry.highLevelPosition &&
                lowLevelPosition == otherPositionIndexEntry.lowLevelPosition;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("highLevelPosition", highLevelPosition)
                .add("lowLevelPosition", lowLevelPosition)
                .build();
    }
    
}
