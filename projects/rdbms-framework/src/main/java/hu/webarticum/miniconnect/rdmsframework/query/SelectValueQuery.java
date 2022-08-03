package hu.webarticum.miniconnect.rdmsframework.query;

public class SelectValueQuery implements Query {

    private final Object value;

    private final String alias;
    
    
    private SelectValueQuery(SelectValueQueryBuilder builder) {
        this.value = builder.value;
        this.alias = builder.alias;
    }
    
    public static SelectValueQueryBuilder builder() {
        return new SelectValueQueryBuilder();
    }
    
    
    public Object value() {
        return value;
    }

    public String alias() {
        return alias;
    }

    
    public static final class SelectValueQueryBuilder {

        private Object value = null;

        private String alias = null;

        
        private SelectValueQueryBuilder() {
            // use builder()
        }
        

        public SelectValueQueryBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public SelectValueQueryBuilder alias(String alias) {
            this.alias = alias;
            return this;
        }

        
        public SelectValueQuery build() {
            return new SelectValueQuery(this);
        }
        
    }
    
}
