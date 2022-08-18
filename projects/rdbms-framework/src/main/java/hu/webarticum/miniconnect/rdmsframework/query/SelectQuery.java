package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.Objects;

import hu.webarticum.miniconnect.lang.ImmutableList;

public final class SelectQuery implements Query {
    
    private final ImmutableList<SelectItem> selectItems;

    private final String schemaName;

    private final String tableName;

    private final String tableAlias;
    
    private final ImmutableList<LeftJoinItem> leftJoins;
    
    private final ImmutableList<WhereItem> where;
    
    private final ImmutableList<OrderByItem> orderBy;

    private final Integer limit;
    
    
    private SelectQuery(SelectQueryBuilder builder) {
        this.selectItems = Objects.requireNonNull(builder.selectItems);
        this.schemaName = builder.schemaName;
        this.tableName = Objects.requireNonNull(builder.tableName);
        this.tableAlias = builder.tableAlias;
        this.leftJoins = Objects.requireNonNull(builder.leftJoins);
        this.where = Objects.requireNonNull(builder.where);
        this.orderBy = Objects.requireNonNull(builder.orderBy);
        this.limit = builder.limit;
    }
    
    public static SelectQueryBuilder builder() {
        return new SelectQueryBuilder();
    }
    

    public ImmutableList<SelectItem> selectItems() {
        return selectItems;
    }

    public String schemaName() {
        return schemaName;
    }

    public String tableName() {
        return tableName;
    }

    public String tableAlias() {
        return tableAlias;
    }

    public ImmutableList<LeftJoinItem> leftJoins() {
        return leftJoins;
    }
    
    public ImmutableList<WhereItem> where() {
        return where;
    }
    
    public ImmutableList<OrderByItem> orderBy() {
        return orderBy;
    }

    public Integer limit() {
        return limit;
    }
    
    
    public static final class SelectQueryBuilder {
        
        private ImmutableList<SelectItem> selectItems = ImmutableList.of(new SelectItem(null, null, null));

        private String schemaName = null;

        private String tableName = null;

        private String tableAlias = null;
        
        private ImmutableList<LeftJoinItem> leftJoins = ImmutableList.empty();
        
        private ImmutableList<WhereItem> where = ImmutableList.empty();
        
        private ImmutableList<OrderByItem> orderBy = ImmutableList.empty();

        private Integer limit = null;
        
        
        private SelectQueryBuilder() {
            // use builder()
        }
        
        
        public SelectQueryBuilder selectItems(ImmutableList<SelectItem> selectItems) {
            this.selectItems = selectItems;
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

        public SelectQueryBuilder tableAlias(String tableAlias) {
            this.tableAlias = tableAlias;
            return this;
        }

        public SelectQueryBuilder leftJoins(ImmutableList<LeftJoinItem> leftJoins) {
            this.leftJoins = leftJoins;
            return this;
        }

        public SelectQueryBuilder leftJoin(LeftJoinItem leftJoin) {
            this.leftJoins = leftJoins.append(leftJoin);
            return this;
        }

        public SelectQueryBuilder where(ImmutableList<WhereItem> where) {
            this.where = where;
            return this;
        }

        public SelectQueryBuilder orderBy(ImmutableList<OrderByItem> orderBy) {
            this.orderBy = orderBy;
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
    
    
    public static class SelectItem {
        
        private final String tableName;
        
        private final String fieldName;
        
        private final String alias;

        
        public SelectItem(String tableName, String fieldName, String alias) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.alias = alias;
        }

        
        public String tableName() {
            return tableName;
        }

        public String fieldName() {
            return fieldName;
        }

        public String alias() {
            return alias;
        }
        
    }
    
    
    public static class LeftJoinItem {

        private final String targetSchemaName;
        
        private final String targetTableName;

        private final String targetTableAlias;
        
        private final String targetFieldName;

        private final String sourceTableAlias;

        private final String sourceFieldName;

        
        public LeftJoinItem(
                String targetSchemaName,
                String targetTableName,
                String targetTableAlias,
                String targetFieldName,
                String sourceTableAlias,
                String sourceFieldName) {
            this.targetSchemaName = targetSchemaName;
            this.targetTableName = targetTableName;
            this.targetTableAlias = targetTableAlias;
            this.targetFieldName = targetFieldName;
            this.sourceTableAlias = sourceTableAlias;
            this.sourceFieldName = sourceFieldName;
        }

        
        public String targetSchemaName() {
            return targetSchemaName;
        }

        public String targetTableName() {
            return targetTableName;
        }

        public String targetTableAlias() {
            return targetTableAlias;
        }

        public String targetFieldName() {
            return targetFieldName;
        }

        public String sourceTableAlias() {
            return sourceTableAlias;
        }

        public String sourceFieldName() {
            return sourceFieldName;
        }

    }
    

    public static class WhereItem {

        private final String tableName;
        
        private final String fieldName;
        
        private final Object value;

        
        public WhereItem(String tableName, String fieldName, Object value) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.value = value;
        }

        
        public String tableName() {
            return tableName;
        }

        public String fieldName() {
            return fieldName;
        }

        public Object value() {
            return value;
        }
        
    }
    

    public static class OrderByItem {

        private final String tableName;
        
        private final String fieldName;
        
        private final Integer position;
        
        private final boolean ascOrder;

        public OrderByItem(String tableName, String fieldName, Integer position, boolean ascOrder) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.position = position;
            this.ascOrder = ascOrder;
        }

        
        public String tableName() {
            return tableName;
        }

        public String fieldName() {
            return fieldName;
        }

        public Integer position() {
            return position;
        }

        public boolean ascOrder() {
            return ascOrder;
        }
        
    }
    
}
