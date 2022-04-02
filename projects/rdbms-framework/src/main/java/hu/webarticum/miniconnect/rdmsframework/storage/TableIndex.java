package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface TableIndex extends NamedResource {
    
    public enum InclusionMode { INCLUDE, EXCLUDE }
    
    public enum NullsMode { WITH_NULLS, NO_NULLS }
    
    public enum SortMode {
        
        UNSORTED, ASC_NULLS_FIRST, ASC_NULLS_LAST, DESC_NULLS_LAST, DESC_NULLS_FIRST
        
    }
    
    
    public ImmutableList<String> columnNames();

    public boolean isUnique();

    public TableSelection findMulti(
            ImmutableList<?> from,
            InclusionMode fromInclusionMode,
            ImmutableList<?> to,
            InclusionMode toInclusionMode,
            ImmutableList<NullsMode> nullsModes,
            ImmutableList<SortMode> sortModes);

    public default TableSelection findMulti(ImmutableList<?> values) {
        return findMulti(
                values,
                InclusionMode.INCLUDE,
                values,
                InclusionMode.INCLUDE,
                values.map(v -> NullsMode.WITH_NULLS),
                values.map(v -> SortMode.UNSORTED));
    }

    public default TableSelection find(
            Object from,
            InclusionMode fromInclusionMode,
            Object to,
            InclusionMode toInclusionMode,
            NullsMode nullsMode,
            SortMode sortMode) {
        return findMulti(
                ImmutableList.of(from),
                fromInclusionMode,
                ImmutableList.of(to),
                toInclusionMode,
                ImmutableList.of(nullsMode),
                ImmutableList.of(sortMode));
    }

    public default TableSelection find(Object value) {
        return findMulti(ImmutableList.of(value));
    }
    
}
