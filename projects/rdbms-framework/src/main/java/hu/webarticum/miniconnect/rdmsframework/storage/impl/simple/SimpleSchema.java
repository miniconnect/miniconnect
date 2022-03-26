package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.rdmsframework.storage.Schema;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;

public class SimpleSchema implements Schema {

    private final String name;
    
    private final SimpleResourceManager<Table> tableManager = new SimpleResourceManager<>();
    
    
    public SimpleSchema(String name) {
        this.name = name;
    }
    

    @Override
    public String name() {
        return name;
    }

    @Override
    public SimpleResourceManager<Table> tables() {
        return tableManager;
    }

}
