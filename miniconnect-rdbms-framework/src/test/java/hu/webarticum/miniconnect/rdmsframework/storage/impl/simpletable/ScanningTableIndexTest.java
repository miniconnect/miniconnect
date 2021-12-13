package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelectionEntry;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.fakecolumn.FakeColumnDefinition;
import hu.webarticum.miniconnect.util.data.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;

class ScanningTableIndexTest {
    
    private Table table;
    

    @BeforeEach
    void init() {
        table = SimpleTable.builder()
                .addColumn("id", new FakeColumnDefinition())
                .addColumn("firstname", new FakeColumnDefinition())
                .addColumn("lastname", new FakeColumnDefinition())
                .addColumn("country", new FakeColumnDefinition())
                .addRow(ImmutableList.of(1, "Sándor", "Petőfi", "Hungary"))
                .addRow(ImmutableList.of(2, "Adam", "Smith", "England"))
                .addRow(ImmutableList.of(3, "Will", "Smith", "USA"))
                .addRow(ImmutableList.of(4, "Karl", "Marx", "Germany"))
                .addRow(ImmutableList.of(5, "Anton", "Bruckner", "Germany"))
                .addRow(ImmutableList.of(6, "Anonymus", null, "Hungary"))
                .addRow(ImmutableList.of(7, "Anton", "Bruckner", "Germany"))
                .build();
    }

    @Test
    void testNonExistingColumn() {
        assertThatThrownBy(() -> index("lorem")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNoResult() {
        assertThat(index("id").findValue(99)).isEmpty();
    }
    
    @Test
    void testSingleResult() {
        assertThat(index("id").findValue(3))
                .map(TableSelectionEntry::tableIndex)
                .containsExactly(bigs(2));
    }

    @Test
    void testMoreResults() {
        assertThat(index("lastname").findValue("Smith"))
                .map(TableSelectionEntry::tableIndex)
                .containsExactly(bigs(1, 2));
    }

    @Test
    void testMultiColumnNoResult() {
        assertThat(index("firstname", "lastname").findValue(ImmutableList.of("Lorem", "Ipsum")))
                .isEmpty();
    }

    @Test
    void testMultiColumnSingleResult() {
        assertThat(index("firstname", "lastname").find(ImmutableList.of("Karl", "Marx")))
                .map(TableSelectionEntry::tableIndex)
                .containsExactly(bigs(3));
    }

    @Test
    void testMultiColumnMoreResults() {
        assertThat(index("firstname", "lastname").find(ImmutableList.of("Anton", "Bruckner")))
                .map(TableSelectionEntry::tableIndex)
                .containsExactly(bigs(4, 6));
    }
    
    // TODO
    
    
    private ScanningTableIndex index(String... columnNames) {
        return new ScanningTableIndex(table, "index", ImmutableList.of(columnNames));
    }
    
    private BigInteger[] bigs(int... values) {
        BigInteger[] result = new BigInteger[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = BigInteger.valueOf(values[i]);
        }
        return result;
    }
    
}
