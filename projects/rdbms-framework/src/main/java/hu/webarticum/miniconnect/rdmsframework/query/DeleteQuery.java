package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DeleteQuery implements Query {

    private final String schemaName;
    
    private final String tableName;
    
    private final LinkedHashMap<String, Object> where;
    
    
    private DeleteQuery(DeleteQueryBuilder builder) {
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.where = Objects.requireNonNull(builder.where);
    }
    
    public static DeleteQueryBuilder builder() {
        return new DeleteQueryBuilder();
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
    
    
    public static final class DeleteQueryBuilder {

        private String schemaName = null;
        
        private String tableName = null;
        
        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();

        
        private DeleteQueryBuilder() {
            // use builder()
        }
        
        
        public DeleteQueryBuilder inSchema(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

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
