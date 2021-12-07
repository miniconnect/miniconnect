package hu.webarticum.miniconnect.rdmsframework.table;

import hu.webarticum.miniconnect.rdmsframework.database.TablePatch;

public interface PatchableTableIndex extends TableIndex {

    public void applyPatch(TablePatch patch);
    
}
