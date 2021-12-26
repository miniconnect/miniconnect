package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SimpleSelectQuery.SimpleSelectQueryBuilder select() {
        return new SimpleSelectQuery.SimpleSelectQueryBuilder();
    }
    
}
