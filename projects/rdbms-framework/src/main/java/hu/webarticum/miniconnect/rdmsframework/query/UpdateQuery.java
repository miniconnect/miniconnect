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
