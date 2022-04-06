package hu.webarticum.miniconnect.rdmsframework.query;

public class ShowTablesQuery implements Query {

    private final String from;

    private final String like;
    
    
    private ShowTablesQuery(ShowTablesQueryBuilder builder) {
        this.from = builder.from;
        this.like = builder.like;
    }
    
    public static ShowTablesQueryBuilder builder() {
        return new ShowTablesQueryBuilder();
    }
    

    public String from() {
        return from;
    }

    public String like() {
        return like;
    }

    
    public static final class ShowTablesQueryBuilder {
        
        private String from = null;
        
        private String like = null;

        
        private ShowTablesQueryBuilder() {
            // use builder()
        }
        

        public ShowTablesQueryBuilder from(String from) {
            this.from = from;
            return this;
        }

        public ShowTablesQueryBuilder like(String like) {
            this.like = like;
            return this;
        }

        
        public ShowTablesQuery build() {
            return new ShowTablesQuery(this);
        }
        
    }
        
}
