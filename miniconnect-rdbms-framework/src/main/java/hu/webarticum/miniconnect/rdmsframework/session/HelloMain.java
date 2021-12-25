package hu.webarticum.miniconnect.rdmsframework.session;

import java.util.function.Supplier;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.rdmsframework.execution.QueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeSqlParser;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeStorageAccess;
import hu.webarticum.miniconnect.rdmsframework.query.GeneralSqlParser;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccess;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class HelloMain {

    public static void main(String[] args) {
        //Supplier<SqlParser> sqlParser = FakeSqlParser::new;
        Supplier<SqlParser> sqlParser = GeneralSqlParser::new;
        Supplier<QueryExecutor> queryExecutor = FakeQueryExecutor::new;
        Supplier<StorageAccess> storageAccessFactory = FakeStorageAccess::new;
        try (MiniSession session = new FrameworkSession(
                sqlParser, queryExecutor, storageAccessFactory)) {
            MiniResult result = session.execute("SELECT 1 AS lorem");
            if (!result.success()) {
                System.out.println("oops");
                System.out.println(result.error().message());
            } else {
                System.out.println("OK");
                for (ImmutableList<MiniValue> row : result.resultSet()) {
                    for (MiniValue value : row) {
                        System.out.print(value.contentAccess().get().toString()); // FIXME
                        System.out.print(" | ");
                    }
                    System.out.println();
                }
            }
        }
    }
    
}
