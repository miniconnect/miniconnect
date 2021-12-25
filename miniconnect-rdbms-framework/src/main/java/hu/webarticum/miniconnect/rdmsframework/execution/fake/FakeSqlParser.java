package hu.webarticum.miniconnect.rdmsframework.execution.fake;

import hu.webarticum.miniconnect.rdmsframework.execution.SqlParser;
import hu.webarticum.miniconnect.rdmsframework.query.Query;

public class FakeSqlParser implements SqlParser {

    @Override
    public Query parse(String sql) {
        return new FakeQuery();
    }

}
