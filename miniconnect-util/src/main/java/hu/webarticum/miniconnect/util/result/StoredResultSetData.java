package hu.webarticum.miniconnect.util.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;

public class StoredResultSetData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    

    private final List<MiniColumnHeader> columnHeaders; // NOSONAR serializable
    
    private final List<List<MiniValue>> rows; // NOSONAR serializable
    

    public StoredResultSetData() {
        this(new ArrayList<>(), new ArrayList<>());
    }
    
    public StoredResultSetData(List<MiniColumnHeader> columnHeaders, List<List<MiniValue>> rows) {
        this.columnHeaders = new ArrayList<>(columnHeaders);
        this.rows = new ArrayList<>(rows.size());
        for (List<MiniValue> row : rows) {
            this.rows.add(new ArrayList<>(row));
        }
    }
    

    public List<MiniColumnHeader> columnHeaders() {
        return new ArrayList<>(columnHeaders);
    }

    public Iterator<List<MiniValue>> iterator() {
        return Collections.unmodifiableCollection(rows).iterator();
    }

}
