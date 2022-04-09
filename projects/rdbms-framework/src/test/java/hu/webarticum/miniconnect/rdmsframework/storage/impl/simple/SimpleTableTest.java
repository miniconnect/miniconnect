package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.AbstractWritableTableTest;

class SimpleTableTest extends AbstractWritableTableTest {

    @Override
    protected SimpleTable tableFrom(
            ImmutableList<String> columnNames,
            ImmutableList<? extends ColumnDefinition> columnDefinitions,
            ImmutableList<ImmutableList<Object>> content) {
        return simpleTableFrom(columnNames, columnDefinitions, content);
    }

}
