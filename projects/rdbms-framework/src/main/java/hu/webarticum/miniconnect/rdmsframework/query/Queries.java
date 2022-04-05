package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SelectQuery.SelectQueryBuilder select() {
        return SelectQuery.builder();
    }

    public static InsertQuery.InsertQueryBuilder insert() {
        return InsertQuery.builder();
    }

    public static UpdateQuery.UpdateQueryBuilder update() {
        return UpdateQuery.builder();
    }

    public static DeleteQuery.DeleteQueryBuilder delete() {
        return DeleteQuery.builder();
    }

    public static ShowSchemasQuery.ShowSchemasQueryBuilder showSchemas() {
        return ShowSchemasQuery.builder();
    }

    public static ShowTablesQuery.ShowTablesQueryBuilder showTables() {
        return ShowTablesQuery.builder();
    }

}
