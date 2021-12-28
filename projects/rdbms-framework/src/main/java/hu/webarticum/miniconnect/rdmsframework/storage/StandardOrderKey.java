package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public final class StandardOrderKey implements OrderKey {
    
    private final String tableName;
    
    private final ImmutableList<String> columnNames;
    
    private final boolean ascOrder;
    
    private final boolean nullsFirst;
    

    public StandardOrderKey(String tableName, boolean ascOrder) {
        this(tableName, ImmutableList.empty(), ascOrder, true);
    }
    
    public StandardOrderKey(
            String tableName,
            ImmutableList<String> columnNames,
            boolean ascOrder,
            boolean nullsFirst) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.ascOrder = ascOrder;
        this.nullsFirst = nullsFirst;
    }
    
    
    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = result | columnNames.hashCode();
        result = (result * 37) + (ascOrder ? 1 : 0);
        result = (result * 37) + (nullsFirst ? 1 : 0);
        return result;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof StandardOrderKey)) {
            return false;
        }
        
        StandardOrderKey otherKey = (StandardOrderKey) other;
        return
                tableName.equals(otherKey.tableName) &&
                columnNames.equals(otherKey.columnNames) &&
                ascOrder == otherKey.ascOrder &&
                nullsFirst == otherKey.nullsFirst;
    }
    
}
