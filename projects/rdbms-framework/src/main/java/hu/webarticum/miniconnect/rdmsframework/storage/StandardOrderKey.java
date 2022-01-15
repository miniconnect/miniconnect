package hu.webarticum.miniconnect.rdmsframework.storage;

import java.util.Objects;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public final class StandardOrderKey implements OrderKey {
    
    private final String tableName;
    
    private final ImmutableList<Entry> columnEntries;
    
    private final boolean groupAscOrder;
    

    public StandardOrderKey(String tableName, boolean rowAscOrder) {
        this(tableName, ImmutableList.empty(), rowAscOrder);
    }

    public StandardOrderKey(
            String tableName, ImmutableList<Entry> columnEntries, boolean groupAscOrder) {
        this.tableName = tableName;
        this.columnEntries = columnEntries;
        this.groupAscOrder = groupAscOrder;
    }
    
    
    public String tableName() {
        return tableName;
    }

    public ImmutableList<Entry> columnEntries() {
        return columnEntries;
    }

    public boolean isGroupAscOrder() {
        return groupAscOrder;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tableName, columnEntries, groupAscOrder);
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof StandardOrderKey)) {
            return false;
        }
        
        StandardOrderKey otherKey = (StandardOrderKey) other;
        return
                tableName.equals(otherKey.tableName) &&
                columnEntries.equals(otherKey.columnEntries) &&
                groupAscOrder == otherKey.groupAscOrder;
    }
    
    
    public static final class Entry {
        
        private final String columnName;
        
        private final boolean ascOrder;
        
        private final boolean nullHighest;
        
        
        public Entry(String columnName, boolean ascOrder, boolean nullHighest) {
            this.columnName = columnName;
            this.ascOrder = ascOrder;
            this.nullHighest = nullHighest;
        }

        
        public String columnName() {
            return columnName;
        }

        public boolean isAscOrder() {
            return ascOrder;
        }

        public boolean isNullHighest() {
            return nullHighest;
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName, ascOrder, nullHighest);
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof Entry)) {
                return false;
            }
            
            Entry otherEntry = (Entry) other;
            return
                    columnName.equals(otherEntry.columnName) &&
                    ascOrder == otherEntry.ascOrder &&
                    nullHighest == otherEntry.nullHighest;
        }
    }
    
}
