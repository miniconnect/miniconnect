package hu.webarticum.miniconnect.rdmsframework.storage.impl.simple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.SortMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScanningTableIndexTest {
    
    private Table table;
    

    @BeforeEach
    void init() {
        table = SimpleTable.builder()
                .addColumn("id", new SimpleColumnDefinition())
                .addColumn("firstname", new SimpleColumnDefinition())
                .addColumn("lastname", new SimpleColumnDefinition())
                .addColumn("country", new SimpleColumnDefinition())
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
    void testFindValueNoResult() {
        assertThat(index("id").find(99)).isEmpty();
    }
    
    @Test
    void testFindValueSingleResult() {
        assertThat(index("id").find(3)).containsExactly(larges(2));
    }

    @Test
    void testFindValueMoreResults() {
        assertThat(index("lastname").find("Smith")).containsExactly(larges(1, 2));
    }

    @Test
    void testFindMultiColumnNoResult() {
        assertThat(index("firstname", "lastname").findMulti(ImmutableList.of("Lorem", "Ipsum")))
                .isEmpty();
    }

    @Test
    void testFindMultiColumnSingleResult() {
        assertThat(index("firstname", "lastname").findMulti(ImmutableList.of("Karl", "Marx")))
                .containsExactly(larges(3));
    }

    @Test
    void testFindMultiColumnMoreResults() {
        assertThat(index("firstname", "lastname").findMulti(ImmutableList.of("Anton", "Bruckner")))
                .containsExactly(larges(4, 6));
    }

    @Test
    void testFindMultiColumnPartialMoreResults() {
        assertThat(index("lastname", "firstname").findMulti(ImmutableList.of("Smith")))
                .containsExactly(larges(1, 2));
    }

    @Test
    void testFindMultiColumnPartialNoResult() {
        assertThat(index("lastname", "firstname").findMulti(ImmutableList.of("Lorem")))
                .isEmpty();
    }

    @Test
    void testFindToInclusive() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.of("Marx", "Karl"),
                InclusionMode.INCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.UNSORTED));
        assertThat(selection).containsExactly(larges(3, 4, 5, 6));
    }

    @Test
    void testFindToExclusiveSomeResultsNotSorted() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.of("Marx", "Karl"),
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.UNSORTED));
        assertThat(selection).containsExactlyInAnyOrder(larges(4, 5, 6));
    }

    @Test
    void testFindToNonExistingExclusiveSomeResultsNotSorted() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.of("Nash", "John"),
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.UNSORTED));
        assertThat(selection).containsExactlyInAnyOrder(larges(3, 4, 5, 6));
    }

    @Test
    void testFindFromInclusive() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                ImmutableList.of("Marx", "Karl"),
                InclusionMode.INCLUDE,
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.ASC_NULLS_FIRST));
        assertThat(selection).containsExactly(larges(3, 0, 1, 2));
    }

    @Test
    void testFindFromExclusiveSomeResults() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                ImmutableList.of("Marx", "Karl"),
                InclusionMode.EXCLUDE,
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.ASC_NULLS_FIRST));
        assertThat(selection).containsExactly(larges(0, 1, 2));
    }

    @Test
    void testFindFromNonExistingExclusiveSomeResults() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                ImmutableList.of("Nash", "John"),
                InclusionMode.EXCLUDE,
                null,
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.ASC_NULLS_FIRST));
        assertThat(selection).containsExactly(larges(0, 1, 2));
    }
    
    @Test
    void testFindRangeExclusiveSingleResult() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                ImmutableList.of("Marx", "Karl"),
                InclusionMode.EXCLUDE,
                ImmutableList.of("Smith", "Adam"),
                InclusionMode.EXCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.ASC_NULLS_FIRST));
        assertThat(selection).containsExactly(larges(0));
    }
    
    @Test
    void testFindOddRangeInclusiveSomeResults() {
        TableSelection selection = index("lastname", "firstname").findMulti(
                ImmutableList.of("Bruckner"),
                InclusionMode.EXCLUDE,
                ImmutableList.of("Smith", "Adam"),
                InclusionMode.INCLUDE,
                ImmutableList.fill(2, NullsMode.WITH_NULLS),
                ImmutableList.fill(2, SortMode.ASC_NULLS_FIRST));
        assertThat(selection).containsExactly(larges(3, 0, 1));
    }
    
    
    private ScanningTableIndex index(String... columnNames) {
        return new ScanningTableIndex(table, "index", ImmutableList.of(columnNames));
    }
    
    private LargeInteger[] larges(int... values) {
        LargeInteger[] result = new LargeInteger[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = LargeInteger.of(values[i]);
        }
        return result;
    }
    
}
