package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

public class SelectVariableQuery implements Query {

    private final String name;

    private final String alias;
    
    
    private SelectVariableQuery(SelectVariableQueryBuilder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.alias = builder.alias;
    }
    
    public static SelectVariableQueryBuilder builder() {
        return new SelectVariableQueryBuilder();
    }
    
    
    public String name() {
        return name;
    }

    public String alias() {
        return alias;
    }

    
    public static final class SelectVariableQueryBuilder {

        private String name = null;

        private String alias = null;

        
        private SelectVariableQueryBuilder() {
            // use builder()
        }
        

        public SelectVariableQueryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SelectVariableQueryBuilder alias(String alias) {
            this.alias = alias;
            return this;
        }

        
        public SelectVariableQuery build() {
            return new SelectVariableQuery(this);
        }
        
    }
    
}
