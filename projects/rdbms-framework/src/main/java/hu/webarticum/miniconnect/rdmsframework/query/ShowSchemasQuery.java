package hu.webarticum.miniconnect.rdmsframework.query;

public class ShowSchemasQuery implements Query {

    private final String like;
    
    
    private ShowSchemasQuery(ShowSchemasQueryBuilder builder) {
        this.like = builder.like;
    }
    
    public static ShowSchemasQueryBuilder builder() {
        return new ShowSchemasQueryBuilder();
    }
    
    
    public String like() {
        return like;
    }

    
    public static final class ShowSchemasQueryBuilder {
        
        private String like = null;

        
        private ShowSchemasQueryBuilder() {
            // use builder()
        }
        

        public ShowSchemasQueryBuilder like(String like) {
            this.like = like;
            return this;
        }

        
        public ShowSchemasQuery build() {
            return new ShowSchemasQuery(this);
        }
        
    }
        
}
