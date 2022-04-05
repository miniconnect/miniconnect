package hu.webarticum.miniconnect.rdmsframework.query;

public class UseQuery implements Query {

    private final String schema;
    
    
    private UseQuery(UseQueryBuilder builder) {
        this.schema = builder.schema;
    }
    
    public static UseQueryBuilder builder() {
        return new UseQueryBuilder();
    }
    
    
    public String schema() {
        return schema;
    }

    
    public static final class UseQueryBuilder {
        
        private String schema = null;

        
        private UseQueryBuilder() {
            // use builder()
        }
        

        public UseQueryBuilder schema(String schema) {
            this.schema = schema;
            return this;
        }

        
        public UseQuery build() {
            return new UseQuery(this);
        }
        
    }
        
}
