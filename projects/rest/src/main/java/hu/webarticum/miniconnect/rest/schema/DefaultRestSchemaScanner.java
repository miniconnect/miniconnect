package hu.webarticum.miniconnect.rest.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.ResultRecord;
import hu.webarticum.miniconnect.record.ResultTable;

public class DefaultRestSchemaScanner implements RestSchemaScanner {

    // FIXME
    private static final String SHOW_TABLES_SQL = "SHOW TABLES";

    // FIXME
    private static final String LIST_PRIMARY_KEY_COLUMNS_SQL =
            "SELECT " +
            " kcu.COLUMN_NAME " +
            "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
            "LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON " +
            " kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA AND " +
            " kcu.TABLE_NAME = tc.TABLE_NAME AND " +
            " kcu.CONSTRAINT_NAME  = tc.CONSTRAINT_NAME " +
            "WHERE " +
            " tc.CONSTRAINT_TYPE = 'PRIMARY KEY' AND " +
            " tc.TABLE_NAME = %s AND " +
            " tc.TABLE_SCHEMA = SCHEMA()";

    // FIXME
    private static final String LIST_FOREIGN_KEYS_SQL =
            "SELECT " +
            " kcu.CONSTRAINT_NAME, " +
            " kcu.COLUMN_NAME, " +
            " kcu.REFERENCED_TABLE_NAME, " +
            " REFERENCED_COLUMN_NAME " +
            "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
            "LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON " +
            " kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA AND " +
            " kcu.TABLE_NAME = tc.TABLE_NAME AND " +
            " kcu.CONSTRAINT_NAME  = tc.CONSTRAINT_NAME " +
            "WHERE " +
            " tc.CONSTRAINT_TYPE = 'FOREIGN KEY' AND " +
            " tc.TABLE_NAME = %s AND " +
            " tc.TABLE_SCHEMA = SCHEMA()";

    
    @Override
    public RestSchema scanSchema(MiniSession session) {
        ImmutableList<String> tableNames = scanTableNames(session);
        ImmutableMap<String, String> tableRenames = tableNames.assign(this::normalizeTableName);
        ImmutableMap<String, ImmutableList<String>> primaryKeys =
                tableNames.assign(n -> scanPrimaryKey(session, n));
        ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>> rawForeignKeys =
                scanForeignKeys(session, tableNames);
        ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>> foreignKeys =
                filterOutCircularAssociations(rawForeignKeys);
        
        return new RestSchema(tableNames
                .assign(
                    n -> new RestSchemaResource(
                            n,
                            primaryKeys.get(n),
                            buildAssociations(n, tableRenames, primaryKeys, foreignKeys)))
                .map(n -> normalizeTableName(n), Function.identity()));
    }
    
