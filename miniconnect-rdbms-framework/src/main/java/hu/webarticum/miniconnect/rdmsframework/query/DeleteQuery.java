package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DeleteQuery implements Query {
    
    private final String tableName;
    
    private final LinkedHashMap<String, Object> where;
    
    
    private DeleteQuery(DeleteQueryBuilder builder) {
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.where = Objects.requireNonNull(builder.where);
    }
    
    public static DeleteQueryBuilder builder() {
        return new DeleteQueryBuilder();
    }
    
    
    public String tableName() {
        return tableName;
    }

    public Map<String, Object> where() {
        return new LinkedHashMap<>(where);
    }
    
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("DELETE FROM ");
        resultBuilder.append(SqlUtil.quoteIdentifier(tableName));
        appendWhereSql(resultBuilder);
        return resultBuilder.toString();
    }
    
    private void appendWhereSql(StringBuilder sqlBuilder) {
        if (where.isEmpty()) {
            return;
        }
        
        sqlBuilder.append(" WHERE");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : where.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(" AND");
            }
            sqlBuilder.append(' ');
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
            sqlBuilder.append('=');
            sqlBuilder.append(stringifyValue(value));
        }
    }
    
    private String stringifyValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if (value instanceof String) {
            return SqlUtil.quoteString((String) value);
        } else {
            throw new IllegalArgumentException(
                    "Unknown type to stringify: " + value.getClass().getName());
        }
    }

    
    public static final class DeleteQueryBuilder {
        
        private String tableName = null;
        
        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        
        
        public DeleteQueryBuilder from(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public DeleteQueryBuilder where(Map<String, Object> where) {
            this.where = new LinkedHashMap<>(where);
            return this;
        }

        
        public DeleteQuery build() {
            return new DeleteQuery(this);
        }
        
    }
    
}
