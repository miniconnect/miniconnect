package hu.webarticum.miniconnect.rdmsframework.query;

public class SpecialSelectQuery implements Query {

    private final SpecialSelectQueryType queryType;
    
    
    private SpecialSelectQuery(SpecialSelectQueryBuilder builder) {
        this.queryType = builder.queryType;
    }
    
    public static SpecialSelectQueryBuilder builder() {
        return new SpecialSelectQueryBuilder();
    }
    
    
    public SpecialSelectQueryType queryType() {
        return queryType;
    }

    
    public static final class SpecialSelectQueryBuilder {
        
        private SpecialSelectQueryType queryType = null;

        
        private SpecialSelectQueryBuilder() {
            // use builder()
        }
        

        public SpecialSelectQueryBuilder queryType(SpecialSelectQueryType queryType) {
            this.queryType = queryType;
            return this;
        }

        
        public SpecialSelectQuery build() {
            return new SpecialSelectQuery(this);
        }
        
    }
        
}
