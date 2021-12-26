package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public final class SimpleSelectQuery implements Query {
    
    private final ImmutableList<String> fields;
    
    private final ImmutableList<String> aliases;

    private final String fromTableName;
    
    
    private SimpleSelectQuery(SimpleSelectQueryBuilder builder) {
        ImmutableList<String> aliases = builder.fields;
        if (builder.fields != null) {
            if (builder.fields.isEmpty()) {
                throw new IllegalArgumentException("At least one field is required");
            }
            if (builder.aliases != null) {
                if (builder.aliases.size() != builder.fields.size()) {
                    throw new IllegalArgumentException("Aliases and fields must have the same size");
                }
                aliases = builder.aliases;
            }
        }

        this.fields = builder.fields;
        this.aliases = aliases;
        this.fromTableName = Objects.requireNonNull(builder.fromTableName);
        
        // TODO
        
    }
    
    public static SimpleSelectQueryBuilder builder() {
        return new SimpleSelectQueryBuilder();
    }
    
    
    public String fromTableName() {
        return fromTableName;
    }
    
    
    // FIXME: Query::toSqlString() ?
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("SELECT");
        appendFieldsSql(resultBuilder);
        resultBuilder.append(" FROM ");
        resultBuilder.append(SqlUtil.quoteIdentifier(fromTableName));

        // TODO: where
        // TODO: orderBy
        
        return resultBuilder.toString();
    }
    
    private void appendFieldsSql(StringBuilder sqlBuilder) {
        if (fields == null) {
            sqlBuilder.append(" *");
            return;
        }
        
        boolean first = true;
        int size = fields.size();
        for (int i = 0; i < size; i++) {
            String fieldName = fields.get(i);
            String alias = aliases.get(i);
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(',');
            }
            sqlBuilder.append(' ');
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
            if (!alias.equals(fieldName)) {
                sqlBuilder.append(" AS ");
                sqlBuilder.append(SqlUtil.quoteIdentifier(alias));
            }
        }
    }
    
    
    public static final class SimpleSelectQueryBuilder {
        
        private ImmutableList<String> fields = null;
        
        private ImmutableList<String> aliases = null;
        
        private String fromTableName = null;
        
        
        public SimpleSelectQueryBuilder fields(ImmutableList<String> fields) {
            this.fields = fields;
            return this;
        }

        public SimpleSelectQueryBuilder aliases(ImmutableList<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public SimpleSelectQueryBuilder from(String fromTableName) {
            this.fromTableName = fromTableName;
            return this;
        }
        
        
        public SimpleSelectQuery build() {
            return new SimpleSelectQuery(this);
        }
        
    }
    
}
