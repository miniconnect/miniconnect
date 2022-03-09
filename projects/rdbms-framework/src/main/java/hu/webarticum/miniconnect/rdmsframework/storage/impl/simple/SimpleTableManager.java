package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;

public class SimpleTableManager implements NamedResourceStore<Table> {
    
    private Map<String, Table> tables = Collections.synchronizedMap(new LinkedHashMap<>());
    

    @Override
    public ImmutableList<String> names() {
        return ImmutableList.fromCollection(tables.keySet());
    }

    @Override
    public boolean contains(String name) {
        return tables.containsKey(name);
    }
    
    @Override
    public Table get(String name) {
        return tables.get(name);
    }
    
    public void registerTable(Table table) {
        String name = table.name();
        tables.compute(name, (n, t) -> checkRegisterTable(n, t, table));
    }
    
    private Table checkRegisterTable(String name, Table existingTable, Table newTable) {
        if (existingTable != null) {
            throw new IllegalArgumentException("Table already exists: " + name);
        }
        return newTable;
    }

    public void removeTable(String name) {
        if (tables.remove(name) == null) {
            throw new NoSuchElementException("No table named " + name);
        }
    }

}
