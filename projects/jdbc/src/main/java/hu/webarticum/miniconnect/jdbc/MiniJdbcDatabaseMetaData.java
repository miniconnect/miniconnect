package hu.webarticum.miniconnect.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResultSet;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.translator.NullTranslator;
import hu.webarticum.miniconnect.record.translator.StringTranslator;

public class MiniJdbcDatabaseMetaData implements DatabaseMetaData {
    
    private static final int JDBC_MAJOR_VERSION = 4;
    
    private static final int JDBC_MINOR_VERSION = 2;
    
    private static final String DRIVER_NAME = "MiniConnect JDBC";
    
    private static final int DRIVER_MAJOR_VERSION = 0;
    
    private static final int DRIVER_MINOR_VERSION = 1;
    
    private static final String DRIVER_VERSION = DRIVER_MAJOR_VERSION + "." + DRIVER_MINOR_VERSION;
    
    
    private final MiniJdbcConnection connection;
    

    public MiniJdbcDatabaseMetaData(MiniJdbcConnection connection) {
        this.connection = connection;
    }
    

    @Override
    public MiniJdbcConnection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        if (!isWrapperFor(type)) {
            throw new SQLException(String.format("Unable to convert %s to %s", getClass(), type));
        }
        
        @SuppressWarnings("unchecked")
        T result = (T) this;
        return result;
    }

    @Override
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        return (type != null && type.isAssignableFrom(getClass()));
    }


    // --- DRIVER ---
    // [start]
    
    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return JDBC_MAJOR_VERSION;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return JDBC_MINOR_VERSION;
    }

    @Override
    public String getDriverName() throws SQLException {
        return DRIVER_NAME;
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return DRIVER_VERSION;
    }

    @Override
    public int getDriverMajorVersion() {
        return DRIVER_MAJOR_VERSION;
    }

    @Override
    public int getDriverMinorVersion() {
        return DRIVER_MINOR_VERSION;
    }
    
    // [end]


    // --- DATABASE ---
    // [start]
    
    @Override
    public String getDatabaseProductName() throws SQLException {
        return connection.getDatabaseProvider().getDatabaseProductName(connection.getMiniSession());
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return connection.getDatabaseProvider().getDatabaseFullVersion(connection.getMiniSession());
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return connection.getDatabaseProvider().getDatabaseMajorVersion(connection.getMiniSession());
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return connection.getDatabaseProvider().getDatabaseMinorVersion(connection.getMiniSession());
    }

    // [end]


    // --- CONNECTION ---
    // [start]
    
    @Override
    public String getURL() throws SQLException {
        return connection.getConnectionUrl();
    }

    @Override
    public String getUserName() throws SQLException {
        return connection.getDatabaseProvider().getUser(connection.getMiniSession());
    }

    // [end]


    // --- SUPPORT ---
    // [start]

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        // FIXME: what does this mean?
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true; // TODO
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true; // TODO
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return connection.supportsTransactionIsolationLevel(level);
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return concurrency == ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false; // FIXME / TODO
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return true; // FIXME: most likely guess
    }
    
    // [end]


    // --- LIMITS ---
    // [start]

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false; // FIXME: most likely guess
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 0; // FIXME: most likely guess
    }

    // [end]


    // --- SETTINGS ---
    // [start]

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_READ_COMMITTED;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return DatabaseMetaData.sqlStateSQL;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    // [end]


    // --- SYNTAX ---
    // [start]

    @Override
    public String getSQLKeywords() throws SQLException {
        return // FIXME: this is H2's list
                "CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,INTERSECTS,LIMIT,MINUS,OFFSET," +
                "QUALIFY,REGEXP,_ROWID_,ROWNUM,SYSDATE,SYSTIME,SYSTIMESTAMP,TODAY,TOP";
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\""; // FIXME: most likely guess
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\"; // FIXME: most likely guess
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return ""; // FIXME: most likely guess
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return "SCHEMA"; // FIXME: most likely guess
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE"; // FIXME: most likely guess
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "CATALOG"; // FIXME: most likely guess
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true; // FIXME: most likely guess
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return "."; // FIXME: most likely guess
    }

    // [end]


    // --- FUNCTIONS ---
    // [start]

    @Override
    public String getNumericFunctions() throws SQLException {
        return ""; // FIXME: currently not supported
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return ""; // FIXME: currently not supported
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return ""; // FIXME: currently not supported
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return ""; // FIXME: currently not supported
    }

    // [end]


    // --- STRUCTURE ---
    // [start]

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        ImmutableList<String> schemas = connection.getDatabaseProvider().getSchemas(connection.getMiniSession());
        StringTranslator stringTranslator = StringTranslator.utf8Instance();
        NullTranslator nullTranslator = NullTranslator.instance();
        MiniColumnHeader schemaHeader = new StoredColumnHeader("TABLE_SCHEM", false, stringTranslator.definition());
        MiniColumnHeader catalogHeader = new StoredColumnHeader("TABLE_CATALOG", false, nullTranslator.definition());
        ImmutableList<ImmutableList<MiniValue>> rows = schemas.map(
                s -> ImmutableList.of(stringTranslator.encodeFully(s), nullTranslator.encodeFully(null)));
        StoredResultSetData data = new StoredResultSetData(ImmutableList.of(schemaHeader, catalogHeader), rows);
        return new MiniJdbcResultSet(null, new StoredResultSet(data)); // NOSONAR: not closing is OK
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getTables(
            String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        // FIXME: most common types
        StringTranslator stringTranslator = StringTranslator.utf8Instance();
        MiniColumnHeader header = new StoredColumnHeader("TABLE_TYPE", false, stringTranslator.definition());
        ImmutableList<ImmutableList<MiniValue>> rows = ImmutableList.of(
                ImmutableList.of(stringTranslator.encodeFully("TABLE")),
                ImmutableList.of(stringTranslator.encodeFully("VIEW")),
                ImmutableList.of(stringTranslator.encodeFully("SYSTEM TABLE")));
        StoredResultSetData data = new StoredResultSetData(ImmutableList.of(header), rows);
        return new MiniJdbcResultSet(null, new StoredResultSet(data)); // NOSONAR: not closing is OK
    }

    @Override
    public ResultSet getColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getColumnPrivileges(
            String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getTablePrivileges(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getBestRowIdentifier(
            String catalog, String schema, String table, int scope, boolean nullable
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getCrossReference(
            String parentCatalog,
            String parentSchema,
            String parentTable,
            String foreignCatalog,
            String foreignSchema,
            String foreignTable
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getIndexInfo(
            String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getUDTs(
            String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getSuperTypes(
            String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getSuperTables(
            String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getAttributes(
            String catalog,
            String schemaPattern,
            String typeNamePattern,
            String attributeNamePattern
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getProcedures(
            String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getProcedureColumns(
            String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getFunctions(
            String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }

    @Override
    public ResultSet getPseudoColumns(
            String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern
            ) throws SQLException {
        return new MiniJdbcResultSet(null, new StoredResultSet()); // FIXME: currently not supported
    }
    
    // [end]
    
}
