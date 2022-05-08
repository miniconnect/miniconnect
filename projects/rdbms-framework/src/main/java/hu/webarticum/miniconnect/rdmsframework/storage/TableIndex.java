package hu.webarticum.miniconnect.rdmsframework.storage;

import hu.webarticum.miniconnect.lang.ImmutableList;

public interface TableIndex extends NamedResource {
    
    public enum InclusionMode { INCLUDE, EXCLUDE }
    
    public enum NullsMode { WITH_NULLS, NO_NULLS, NULLS_ONLY }
    
    public enum SortMode {
        
        UNSORTED(false, true, true),
        ASC_NULLS_FIRST(true, true, true),
        ASC_NULLS_LAST(true, true, false),
        DESC_NULLS_LAST(true, false, false),
        DESC_NULLS_FIRST(true, false, true),
        
        ;
        
        
        private final boolean sorted;

        private final boolean asc;
        
        private final boolean nullsFirst;
        
        
        private SortMode(boolean sorted, boolean asc, boolean nullsFirst) {
            this.sorted = sorted;
            this.asc = asc;
            this.nullsFirst = nullsFirst;
        }

        
        public boolean isSorted() {
            return sorted;
        }

        public boolean isAsc() {
            return asc;
        }

        public boolean isNullsFirst() {
            return nullsFirst;
        }
        
    }
    

    public boolean isUnique();

    public ImmutableList<String> columnNames();

    public default int width() {
        return columnNames().size();
    }

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
                values.map(v -> v == null ? NullsMode.NULLS_ONLY : NullsMode.NO_NULLS),
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
                from == null ? null : ImmutableList.of(from),
                fromInclusionMode,
                to == null ? null : ImmutableList.of(to),
                toInclusionMode,
                ImmutableList.of(nullsMode),
                ImmutableList.of(sortMode));
    }

    public default TableSelection find(Object value) {
        return findMulti(ImmutableList.of(value));
    }
    
}
