package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import hu.webarticum.miniconnect.rdmsframework.util.SqlUtil;

public final class SelectQuery implements Query {
    
    private final LinkedHashMap<String, String> fields;

    private final String schemaName;

    private final String tableName;
    
    private final LinkedHashMap<String, Object> where;
    
    private final LinkedHashMap<String, Boolean> orderBy;

    private final Integer limit;
    
    
    private SelectQuery(SelectQueryBuilder builder) {
        this.fields = new LinkedHashMap<>(Objects.requireNonNull(builder.fields));
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.where = new LinkedHashMap<>(Objects.requireNonNull(builder.where));
        this.orderBy = new LinkedHashMap<>(Objects.requireNonNull(builder.orderBy));
        this.limit = builder.limit;
    }
    
    public static SelectQueryBuilder builder() {
        return new SelectQueryBuilder();
    }
    

    public Map<String, String> fields() {
        return new LinkedHashMap<>(fields);
    }

    public String schemaName() {
        return schemaName;
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

    public Integer limit() {
        return limit;
    }
    
    
    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder("SELECT");
        appendFieldsSql(resultBuilder);
        resultBuilder.append(" FROM ");
        if (schemaName != null) {
            resultBuilder.append(SqlUtil.quoteIdentifier(schemaName));
            resultBuilder.append('.');
        }
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
            sqlBuilder.append(SqlUtil.stringifyValue(value));
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

    
    public static final class SelectQueryBuilder {
        
        private LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        private String schemaName = null;

        private String tableName = null;
        
        private LinkedHashMap<String, Object> where = new LinkedHashMap<>();
        
        private LinkedHashMap<String, Boolean> orderBy = new LinkedHashMap<>();

        private Integer limit = null;
        
        
        private SelectQueryBuilder() {
            // use builder()
        }
        
        
        public SelectQueryBuilder fields(Map<String, String> fields) {
            this.fields = new LinkedHashMap<>(fields);
            return this;
        }

        public SelectQueryBuilder inSchema(String schemaName) {
            this.schemaName = schemaName;
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

        public SelectQueryBuilder limit(Integer limit) {
            this.limit = limit;
            return this;
        }
        
        
        public SelectQuery build() {
            return new SelectQuery(this);
        }
        
    }
    
}
