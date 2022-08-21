package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Map;
import java.util.Objects;

import hu.webarticum.miniconnect.lang.ImmutableList;

public final class InsertQuery implements Query {
    
    private final boolean replace;

    private final String schemaName;

    private final String tableName;
    
    private final ImmutableList<String> fields;
    
    private final ImmutableList<Object> values;
    
    
    private InsertQuery(InsertQueryBuilder builder) {
        Objects.requireNonNull(builder.values);
        
        if (builder.fields != null && builder.values.size() != builder.fields.size()) {
            throw new IllegalArgumentException("Count of fields and values must be the same");
        }
        this.replace = builder.replace;
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.fields = builder.fields;
        this.values = builder.values;
    }
    
    public static InsertQueryBuilder builder() {
        return new InsertQueryBuilder();
    }
    

    public boolean replace() {
        return replace;
    }

    public String schemaName() {
        return schemaName;
    }

    public String tableName() {
        return tableName;
    }
    
    public ImmutableList<String> fields() {
        return fields;
    }

    public ImmutableList<Object> values() {
        return values;
    }
    
    
    public static final class InsertQueryBuilder {
        
        private boolean replace = false;
        
        private String schemaName = null;
        
        private String tableName = null;
        
        private ImmutableList<String> fields = null;
        
        private ImmutableList<Object> values = null;

        
        private InsertQueryBuilder() {
            // use builder()
        }
        

        public InsertQueryBuilder replace(boolean replace) {
            this.replace = replace;
            return this;
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
