package hu.webarticum.miniconnect.rdmsframework.execution.impl.select;

import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;

class SelectItemEntry {

    final String tableAlias;

    final String fieldName;

    final String fieldAlias;

    final ValueTranslator valueTranslator;

    final ColumnDefinition columnDefinition;
    
    
    SelectItemEntry(
            String tableAlias,
            String fieldName,
            String fieldAlias,
            ValueTranslator valueTranslator,
            ColumnDefinition columnDefinition) {
        this.tableAlias = tableAlias;
        this.fieldName = fieldName;
        this.fieldAlias = fieldAlias;
        this.valueTranslator = valueTranslator;
        this.columnDefinition = columnDefinition;
    }

}