package hu.webarticum.miniconnect.rdmsframework.execution.impl.select;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import hu.webarticum.miniconnect.rdmsframework.query.SelectQuery.JoinItem;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

class TableEntry {
    
    final String schemaName;
    
    final Table table;
    
    final JoinItem joinItem;

    boolean preorderable;
    
    final Map<String, ValueTranslator> valueTranslators = new HashMap<>();
    
    final Map<String, Object> subFilter = new LinkedHashMap<>();
    
    
    TableEntry(String schemaName, Table table, JoinItem joinItem, boolean preorderable) {
        this.schemaName = schemaName;
        this.table = table;
        this.joinItem = joinItem;
        this.preorderable = preorderable;
    }
    
}