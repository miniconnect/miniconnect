package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SelectQuery implements Query {
    
    private final LinkedHashMap<String, String> fields;

    private final String tableName;
    
    private final LinkedHashMap<String, Object> where;
    
    private final LinkedHashMap<String, Boolean> orderBy;
    
    
    private SelectQuery(SelectQueryBuilder builder) {
        this.fields = builder.fields;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.where = Objects.requireNonNull(builder.where);
        this.orderBy = Objects.requireNonNull(builder.orderBy);
    }
    
    public static SelectQueryBuilder builder() {
        return new SelectQueryBuilder();
    }
    

    public Map<String, String> fields() {
        return new LinkedHashMap<>(fields);
    }

    public String tableName() {
        return tableName;
    }

    public Map<String, Object> where() {
        return new LinkedHashMap<>(where);
    }
    
    public Map<String, Boolean> orderBy() {
        return new LinkedHashMap<>(orderBy);
    }
    
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("SELECT");
        appendFieldsSql(resultBuilder);
        resultBuilder.append(" FROM ");
        resultBuilder.append(SqlUtil.quoteIdentifier(tableName));
        appendWhereSql(resultBuilder);
        appendOrderBySql(resultBuilder);
        return resultBuilder.toString();
    }
    
    private void appendFieldsSql(StringBuilder sqlBuilder) {
        if (fields.isEmpty()) {
            sqlBuilder.append(" *");
            return;
        }
        
        boolean first = true;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getValue();
            String alias = entry.getKey();
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

    private void appendWhereSql(StringBuilder sqlBuilder) {
        if (where.isEmpty()) {
            return;
        }
        
        sqlBuilder.append(" WHERE");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : where.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(" AND");
            }
            sqlBuilder.append(' ');
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
            sqlBuilder.append('=');
            sqlBuilder.append(stringifyValue(value));
        }
    }
    
    private void appendOrderBySql(StringBuilder sqlBuilder) {
        if (orderBy.isEmpty()) {
            return;
        }
        
        sqlBuilder.append(" ORDER BY");

        boolean first = true;
        for (Map.Entry<String, Boolean> entry : orderBy.entrySet()) {
            String fieldName = entry.getKey();
            boolean ascOrder = entry.getValue();
            if (first) {
                first = false;
            } else {
                sqlBuilder.append(',');
            }
            sqlBuilder.append(' ');
            sqlBuilder.append(SqlUtil.quoteIdentifier(fieldName));
            sqlBuilder.append(ascOrder ? " ASC" : " DESC");        }
    }

    private String stringifyValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if (value instanceof String) {
            return SqlUtil.quoteString((String) value);
        } else {
            throw new IllegalArgumentException(
                    "Unknown type to stringify: " + value.getClass().getName());
        }
    }

    
    public static final class SelectQueryBuilder {
        
        private LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        private String tableName = null;
        
        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        
        private LinkedHashMap<String, Boolean> orderBy = new LinkedHashMap<>();
        
        
        public SelectQueryBuilder fields(Map<String, String> fields) {
            this.fields = new LinkedHashMap<>(fields);
            return this;
        }

        public SelectQueryBuilder from(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public SelectQueryBuilder where(Map<String, Object> where) {
            this.where = new LinkedHashMap<>(where);
            return this;
        }

        public SelectQueryBuilder orderBy(Map<String, Boolean> orderBy) {
            this.orderBy = new LinkedHashMap<>(orderBy);
            return this;
        }
        
        
        public SelectQuery build() {
            return new SelectQuery(this);
        }
        
    }
    
}
