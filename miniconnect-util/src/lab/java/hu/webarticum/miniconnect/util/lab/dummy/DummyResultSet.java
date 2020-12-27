package hu.webarticum.miniconnect.util.lab.dummy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;

public class DummyResultSet implements MiniResultSet {
    
    private final List<String> columnNames;
    
    private final List<List<MiniValue>> rows;
    

    private volatile boolean closed = false;
    

    public DummyResultSet() {
        this(new ArrayList<>(), new ArrayList<>());
    }
    
    public DummyResultSet(List<String> columnNames, List<List<MiniValue>> rows) {
        this.columnNames = new ArrayList<>(columnNames);
        this.rows = new ArrayList<>(rows.size());
        for (List<MiniValue> row : rows) {
            this.rows.add(new ArrayList<>(row));
        }
    }
    

    @Override
    public List<String> columnNames() {
        return new ArrayList<>(columnNames);
    }

    @Override
    public Iterator<List<MiniValue>> iterator() {
        return Collections.unmodifiableCollection(rows).iterator();
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

}
