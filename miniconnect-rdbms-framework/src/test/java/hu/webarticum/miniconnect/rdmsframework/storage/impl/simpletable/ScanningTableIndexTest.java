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
                .addRow(ImmutableList.of(4, "Anton", "Bruckner", "Germany"))
                .addRow(ImmutableList.of(5, "Anonymus", null, "Hungary"))
                .build();
    }

    @Test
    void testNonExistingColumn() {
        assertThatThrownBy(() -> index("lorem")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNotFound() {
        assertThat(index("id").findValue(99)).isEmpty();
    }
    
    @Test
    void testFindSingle() {
        assertThat(index("id").findValue(3)).map(TableSelectionEntry::tableIndex)
                .containsExactly(bigs(2));
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
