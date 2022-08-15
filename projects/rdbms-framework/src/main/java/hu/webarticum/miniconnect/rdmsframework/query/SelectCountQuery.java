package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SelectCountQuery implements Query {
    
    private final String schemaName;

    private final String tableName;
    
    private final LinkedHashMap<String, Object> where;
    
    
    private SelectCountQuery(SelectCountQueryBuilder builder) {
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.where = new LinkedHashMap<>(Objects.requireNonNull(builder.where));
    }
    
    public static SelectCountQueryBuilder builder() {
        return new SelectCountQueryBuilder();
    }
    

    public String schemaName() {
        return schemaName;
    }

    public String tableName() {
        return tableName;
    }

    public Map<String, Object> where() {
        return new LinkedHashMap<>(where);
    }
    
    
    public static final class SelectCountQueryBuilder {
        
        private String schemaName = null;

        private String tableName = null;
        
        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        
        
        private SelectCountQueryBuilder() {
            // use builder()
        }
        
        
        public SelectCountQueryBuilder inSchema(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public SelectCountQueryBuilder from(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public SelectCountQueryBuilder where(Map<String, Object> where) {
            this.where = new LinkedHashMap<>(where);
            return this;
        }

        
        public SelectCountQuery build() {
            return new SelectCountQuery(this);
        }
        
    }
    
}
