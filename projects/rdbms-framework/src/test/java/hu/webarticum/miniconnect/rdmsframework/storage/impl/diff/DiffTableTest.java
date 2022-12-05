package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.AbstractWritableTableTest;

class DiffTableTest extends AbstractWritableTableTest {

    @Test
    void testBaseTableUntouched() {
        Table baseTable = createSimpleTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch patch1 = TablePatch.builder()
                .insert(ImmutableList.of(large(11), "AAA", true))
                .insert(ImmutableList.of(large(12), "BBB", false))
                .insert(ImmutableList.of(large(13), "CCC", true))
                .update(large(2), ImmutableMap.of(1, "UUU"))
                .update(large(3), ImmutableMap.of(1, "VVV"))
                .delete(LargeInteger.ZERO)
                .build();
        diffTable.applyPatch(patch1);

        TablePatch patch2 = TablePatch.builder()
                .insert(ImmutableList.of(large(14), "XXX", true))
                .update(large(1), ImmutableMap.of(1, "uuuu", 2, false))
                .delete(large(0))
                .delete(large(3))
                .build();
        diffTable.applyPatch(patch2);

        assertThat(contentOf(baseTable)).isEqualTo(defaultContent());
    }

    @Override
    protected DiffTable tableFrom(
            ImmutableList<String> columnNames,
            ImmutableList<? extends ColumnDefinition> columnDefinitions,
            ImmutableList<ImmutableList<Object>> content) {
        return new DiffTable(simpleTableFrom(columnNames, columnDefinitions, content));
    }
    
}
