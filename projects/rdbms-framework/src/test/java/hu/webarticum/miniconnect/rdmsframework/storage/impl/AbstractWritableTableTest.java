package hu.webarticum.miniconnect.rdmsframework.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.rdmsframework.storage.ColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.TablePatch;
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
    protected void testInsert() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .insert(ImmutableList.of(5, "hello", true))
                .insert(ImmutableList.of(6, "world", false))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "xxx", true),
                ImmutableList.of(4, "yyy", false),
                ImmutableList.of(5, "hello", true),
                ImmutableList.of(6, "world", false));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testUpdate() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .update(BigInteger.valueOf(1L), ImmutableMap.of(1, "IPPSUM"))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "EX", 2, false))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(2, "IPPSUM", false),
                ImmutableList.of(3, "EX", false),
                ImmutableList.of(4, "yyy", false));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testDelete() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .delete(BigInteger.valueOf(3L))
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(3, "xxx", true));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplex() {
        Table table = createSubjectTable();
        TablePatch patch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV", 2, true))
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "UUU", true),
                ImmutableList.of(4, "VVV", true),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "BBB", false));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenInsert() {
        Table table = createSubjectTable();
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(complexPatch);

        TablePatch insertPatch = TablePatch.builder()
                .insert(ImmutableList.of(8, "888", true))
                .insert(ImmutableList.of(9, "999", false))
                .build();
        table.applyPatch(insertPatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "UUU", true),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "BBB", false),
                ImmutableList.of(7, "CCC", true),
                ImmutableList.of(8, "888", true),
                ImmutableList.of(9, "999", false));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenUpdate() {
        Table table = createSubjectTable();
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(complexPatch);

        TablePatch updatePatch = TablePatch.builder()
                .update(BigInteger.ZERO, ImmutableMap.of(2, true))
                .update(BigInteger.ONE, ImmutableMap.of(1, "uuuu", 2, false))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(0, 44))
                .update(BigInteger.valueOf(4L), ImmutableMap.of(1, "bbbb"))
                .build();
        table.applyPatch(updatePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", true),
                ImmutableList.of(3, "uuuu", false),
                ImmutableList.of(44, "VVV", false),
                ImmutableList.of(5, "AAA", true),
                ImmutableList.of(6, "bbbb", false),
                ImmutableList.of(7, "CCC", true));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenDelete() {
        Table table = createSubjectTable();
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(complexPatch);

        TablePatch deletePatch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .delete(BigInteger.valueOf(3L))
                .delete(BigInteger.valueOf(4L))
                .build();
        table.applyPatch(deletePatch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(7, "CCC", true));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testComplexThenComplex() {
        Table table = createSubjectTable();
        
        TablePatch complexPatch = TablePatch.builder()
                .insert(ImmutableList.of(5, "AAA", true))
                .insert(ImmutableList.of(6, "BBB", false))
                .insert(ImmutableList.of(7, "CCC", true))
                .update(BigInteger.valueOf(2L), ImmutableMap.of(1, "UUU"))
                .update(BigInteger.valueOf(3L), ImmutableMap.of(1, "VVV"))
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(complexPatch);

        TablePatch complex2Patch = TablePatch.builder()
                .insert(ImmutableList.of(8, "XXX", true))
                .update(BigInteger.ONE, ImmutableMap.of(1, "uuuu", 2, false))
                .delete(BigInteger.ZERO)
                .delete(BigInteger.valueOf(3L))
                .build();
        table.applyPatch(complex2Patch);

        ImmutableList<ImmutableList<Object>> expectedContent = ImmutableList.of(
                ImmutableList.of(3, "uuuu", false),
                ImmutableList.of(4, "VVV", false),
                ImmutableList.of(6, "BBB", false),
                ImmutableList.of(7, "CCC", true),
                ImmutableList.of(8, "XXX", true));
        
        assertThat(contentOf(table)).isEqualTo(expectedContent);
    }

    @Test
    protected void testThreeDeletes() {
        Table table = createSubjectTable();
        
        TablePatch delete1Patch = TablePatch.builder()
                .delete(BigInteger.ZERO)
                .delete(BigInteger.valueOf(2L))
                .build();
        table.applyPatch(delete1Patch);
        ImmutableList<ImmutableList<Object>> expectedContent1 = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(4, "yyy", false));
        assertThat(contentOf(table)).isEqualTo(expectedContent1);

        TablePatch delete2Patch = TablePatch.builder()
                .delete(BigInteger.ONE)
                .build();
        table.applyPatch(delete2Patch);
        ImmutableList<ImmutableList<Object>> expectedContent2 = ImmutableList.of(
                ImmutableList.of(2, "ipsum", false));
        assertThat(contentOf(table)).isEqualTo(expectedContent2);

        TablePatch delete3Patch = TablePatch.builder()
                .delete(BigInteger.ZERO)
                .build();
        table.applyPatch(delete3Patch);
        assertThat(contentOf(table)).isEmpty();
    }

    protected Table createSubjectTable() {
        return tableFrom(defaultColumnNames(), defaultColumnDefinitions(), defaultContent());
    }

    protected SimpleTable createSimpleTable() {
        return simpleTableFrom(defaultColumnNames(), defaultColumnDefinitions(), defaultContent());
    }
    
    protected ImmutableList<String> defaultColumnNames() {
        return ImmutableList.of("id", "label", "is_owned");
    }

    protected ImmutableList<ColumnDefinition> defaultColumnDefinitions() {
        return ImmutableList.of(
                new SimpleColumnDefinition(Integer.class, false),
                new SimpleColumnDefinition(String.class, false),
                new SimpleColumnDefinition(Boolean.class, false));
    }

    protected ImmutableList<ImmutableList<Object>> defaultContent() {
        return ImmutableList.of(
                ImmutableList.of(1, "lorem", true),
                ImmutableList.of(2, "ipsum", false),
                ImmutableList.of(3, "xxx", true),
                ImmutableList.of(4, "yyy", false));
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
        return builder.build();
    }
    
}