    private ImmutableList<String> scanTableNames(MiniSession session) {
        List<String> resultBuilder = new ArrayList<>();
        MiniResult result = session.execute(SHOW_TABLES_SQL).requireSuccess();
        try (MiniResultSet resultSet = result.resultSet()) {
            for (ResultRecord resultRecord : new ResultTable(resultSet)) {
                String tableName = resultRecord.get(0).as(String.class);
                resultBuilder.add(tableName);
            }
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    private ImmutableList<String> scanPrimaryKey(MiniSession session, String tableName) {
        try {
            return scanPrimaryKeyUnsafe(session, tableName);
        } catch (Exception e) {
            return ImmutableList.empty();
        }
    }
    
    private ImmutableList<String> scanPrimaryKeyUnsafe(MiniSession session, String tableName) {
        List<String> resultBuilder = new ArrayList<>();
        String sql = String.format(LIST_PRIMARY_KEY_COLUMNS_SQL, quoteString(tableName));
        MiniResult result = session.execute(sql).requireSuccess();
        try (MiniResultSet resultSet = result.resultSet()) {
            for (ResultRecord resultRecord : new ResultTable(resultSet)) {
                String columnName = resultRecord.get(0).as(String.class);
                resultBuilder.add(columnName);
            }
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    // TODO: support for pluralization and format normalization (e. g. SOME_ENTITY -> some-entities)
    private String normalizeTableName(String tableName) {
        return tableName.toLowerCase();
    }

    private ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>>
            scanForeignKeys(
                    MiniSession session, ImmutableList<String> tableNames) {
        Map<ImmutableList<String>, ImmutableList<ImmutableList<String>>> resultBuilder =
                new HashMap<>();
        for (String tableName : tableNames) {
            ImmutableMap<String, ImmutableList<ImmutableList<String>>> tableForeignKeys =
                    scanTableForeignKeys(session, tableName);
            resultBuilder.putAll(tableForeignKeys
                    .map(k -> ImmutableList.of(tableName, k), Function.identity())
                    .asMap());
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    private ImmutableMap<String, ImmutableList<ImmutableList<String>>> scanTableForeignKeys(
            MiniSession session, String tableName) {
        try {
            return scanTableForeignKeysUnsafe(session, tableName);
        } catch (Exception e) {
            return ImmutableMap.empty();
        }
    }
    
    private ImmutableMap<String, ImmutableList<ImmutableList<String>>> scanTableForeignKeysUnsafe(
            MiniSession session, String tableName) {
        String sql = String.format(LIST_FOREIGN_KEYS_SQL, quoteString(tableName));
        MiniResult result = session.execute(sql).requireSuccess();
        Map<String, String> tablesByConstraint = new HashMap<>();
        Map<String, ImmutableList<List<String>>> columnAssociationsByConstraint = new HashMap<>();
        try (MiniResultSet resultSet = result.resultSet()) {
            for (ResultRecord resultRecord : new ResultTable(resultSet)) {
                String constraintName = resultRecord.get(0).as(String.class);
                String columnName = resultRecord.get(1).as(String.class);
                String referencedTableName = resultRecord.get(2).as(String.class);
                String referencedColumnName = resultRecord.get(3).as(String.class);
                
                tablesByConstraint.putIfAbsent(constraintName, referencedTableName);
                ImmutableList<List<String>> columnAssociation =
                        columnAssociationsByConstraint.computeIfAbsent(
                                constraintName,
                                n -> ImmutableList.of(new ArrayList<>(), new ArrayList<>()));
                columnAssociation.get(0).add(columnName);
                columnAssociation.get(1).add(referencedColumnName);
            }
        }
        
        Map<String, ImmutableList<ImmutableList<String>>> resultBuilder = new HashMap<>();
        for (Map.Entry<String, String> entry : tablesByConstraint.entrySet()) {
            String constraintName = entry.getKey();
            String referencedTableName = entry.getValue();
            ImmutableList<List<String>> columnAssociation =
                    columnAssociationsByConstraint.get(constraintName);
            ImmutableList<String> childColumnNames =
                    ImmutableList.fromCollection(columnAssociation.get(0));
            ImmutableList<String> parentColumnNames =
                    ImmutableList.fromCollection(columnAssociation.get(1));
            resultBuilder.put(
                    referencedTableName, ImmutableList.of(childColumnNames, parentColumnNames));
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    public ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>>
            filterOutCircularAssociations(
                        ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>>
                                rawForeignKeys) {
        Map<ImmutableList<String>, ImmutableList<ImmutableList<String>>> resultBuilder =
                new HashMap<>();
        Map<String, Set<String>> graphState = new HashMap<>();
        for (Map.Entry<ImmutableList<String>, ImmutableList<ImmutableList<String>>> entry :
                rawForeignKeys.entrySet()) {
            ImmutableList<String> tableAssociation = entry.getKey();
            ImmutableList<ImmutableList<String>> keyAssociation = entry.getValue();
            String from = tableAssociation.get(0);
            String to = tableAssociation.get(1);
            Set<String> fromSources = graphState.computeIfAbsent(from, k -> new HashSet<>());
            if (fromSources.contains(to)) {
                continue;
            }
            
            Set<String> toSources = graphState.computeIfAbsent(to, k -> new HashSet<>());
            toSources.add(from);
            toSources.addAll(fromSources);
            for (Set<String> sources : graphState.values()) {
                if (sources.contains(to)) {
                    sources.add(from);
                    sources.addAll(fromSources);
                }
            }
            
            resultBuilder.put(tableAssociation, keyAssociation);
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    private ImmutableMap<String, RestSchemaResourceAssociation> buildAssociations(
            String parentTableName,
            ImmutableMap<String, String> tableRenames,
            ImmutableMap<String, ImmutableList<String>> primaryKeys,
            ImmutableMap<ImmutableList<String>, ImmutableList<ImmutableList<String>>> foreignKeys) {
        Map<String, RestSchemaResourceAssociation> resultBuilder = new HashMap<>();
        for (Map.Entry<ImmutableList<String>, ImmutableList<ImmutableList<String>>> entry :
                foreignKeys.filter(k -> k.get(1).equals(parentTableName)).entrySet()) {
            ImmutableList<String> tableAssociation = entry.getKey();
            String childTableName = tableAssociation.get(0);
            ImmutableList<ImmutableList<String>> keyAssociation = entry.getValue();
            ImmutableList<String> childKey = keyAssociation.get(0);
            ImmutableList<String> parentKey = keyAssociation.get(1);
            String childResourceName = normalizeTableName(childTableName);

            ImmutableMap<String, RestSchemaResourceAssociation> childAssociatons =
                    buildAssociations(childTableName, tableRenames, primaryKeys, foreignKeys);
            RestSchemaResource associatedResource = new RestSchemaResource(
                    childTableName, primaryKeys.get(childTableName), childAssociatons);
            RestSchemaResourceAssociation association = new RestSchemaResourceAssociation(
                    parentKey, childKey, associatedResource);
            resultBuilder.put(childResourceName, association);
        }
        return ImmutableMap.fromMap(resultBuilder);
    }

    // FIXME
    private String quoteString(String value) {
        return "'" + value.replace("'", "\\'") + "'";
    }

}
