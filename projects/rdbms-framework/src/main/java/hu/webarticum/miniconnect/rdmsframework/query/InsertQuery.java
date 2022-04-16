package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import hu.webarticum.miniconnect.lang.ImmutableList;

public final class InsertQuery implements Query {

    private final String schemaName;

    private final String tableName;
    
    private final LinkedHashMap<String, Object> values;
    
    
    private InsertQuery(InsertQueryBuilder builder) {
        Objects.requireNonNull(builder.fields);
        Objects.requireNonNull(builder.values);
        
        int valueCount = builder.fields.size();
        if (builder.values.size() != valueCount) {
            throw new IllegalArgumentException("Count of fields and values must be the same");
        }
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        
        values = new LinkedHashMap<>(valueCount);
        for (int i = 0; i < valueCount; i++) {
            String fieldName = builder.fields.get(i);
            Object value = builder.values.get(i);
            values.put(fieldName, value);
        }
    }
    
    public static InsertQueryBuilder builder() {
        return new InsertQueryBuilder();
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

    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("INSERT INTO ");
        resultBuilder.append(SqlUtil.quoteIdentifier(tableName));
        appendFieldsSql(resultBuilder);
        appendValuesSql(resultBuilder);
        return resultBuilder.toString();
    }
    
    private void appendFieldsSql(StringBuilder sqlBuilder) {
        sqlBuilder.append(" (");
        
        boolean first = true;
        for (String fieldName : values.keySet()) {
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
        }
        
        sqlBuilder.append(")");
    }

    private void appendValuesSql(StringBuilder sqlBuilder) {
        sqlBuilder.append(" VALUES (");
        
        boolean first = true;
        for (Object value : values.values()) {
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(SqlUtil.stringifyValue(value));
        }
        
        sqlBuilder.append(")");
    }

    
    public static final class InsertQueryBuilder {
        
        private String schemaName = null;
        
        private String tableName = null;
        
        private ImmutableList<String> fields = null;
        
        private ImmutableList<Object> values = null;

        
        private InsertQueryBuilder() {
            // use builder()
        }
        

        public InsertQueryBuilder inSchema(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public InsertQueryBuilder into(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public InsertQueryBuilder fields(ImmutableList<String> fields) {
            this.fields = fields;
            return this;
        }

        public InsertQueryBuilder values(ImmutableList<Object> values) {
            this.values = values;
            return this;
        }

        public InsertQueryBuilder set(Map<String, Object> values) {
            this.fields = ImmutableList.fromCollection(values.keySet());
            this.values = ImmutableList.fromCollection(values.values());
            return this;
        }
        
        
        public InsertQuery build() {
            return new InsertQuery(this);
        }
        
    }
    
}
