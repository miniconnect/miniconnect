package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

public final class SimpleSelectQuery implements Query {

    private final String fromTableName;
    
    
    private SimpleSelectQuery(SimpleSelectQueryBuilder builder) {
        this.fromTableName = Objects.requireNonNull(builder.fromTableName);
        
        // TODO
        
    }
    
    public static SimpleSelectQueryBuilder builder() {
        return new SimpleSelectQueryBuilder();
    }
    
    
    // FIXME: Query::toSqlString() ?
    @Override
    public String toString() {
        // TODO
        return "SELECT * FROM " + SqlUtil.quoteIdentifier(fromTableName);
    }
    
    
    public static final class SimpleSelectQueryBuilder {
        
        private String fromTableName = null;
        
        public SimpleSelectQueryBuilder from(String fromTableName) {
            this.fromTableName = fromTableName;
            return this;
        }
        
        public SimpleSelectQuery build() {
            return new SimpleSelectQuery(this);
        }
        
    }
    
}
