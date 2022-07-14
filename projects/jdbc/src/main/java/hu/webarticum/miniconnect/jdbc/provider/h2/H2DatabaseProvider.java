package hu.webarticum.miniconnect.jdbc.provider.h2;

import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.ParameterValue;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.record.ResultTable;

public class H2DatabaseProvider implements DatabaseProvider {

    @Override
    public boolean isReadOnly(MiniSession session) {
        String sql = "CALL READONLY()";
        try (MiniResultSet resultSet = session.execute(sql).resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            return resultTable.iterator().next().get(0).as(Boolean.class);
        }
    }

    @Override
    public void setReadOnly(MiniSession session, boolean readOnly) {
        boolean currentReadOnly = isReadOnly(session);
        if (readOnly != currentReadOnly) {
            throw new UnsupportedOperationException("Read-only mode can not be changed");
        }
    }
    
    @Override
    public String getSchema(MiniSession session) {
        String sql = "SELECT CURRENT_SCHEMA";
        try (MiniResultSet resultSet = session.execute(sql).resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            return resultTable.iterator().next().get(0).as(String.class);
        }
    }

    @Override
    public void setSchema(MiniSession session, String schemaName) {
        String sql = "USE " + quoteIdentifier(schemaName);
        session.execute(sql);
    }

    @Override
    public String getCatalog(MiniSession session) {
        String sql = "SELECT CURRENT_CATALOG";
        try (MiniResultSet resultSet = session.execute(sql).resultSet()) {
            ResultTable resultTable = new ResultTable(resultSet);
            return resultTable.iterator().next().get(0).as(String.class);
        }
    }

    @Override
    public void setCatalog(MiniSession session, String catalogName) {
        String currentCatalogName = getCatalog(session);
        if (!catalogName.equals(currentCatalogName)) {
            throw new UnsupportedOperationException("Catalog can not be changed");
        }
    }

    @Override
    public void checkSessionValid(MiniSession session) {
        getSchema(session);
    }

    @Override
    public H2PreparedStatementProvider prepareStatement(MiniSession session, String sql) {
        return new H2PreparedStatementProvider(this, session, sql);
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
    
    @Override
    public String stringifyValue(ParameterValue parameterValue) {
        Object value = parameterValue.value();
        if (value == null) {
            return "NULL";
        }
        
        return "'" + value + "'";
    }

}
