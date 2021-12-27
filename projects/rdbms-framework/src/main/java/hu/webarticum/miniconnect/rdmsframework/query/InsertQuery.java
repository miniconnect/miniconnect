package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

public final class InsertQuery implements Query {
    
    private final String tableName;
    
    // TODO: fields, values
    
    
    private InsertQuery(InsertQueryBuilder builder) {
        this.tableName = Objects.requireNonNull(builder.tableName);
        // TODO: fields, values
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
        // TODO
        return resultBuilder.toString();
    }
    
    // TODO
    
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

    
    public static final class InsertQueryBuilder {
        
        private String tableName = null;
        
        
        public InsertQueryBuilder into(String tableName) {
            this.tableName = tableName;
            return this;
        }

        // TODO: fields, values
        
        
        public InsertQuery build() {
            return new InsertQuery(this);
        }
        
    }
    
}
