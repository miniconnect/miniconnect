package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class UpdateQuery implements Query {
    
    private final String schemaName;
    
    private final String tableName;

    private final LinkedHashMap<String, Object> values;
    
    private final LinkedHashMap<String, Object> where;
    
    
    private UpdateQuery(UpdateQueryBuilder builder) {
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.values = Objects.requireNonNull(builder.values);
        this.where = Objects.requireNonNull(builder.where);
    }
    
    public static UpdateQueryBuilder builder() {
        return new UpdateQueryBuilder();
    }
    

    public String schemaName() {
        return schemaName;
    }

    public String tableName() {
        return tableName;
    }

    public Map<String, Object> values() {
        return new LinkedHashMap<>(values);
    }
    
    public Map<String, Object> where() {
        return new LinkedHashMap<>(where);
    }
    
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("UPDATE ");
        resultBuilder.append(SqlUtil.quoteIdentifier(tableName));
        appendSetSql(resultBuilder);
        appendWhereSql(resultBuilder);
        return resultBuilder.toString();
    }
    
    private void appendSetSql(StringBuilder sqlBuilder) {
        sqlBuilder.append(" SET");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(',');
            }
            sqlBuilder.append(' ');
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
            sqlBuilder.append('=');
            sqlBuilder.append(SqlUtil.stringifyValue(value));
        }
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
            sqlBuilder.append(SqlUtil.stringifyValue(value));
        }
    }
    
    
    public static final class UpdateQueryBuilder {

        private String schemaName = null;
        
        private String tableName = null;

        private LinkedHashMap<String, Object> values = new LinkedHashMap<>();

        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();

        
        private UpdateQueryBuilder() {
            // use builder()
        }
        

        public UpdateQueryBuilder inSchema(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public UpdateQueryBuilder table(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public UpdateQueryBuilder set(Map<String, Object> values) {
            this.values = new LinkedHashMap<>(values);
            return this;
        }

        public UpdateQueryBuilder where(Map<String, Object> where) {
            this.where = new LinkedHashMap<>(where);
            return this;
        }
        
        
        public UpdateQuery build() {
            return new UpdateQuery(this);
        }
        
    }
    
}
