package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

public class SelectSpecialQuery implements Query {

    private final SpecialSelectableType queryType;

    private final String alias;
    
    
    private SelectSpecialQuery(SelectSpecialQueryBuilder builder) {
        this.queryType = Objects.requireNonNull(builder.queryType);
        this.alias = builder.alias;
    }
    
    public static SelectSpecialQueryBuilder builder() {
        return new SelectSpecialQueryBuilder();
    }
    
    
    public SpecialSelectableType queryType() {
        return queryType;
    }

    public String alias() {
        return alias;
    }

    
    public static final class SelectSpecialQueryBuilder {
        
        private SpecialSelectableType queryType = null;
        
        private String alias = null;

        
        private SelectSpecialQueryBuilder() {
            // use builder()
        }
        

        public SelectSpecialQueryBuilder queryType(SpecialSelectableType queryType) {
            this.queryType = queryType;
            return this;
        }

        public SelectSpecialQueryBuilder alias(String alias) {
            this.alias = alias;
            return this;
        }

        
        public SelectSpecialQuery build() {
            return new SelectSpecialQuery(this);
        }
        
    }
        
}
