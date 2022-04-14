package hu.webarticum.miniconnect.rdmsframework.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.NamedResourceStore;
import hu.webarticum.miniconnect.rdmsframework.storage.RangeSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.SortMode;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleTable;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleTable.SimpleTableBuilder;


public abstract class AbstractWritableTableTest {

    @Test
    protected void testWritable() {
        Table table = createSubjectTable();
        assertThat(table.isWritable()).isTrue();
    }

    @Test
    protected void testContent() {
        Table table = createSubjectTable();
        ImmutableList<ImmutableList<Object>> expectedContent = defaultContent();
        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testInsert() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .insert(ImmutableList.of(big(15), "zzzz", 2))
                .insert(ImmutableList.of(big(20), "yyyy", 3))
                .insert(ImmutableList.of(big(25), "xxxx", 4))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(1), "eeee", 2),
                ImmutableList.of(big(2), "bbbb", 1),
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "aaaa", 5),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jjjj", 5),
                ImmutableList.of(big(8), "dddd", 2),
                ImmutableList.of(big(9), "iiii", 3),
                ImmutableList.of(big(10), "ffff", 1),
                ImmutableList.of(big(15), "zzzz", 2),
                ImmutableList.of(big(20), "yyyy", 3),
                ImmutableList.of(big(25), "xxxx", 4));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testUpdate() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .update(BigInteger.valueOf(1L), ImmutableMap.of(1, "oooo"))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "pppp", 2, 0))
                .update(BigInteger.valueOf(5L), ImmutableMap.of(0, big(106), 1, "qqqq"))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(1), "eeee", 2),
                ImmutableList.of(big(2), "oooo", 1),
                ImmutableList.of(big(3), "pppp", 0),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "aaaa", 5),
                ImmutableList.of(big(106), "qqqq", 4),
                ImmutableList.of(big(7), "jjjj", 5),
                ImmutableList.of(big(8), "dddd", 2),
                ImmutableList.of(big(9), "iiii", 3),
                ImmutableList.of(big(10), "ffff", 1));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testDelete() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .delete(big(0))
                .delete(big(2))
                .delete(big(3))
                .delete(big(7))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(2), "bbbb", 1),
                ImmutableList.of(big(5), "aaaa", 5),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jjjj", 5),
                ImmutableList.of(big(9), "iiii", 3),
                ImmutableList.of(big(10), "ffff", 1));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplex() {
        Table table = createSubjectTable();
        TablePatch patch = createDefaultComplexTablePatch();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(1), "eeee", 2),
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "ii", 0),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jj", 5),
                ImmutableList.of(big(10), "ffff", 1),
                ImmutableList.of(big(101), "dd", 2),
                ImmutableList.of(big(102), "ee", 4),
                ImmutableList.of(big(103), "ff", 3),
                ImmutableList.of(big(104), "gg", 5));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenInsert() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch insertPatch = TablePatch.builder()
                .insert(ImmutableList.of(big(1001), "mmmmm", 1))
                .insert(ImmutableList.of(big(1002), "nnnnn", 2))
                .build();
        table.applyPatch(insertPatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(1), "eeee", 2),
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "ii", 0),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jj", 5),
                ImmutableList.of(big(10), "ffff", 1),
                ImmutableList.of(big(101), "dd", 2),
                ImmutableList.of(big(102), "ee", 4),
                ImmutableList.of(big(103), "ff", 3),
                ImmutableList.of(big(104), "gg", 5),
                ImmutableList.of(big(1001), "mmmmm", 1),
                ImmutableList.of(big(1002), "nnnnn", 2));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenUpdate() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch updatePatch = TablePatch.builder()
                .update(big(0), ImmutableMap.of(1, "111"))
                .update(big(1), ImmutableMap.of(1, "222", 2, 5))
                .update(big(4), ImmutableMap.of(1, "333"))
                .update(big(5), ImmutableMap.of(1, "444"))
                .update(big(6), ImmutableMap.of(1, "555"))
                .update(big(8), ImmutableMap.of(1, "666"))
                .update(big(10), ImmutableMap.of(0, big(1104), 1, "777"))
                .build();
        table.applyPatch(updatePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(1), "111", 2),
                ImmutableList.of(big(3), "222", 5),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "ii", 0),
                ImmutableList.of(big(6), "333", 4),
                ImmutableList.of(big(7), "444", 5),
                ImmutableList.of(big(10), "555", 1),
                ImmutableList.of(big(101), "dd", 2),
                ImmutableList.of(big(102), "666", 4),
                ImmutableList.of(big(103), "ff", 3),
                ImmutableList.of(big(1104), "777", 5));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenDelete() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch deletePatch = TablePatch.builder()
                .delete(big(0))
                .delete(big(1))
                .delete(big(3))
                .delete(big(7))
                .delete(big(8))
                .build();
        table.applyPatch(deletePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jj", 5),
                ImmutableList.of(big(10), "ffff", 1),
                ImmutableList.of(big(103), "ff", 3),
                ImmutableList.of(big(104), "gg", 5));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenComplex() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch complex2Patch = TablePatch.builder()
                .insert(ImmutableList.of(big(1005), "YY", 1))
                .insert(ImmutableList.of(big(1006), "ZZ", 2))
                .update(big(4), ImmutableMap.of(1, "mmm", 2, 2))
                .update(big(5), ImmutableMap.of(1, "nnn"))
                .update(big(10), ImmutableMap.of(1, "ooo"))
                .delete(big(0))
                .delete(big(2))
                .delete(big(6))
                .delete(big(8))
                .build();
        table.applyPatch(complex2Patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(5), "ii", 0),
                ImmutableList.of(big(6), "mmm", 2),
                ImmutableList.of(big(7), "nnn", 5),
                ImmutableList.of(big(101), "dd", 2),
                ImmutableList.of(big(103), "ff", 3),
                ImmutableList.of(big(104), "ooo", 5),
                ImmutableList.of(big(1005), "YY", 1),
                ImmutableList.of(big(1006), "ZZ", 2));

        assertThat(table.size()).isEqualTo(big(expectedContent.size()));
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testThreeDeletes() {
        Table table = createSubjectTable();
        
        TablePatch delete1Patch = TablePatch.builder()
                .delete(big(1))
                .delete(big(6))
                .delete(big(7))
                .build();
        table.applyPatch(delete1Patch);
        ImmutableList<ImmutableList<Object>> expectedContent1 = ImmutableList.of(
                        ImmutableList.of(big(1), "eeee", 2),
                        ImmutableList.of(big(3), "gggg", 3),
                        ImmutableList.of(big(4), "cccc", 4),
                        ImmutableList.of(big(5), "aaaa", 5),
                        ImmutableList.of(big(6), "hhhh", 4),
                        ImmutableList.of(big(9), "iiii", 3),
                        ImmutableList.of(big(10), "ffff", 1));
        assertThat(table.size()).isEqualTo(expectedContent1.size());
        assertThat(contentOf(table)).isEqualTo(expectedContent1);
        
        TablePatch delete2Patch = TablePatch.builder()
                .delete(big(0))
                .delete(big(2))
                .build();
        table.applyPatch(delete2Patch);
        ImmutableList<ImmutableList<Object>> expectedContent2 = ImmutableList.of(
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(5), "aaaa", 5),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(9), "iiii", 3),
                ImmutableList.of(big(10), "ffff", 1));
        assertThat(table.size()).isEqualTo(expectedContent2.size());
        assertThat(contentOf(table)).isEqualTo(expectedContent2);
        
        TablePatch delete3Patch = TablePatch.builder()
                .delete(big(1))
                .delete(big(4))
                .build();
        table.applyPatch(delete3Patch);
        ImmutableList<ImmutableList<Object>> expectedContent3 = ImmutableList.of(
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(9), "iiii", 3));
        assertThat(table.size()).isEqualTo(expectedContent3.size());
        assertThat(contentOf(table)).isEqualTo(expectedContent3);
    }

    @Test
    protected void testIndexes() {
        Table table = createSubjectTable();
        NamedResourceStore<TableIndex> indexes = table.indexes();
        assertThat(indexes.names()).containsExactlyInAnyOrder(
                "idx_id", "idx_label", "idx_level");
        assertThat(indexes.get("idx_id").columnNames()).isEqualTo(
                ImmutableList.of("id"));
        assertThat(indexes.get("idx_label").columnNames()).isEqualTo(
                ImmutableList.of("label"));
        assertThat(indexes.get("idx_level").columnNames()).isEqualTo(
                ImmutableList.of("level"));
    }

    @Test
    protected void testIndexFind() {
        Table table = createSubjectTable();
        TableIndex index = table.indexes().get("idx_label");
        TableSelection selection = index.find(
                "dddd",
                InclusionMode.EXCLUDE,
                "gggg",
                InclusionMode.INCLUDE,
                NullsMode.NO_NULLS,
                SortMode.UNSORTED);
        
        assertThat(selection).containsExactlyInAnyOrder(bigs(0, 2, 9));
        assertThat(new RangeSelection(BigInteger.ZERO, table.size()))
                .filteredOn(selection::containsRow)
                .containsExactly(bigs(0, 2, 9));
    }

    @Test
    protected void testIndexFindAfterModifications() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch complex2Patch = TablePatch.builder()
                .insert(ImmutableList.of(big(1005), "YY", 1))
                .insert(ImmutableList.of(big(1006), "ZZ", 2))
                .update(big(4), ImmutableMap.of(1, "mmm", 2, 2))
                .update(big(5), ImmutableMap.of(1, "nnn"))
                .update(big(10), ImmutableMap.of(1, "ooo"))
                .delete(big(0))
                .delete(big(2))
                .delete(big(6))
                .delete(big(8))
                .build();
        table.applyPatch(complex2Patch);

        TableIndex index = table.indexes().get("idx_label");
        TableSelection selection = index.find(
                "gg",
                InclusionMode.INCLUDE,
                "yyy",
                InclusionMode.EXCLUDE,
                NullsMode.NO_NULLS,
                SortMode.UNSORTED);
        
        assertThat(selection).containsExactlyInAnyOrder(bigs(0, 1, 2, 3, 6, 7));
        assertThat(new RangeSelection(big(0), table.size()))
                .filteredOn(selection::containsRow)
                .containsExactly(bigs(0, 1, 2, 3, 6, 7));
    }

    @Test
    protected void testIndexFindSortedAfterModifications() {
        Table table = createSubjectTable();
        TablePatch complexPatch = createDefaultComplexTablePatch();
        table.applyPatch(complexPatch);

        TablePatch complex2Patch = TablePatch.builder()
                .insert(ImmutableList.of(big(1005), "KK", 2))
                .insert(ImmutableList.of(big(1006), "ZZ", 4))
                .update(big(4), ImmutableMap.of(1, "nnn", 2, 2))
                .update(big(5), ImmutableMap.of(1, "mmm"))
                .update(big(10), ImmutableMap.of(1, "ooo"))
                .delete(big(0))
                .delete(big(2))
                .delete(big(6))
                .delete(big(8))
                .build();
        table.applyPatch(complex2Patch);

        TableIndex index = table.indexes().get("idx_label");
        TableSelection selection = index.find(
                "gg",
                InclusionMode.INCLUDE,
                "yyy",
                InclusionMode.EXCLUDE,
                NullsMode.NO_NULLS,
                SortMode.ASC_NULLS_FIRST); // TODO: DESC_NULLS_LAST

        //assertThat(selection).containsExactly(bigs(6, 2, 3, 7, 1, 0));
        assertThat(selection).containsExactly(bigs(0, 1, 7, 3, 2, 6));
        assertThat(new RangeSelection(big(0), table.size()))
                .filteredOn(selection::containsRow)
                .containsExactly(bigs(0, 1, 2, 3, 6, 7));
    }

    
    protected Table createSubjectTable() {
        return tableFrom(defaultColumnNames(), defaultColumnDefinitions(), defaultContent());
    }

    protected SimpleTable createSimpleTable() {
        return simpleTableFrom(defaultColumnNames(), defaultColumnDefinitions(), defaultContent());
    }
    
    protected ImmutableList<String> defaultColumnNames() {
        return ImmutableList.of("id", "label", "level");
    }

    protected ImmutableList<ColumnDefinition> defaultColumnDefinitions() {
        return ImmutableList.of(
                new SimpleColumnDefinition(BigInteger.class, false),
                new SimpleColumnDefinition(String.class, false),
                new SimpleColumnDefinition(Integer.class, false));
    }

    protected ImmutableList<ImmutableList<Object>> defaultContent() {
        return ImmutableList.of(
                ImmutableList.of(big(1), "eeee", 2),
                ImmutableList.of(big(2), "bbbb", 1),
                ImmutableList.of(big(3), "gggg", 3),
                ImmutableList.of(big(4), "cccc", 4),
                ImmutableList.of(big(5), "aaaa", 5),
                ImmutableList.of(big(6), "hhhh", 4),
                ImmutableList.of(big(7), "jjjj", 5),
                ImmutableList.of(big(8), "dddd", 2),
                ImmutableList.of(big(9), "iiii", 3),
                ImmutableList.of(big(10), "ffff", 1));
    }
    
    protected ImmutableList<ImmutableList<Object>> contentOf(Table table) {
        List<ImmutableList<Object>> resultBuilder = new ArrayList<>();
        BigInteger size = table.size();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(size) < 0; i = i.add(BigInteger.ONE)) {
            resultBuilder.add(table.row(i).getAll());
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    protected abstract Table tableFrom(
            ImmutableList<String> columnNames,
            ImmutableList<? extends ColumnDefinition> columnDefinitions,
            ImmutableList<ImmutableList<Object>> content);
    
    protected SimpleTable simpleTableFrom(
            ImmutableList<String> columnNames,
            ImmutableList<? extends ColumnDefinition> columnDefinitions,
            ImmutableList<ImmutableList<Object>> content) {
        SimpleTableBuilder builder = SimpleTable.builder();
        columnNames.forEachIndex((i, n) -> builder.addColumn(n, columnDefinitions.get(i)));
        content.forEach(builder::addRow);
        columnNames.forEach(n -> builder.addIndex("idx_" + n, ImmutableList.of(n)));
        return builder.build();
    }
    
    protected TablePatch createDefaultComplexTablePatch() {
        return TablePatch.builder()
                .insert(ImmutableList.of(big(101), "dd", 2))
                .insert(ImmutableList.of(big(102), "ee", 4))
                .insert(ImmutableList.of(big(103), "ff", 3))
                .insert(ImmutableList.of(big(104), "gg", 5))
                .update(big(4), ImmutableMap.of(1, "ii", 2, 0))
                .update(big(6), ImmutableMap.of(1, "jj", 2, 5))
                .delete(big(1))
                .delete(big(7))
                .delete(big(8))
                .build();
    }
    
    protected BigInteger big(long number) {
        return BigInteger.valueOf(number);
    }

    protected BigInteger[] bigs(long... numbers) {
        return Arrays.stream(numbers).mapToObj(this::big).toArray(BigInteger[]::new);
    }
    
}
