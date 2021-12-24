package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import hu.webarticum.miniconnect.rdmsframework.execution.Query;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;

public class FakeSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        return new FakeQuery();
    }

}
