package hu.webarticum.miniconnect.rdmsframework.execution.impl.select;

import hu.webarticum.miniconnect.rdmsframework.query.NullsOrderMode;

public class OrderByEntry {

    public final String tableAlias;

    public final String fieldName;

    public final boolean ascOrder;
    
    public final NullsOrderMode nullsOrderMode;
    
    
    OrderByEntry(String tableAlias, String fieldName, boolean ascOrder, NullsOrderMode nullsOrderMode) {
        this.tableAlias = tableAlias;
        this.fieldName = fieldName;
        this.ascOrder = ascOrder;
        this.nullsOrderMode = nullsOrderMode;
    }

}