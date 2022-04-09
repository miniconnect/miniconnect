package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.AbstractWritableTableTest;

// TODO: test indexes after implemented
class DiffTableTest extends AbstractWritableTableTest {

    @Test
    void testBaseTableUntouched() {
        Table baseTable = createSimpleTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch patch1 = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(patch1);

        TablePatch patch2 = TablePatch.builder()
                .insert(ImmutableList.of(8, "XXX", true))
                .update(BigInteger.ONE, ImmutableMap.of(1, "uuuu", 2, false))
                .delete(BigInteger.ZERO)
                .delete(BigInteger.valueOf(3L))
                .build();
        diffTable.applyPatch(patch2);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(3, "uuuu", false),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(6, "BBB", false),
                ImmutableList.of(7, "CCC", true),
                ImmutableList.of(8, "XXX", true));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createSubjectTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Override
    protected DiffTable tableFrom(
            ImmutableList<String> columnNames,
            ImmutableList<? extends ColumnDefinition> columnDefinitions,
            ImmutableList<ImmutableList<Object>> content) {
        return new DiffTable(simpleTableFrom(columnNames, columnDefinitions, content));
    }
    
}
