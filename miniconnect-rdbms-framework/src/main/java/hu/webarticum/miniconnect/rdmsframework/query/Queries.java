package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SelectQuery.SelectQueryBuilder select() {
        return new SelectQuery.SelectQueryBuilder();
    }

    // TODO: insert
    
    public static UpdateQuery.UpdateQueryBuilder update() {
        return new UpdateQuery.UpdateQueryBuilder();
    }

    public static DeleteQuery.DeleteQueryBuilder delete() {
        return new DeleteQuery.DeleteQueryBuilder();
    }

}
