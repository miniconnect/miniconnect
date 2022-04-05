package hu.webarticum.miniconnect.rdmsframework.query;

public interface Queries {

    public static SelectQuery.SelectQueryBuilder select() {
        return new SelectQuery.SelectQueryBuilder();
    }

    public static InsertQuery.InsertQueryBuilder insert() {
        return new InsertQuery.InsertQueryBuilder();
    }

    public static UpdateQuery.UpdateQueryBuilder update() {
        return new UpdateQuery.UpdateQueryBuilder();
    }

    public static DeleteQuery.DeleteQueryBuilder delete() {
        return new DeleteQuery.DeleteQueryBuilder();
    }

    public static ShowSchemasQuery.ShowSchemasQueryBuilder showSchemas() {
        return new ShowSchemasQuery.ShowSchemasQueryBuilder();
    }

    public static ShowTablesQuery.ShowTablesQueryBuilder showTables() {
        return new ShowTablesQuery.ShowTablesQueryBuilder();
    }

}
