package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SelectQuery.SimpleSelectQueryBuilder select() {
        return new SelectQuery.SimpleSelectQueryBuilder();
    }
    
}
