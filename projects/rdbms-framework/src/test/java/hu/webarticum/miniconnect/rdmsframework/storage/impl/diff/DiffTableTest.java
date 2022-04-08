package hu.webarticum.miniconnect.rdmsframework.storage.impl.diff;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleTable;

// TODO: test indexes after implemented
class DiffTableTest {

    @Test
    void testInsert() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        TablePatch patch = TablePatch.builder()
                .insert(ImmutableList.of(5, "hello", true))
                .insert(ImmutableList.of(6, "world", false))
                .build();
        diffTable.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "xxx", true),
                ImmutableList.of(4, "yyy", false),
                ImmutableList.of(5, "hello", true),
                ImmutableList.of(6, "world", false));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testUpdate() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        TablePatch patch = TablePatch.builder()
                .update(BigInteger.valueOf(1L), ImmutableMap.of(1, "IPPSUM"))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "EX", 2, false))
                .build();
        diffTable.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(2, "IPPSUM", false),
                ImmutableList.of(3, "EX", false),
                ImmutableList.of(4, "yyy", false));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testDelete() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        TablePatch patch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .delete(BigInteger.valueOf(3L))
                .build();
        diffTable.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(3, "xxx", true));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testComplex() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        TablePatch patch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV", 2, true))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "UUU", true),
                ImmutableList.of(4, "VVV", true),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "BBB", false));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testComplexThenInsert() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(complexPatch);

        TablePatch insertPatch = TablePatch.builder()
                .insert(ImmutableList.of(8, "888", true))
                .insert(ImmutableList.of(9, "999", false))
                .build();
        diffTable.applyPatch(insertPatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "UUU", true),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "BBB", false),
                ImmutableList.of(7, "CCC", true),
                ImmutableList.of(8, "888", true),
                ImmutableList.of(9, "999", false));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testComplexThenUpdate() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(complexPatch);

        TablePatch updatePatch = TablePatch.builder()
                .update(BigInteger.ZERO, ImmutableMap.of(2, true))
                .update(BigInteger.ONE, ImmutableMap.of(1, "uuuu", 2, false))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(0, 44))
                .update(BigInteger.valueOf(4L), ImmutableMap.of(1, "bbbb"))
                .build();
        diffTable.applyPatch(updatePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", true),
                ImmutableList.of(3, "uuuu", false),
                ImmutableList.of(44, "VVV", false),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "bbbb", false),
                ImmutableList.of(7, "CCC", true));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testComplexThenDelete() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(complexPatch);

        TablePatch deletePatch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .delete(BigInteger.valueOf(3L))
                .delete(BigInteger.valueOf(4L))
                .build();
        diffTable.applyPatch(deletePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(7, "CCC", true));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testComplexThenComplex() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(complexPatch);

        TablePatch complex2Patch = TablePatch.builder()
                .insert(ImmutableList.of(8, "XXX", true))
                .update(BigInteger.ONE, ImmutableMap.of(1, "uuuu", 2, false))
                .delete(BigInteger.ZERO)
                .delete(BigInteger.valueOf(3L))
                .build();
        diffTable.applyPatch(complex2Patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(3, "uuuu", false),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(6, "BBB", false),
                ImmutableList.of(7, "CCC", true),
                ImmutableList.of(8, "XXX", true));
        
        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent);
    }

    @Test
    void testThreeDeletes() {
        Table baseTable = createBaseTable();
        DiffTable diffTable = new DiffTable(baseTable);
        
        TablePatch delete1Patch = TablePatch.builder()
                .delete(BigInteger.ZERO)
                .delete(BigInteger.valueOf(2L))
                .build();
        diffTable.applyPatch(delete1Patch);
        ImmutableList<ImmutableList<Object>> expectedContent1 = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(4, "yyy", false));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent1);

        TablePatch delete2Patch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .build();
        diffTable.applyPatch(delete2Patch);
        ImmutableList<ImmutableList<Object>> expectedContent2 = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false));
        assertThat(contentOf(diffTable)).isEqualTo(expectedContent2);

        TablePatch delete3Patch = TablePatch.builder()
                .delete(BigInteger.ZERO)
                .build();
        diffTable.applyPatch(delete3Patch);
        assertThat(contentOf(diffTable)).isEmpty();

        assertThat(contentOf(baseTable)).isEqualTo(contentOf(createBaseTable()));
    }

    private ImmutableList<ImmutableList<Object>> contentOf(Table table) {
        List<ImmutableList<Object>> resultBuilder = new ArrayList<>();
        BigInteger size = table.size();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(size) < 0; i = i.add(BigInteger.ONE)) {
            resultBuilder.add(table.row(i).getAll());
        }
        return ImmutableList.fromCollection(resultBuilder);
    }
    
    private Table createBaseTable() {
        return SimpleTable.builder()
                .addColumn("id", new SimpleColumnDefinition(Integer.class, false))
                .addColumn("label", new SimpleColumnDefinition(Integer.class, false))
                .addColumn("is_owned", new SimpleColumnDefinition(Boolean.class, false))
                .addRow(ImmutableList.of(1, "lorem", true))
                .addRow(ImmutableList.of(2, "ipsum", false))
                .addRow(ImmutableList.of(3, "xxx", true))
                .addRow(ImmutableList.of(4, "yyy", false))
                .build();
    }
    
}
