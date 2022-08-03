package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

public class SetVariableQuery implements Query {
    
    private final String name;
    
    private final Object value;
    
    
    private SetVariableQuery(SetVariableQueryBuilder builder) {
        this.name = Objects.requireNonNull(builder.name);
        this.value = builder.value;
    }
    
    public static SetVariableQueryBuilder builder() {
        return new SetVariableQueryBuilder();
    }
    
    
    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }
    
    
    public static final class SetVariableQueryBuilder {

        private String name = null;

        private Object value = null;

        
        private SetVariableQueryBuilder() {
            // use builder()
        }
        

        public SetVariableQueryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SetVariableQueryBuilder value(Object value) {
            this.value = value;
            return this;
        }

        
        public SetVariableQuery build() {
            return new SetVariableQuery(this);
        }
        
    }
    
}
