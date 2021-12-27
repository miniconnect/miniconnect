package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SelectQuery.SelectQueryBuilder select() {
        return new SelectQuery.SelectQueryBuilder();
    }
    
}
