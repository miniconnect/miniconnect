package hu.webarticum.miniconnect.rdmsframework.query;

public class ShowTablesQuery implements Query {

    private final String like;
    
    
    private ShowTablesQuery(ShowTablesQueryBuilder builder) {
        this.like = builder.like;
    }
    
    public static ShowTablesQueryBuilder builder() {
        return new ShowTablesQueryBuilder();
    }
    
    
    public String like() {
        return like;
    }

    
    public static final class ShowTablesQueryBuilder {
        
        private String like = null;
        

        public ShowTablesQueryBuilder like(String like) {
            this.like = like;
            return this;
        }

        
        public ShowTablesQuery build() {
            return new ShowTablesQuery(this);
        }
        
    }
        
}
