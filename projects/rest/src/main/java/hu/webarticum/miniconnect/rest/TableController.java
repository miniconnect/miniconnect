package hu.webarticum.miniconnect.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;

// TODO: make queries configurable
// TODO: add support for name singularization
// TODO: add support for single-row selection by primary key
// TODO: add support for sub-entities by foreign key
@Controller("/tables")
public class TableController {
    
    private final MiniSession session;
    
    
    public TableController(MiniSession session) {
        this.session = session;
    }
    
    // TODO: make it unified
    // FIXME: describe?
    @Get("/")
    public List<Map<String, Object>> handle() {
        List<Map<String, Object>> result = new ArrayList<>();
        MiniResultSet resultSet = session.execute("SHOW TABLES").resultSet();
        ImmutableList<String> columnNames = resultSet.columnHeaders().map(MiniColumnHeader::name);
        ResultTable resultTable = new ResultTable(resultSet);
        for (ResultRecord resultRecord : resultTable) {
            ImmutableMap<String, Object> row = columnNames.assign(n -> resultRecord.get(n).get());
            result.add(row.asMap());
        }
        return result;
    }

    // FIXME
    @Get("/{tableName}")
    public Map<String, Object> handleTable(
            @QueryValue("tableName") String tableName) {
        MiniResultSet resultSet = session.execute("SHOW TABLES").resultSet();
        ImmutableList<String> columnNames = resultSet.columnHeaders().map(MiniColumnHeader::name);
        ResultTable resultTable = new ResultTable(resultSet);
        for (ResultRecord resultRecord : resultTable) {
            String rowTableName = resultRecord.get(0).as(String.class);
            if (rowTableName.equals(tableName)) {
                return columnNames.assign(n -> resultRecord.get(n).get()).asMap();
            }
        }
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Table not found: " + tableName);
    }

    //TODO: add support for search, order and limit
    @Get("/{tableName}/rows")
    public List<Map<String, Object>> handleTableData(
            @QueryValue("tableName") String tableName) {
        List<Map<String, Object>> result = new ArrayList<>();
        String quotedTableName = "`" + tableName.replace("`", "``") + "`";
        String sql = "SELECT * FROM " + quotedTableName;
        MiniResultSet resultSet = session.execute(sql).resultSet();
        ImmutableList<String> columnNames = resultSet.columnHeaders().map(MiniColumnHeader::name);
        ResultTable resultTable = new ResultTable(resultSet);
        for (ResultRecord resultRecord : resultTable) {
            ImmutableMap<String, Object> row = columnNames.assign(n -> resultRecord.get(n).get());
            result.add(row.asMap());
        }
        return result;
    }
    
}
