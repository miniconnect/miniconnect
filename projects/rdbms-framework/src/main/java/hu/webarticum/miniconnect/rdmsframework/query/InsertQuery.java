package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ImmutableList;

public final class InsertQuery implements Query {
    
    private final String tableName;
    
    private final ImmutableList<String> fields;
    
    private final ImmutableList<Object> values;
    
    
    private InsertQuery(InsertQueryBuilder builder) {
        if (
                builder.fields != null &&
                builder.values != null &&
                builder.fields.size() != builder.values.size()) {
            throw new IllegalArgumentException("Size of fields and values must be the same");
        }
        
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.fields = builder.fields;
        this.values = Objects.requireNonNull(builder.values);
    }
    
    public static InsertQueryBuilder builder() {
        return new InsertQueryBuilder();
    }
    
    
    public String tableName() {
        return tableName;
    }

    // TODO: fields, values
    
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("INSERT INTO ");
        resultBuilder.append(SqlUtil.quoteIdentifier(tableName));
        appendFieldsSql(resultBuilder);
        appendValuesSql(resultBuilder);
        return resultBuilder.toString();
    }
    
    private void appendFieldsSql(StringBuilder sqlBuilder) {
        if (fields == null) {
            return;
        }

        sqlBuilder.append(" (");
        
        boolean first = true;
        for (String fieldName : fields) {
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
        for (Object value : values) {
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
        
        private String tableName = null;
        
        private ImmutableList<String> fields = null;
        
        private ImmutableList<Object> values = null;

        
        private InsertQueryBuilder() {
            // use builder()
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
        
        
        public InsertQuery build() {
            return new InsertQuery(this);
        }
        
    }
    
}
