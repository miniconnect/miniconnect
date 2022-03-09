package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.engine.Engine;
import hu.webarticum.miniconnect.rdmsframework.engine.EngineSession;
import hu.webarticum.miniconnect.rdmsframework.engine.impl.SimpleEngine;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.execution.simple.SimpleSelectExecutor;
import hu.webarticum.miniconnect.rdmsframework.query.AntlrSqlParser;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.Table;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleColumnDefinition;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleStorageAccess;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleTable;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleTableManager;
import hu.webarticum.miniconnect.record.ResultTable;
import hu.webarticum.miniconnect.repl.ResultSetPrinter;

public class FrameworkMinimalDemoMain {

    public static void main(String[] args) throws IOException {
        SqlParser sqlParser = new AntlrSqlParser();
        QueryExecutor queryExecutor = new SimpleSelectExecutor();
        StorageAccess storageAccess = createStorageAccess();
        try (
                Engine engine = new SimpleEngine(sqlParser, queryExecutor, storageAccess);
                EngineSession engineSession = engine.openSession();
                MiniSession session = new FrameworkSession(engineSession)) {
            MiniResult result = session.execute(
                    "SELECT id, label AS apple, description banana FROM data " +
                            "WHERE label='Lorem' AND description='Hello' " +
                            "ORDER BY id DESC");
                    //"DELETE FROM data WHERE a=1 AND b='banana'");
                    //"UPDATE data SET col1=NULL, col2=99, col3='str' WHERE a=1 AND b='banana'");
                    //"INSERT INTO data (id, label, description) VALUES (1, 'banana', NULL)");
            if (!result.success()) {
                System.out.println("oops");
                System.out.println(result.error().message());
            } else {
                System.out.println("OK");
                new ResultSetPrinter().print(new ResultTable(result.resultSet()), System.out);
            }
        }
    }
    
    public static StorageAccess createStorageAccess() {
        SimpleStorageAccess storageAccess =  new SimpleStorageAccess();
        SimpleTableManager tableManager = storageAccess.tables();
        Table table = SimpleTable.builder()
                .name("data")
                .addColumnWithIndex("id", new SimpleColumnDefinition())
                .addColumnWithIndex("label", new SimpleColumnDefinition())
                .addColumnWithIndex("description", new SimpleColumnDefinition())
                .addRow(ImmutableList.of(1, "Lorem", "Hello"))
                .addRow(ImmutableList.of(2, "Lorem", "World"))
                .addRow(ImmutableList.of(3, "Lorem", "Hello"))
                .addRow(ImmutableList.of(4, "Ipsum", "Hello"))
                .addRow(ImmutableList.of(5, "Ipsum", "World"))
                .build();
        tableManager.registerTable(table);
        return storageAccess;
    }
    
}
