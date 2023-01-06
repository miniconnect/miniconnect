# MiniConnect

Minimalistic database API and JDBC bridge.

## Overview of sub-projects

MiniConnect consists of several separated components:

| Subproject | Description |
| ---------- | ----------- |
| :minidisc: `impl` | Obvious implementations of some api pieces |
| :old_key: `jdbc` | JDBC driver backed by MiniConnect |
| :electric_plug: `jdbc-adapter` | MiniConnect implementation backed by JDBC |
| :envelope: `messenger` | Default solution for messaging with message definitions |
| :fast_forward: `record` | Easy-to-use wrapper for result sets |
| :postbox: `rest` | Simple REST service for MiniConnect |
| :desktop_computer: `server` | Lightweight and transparent MiniConnect server and client |
| :truck: `transfer` | Simple networking framework |
| :hammer_and_wrench: `util` | Common utilities |

These gradle sub-projects can be found in the projects directory.

## Getting started with the API

[MiniConnect API](https://github.com/miniconnect/miniconnect-api) is in its own repository.

The biggest advantage of API decoupling is that
it makes it very easy to create transparent components
(proxies, loggers, mocks, and other wrappers),
all that's needed is a fairly stable dependency.

The session API is an alternative to JDBC.
The philosophy is, that a minimalistic database access API should
do two things and nothing more:

- send SQL queries and input data to the server
- accept the results

That's exactly what MiniConnect session API provides.
No odd abstractions like `startTransaction()` or `setCatalog()`.
No JDBC freaks like `nativeSQL()` or `setTypeMap()`.
Just a lightweight, REPL-able SQL interpreter.

Here is a minimal example:

```java
try (MiniSession session = connectionFactory.connect()) {
    MiniResult result = session.execute("SELECT name FROM employees");
    try (MiniResultSet resultSet = result.resultSet()) {
	    ImmutableList<MiniValue> row;
	    while ((row = resultSet.fetch()) != null) {
	        String name = row.get(0).contentAccess().get().toString();
	        System.out.println("name: " + name);
	    }
    }
}
```

To tell the truth, in practice there is a third one:

- sending large data in an efficient way

For this the `putLargeData()` method can be used:

```java
// ...

session.putLargeData("mylargedata", 20000L, myDataInputStream);

// now, your large data is stored in the @mylargedata SQL variable
```

## Friendly result sets with the `record` project

The `record` project provides a higher level easy-to-use wrapper over `MiniResultSet`.
`ResultTable` is iterable, can convert values, etc.

This is the `ResultTable` version of the example above, extended with retrieving the id as an integer:

```java
try (MiniSession session = connectionFactory.connect()) {
    MiniResult result = session.execute("SELECT id, name FROM employees");
    try (MiniResultSet resultSet = result.resultSet()) {
        for (ResultRecord row : new ResultTable(resultSet)) {
            int id = row.get(0).as(Integer.class);
            String name = row.get(1).as(String.class);
            System.out.println("id: " + id + ", name: " + name);
        }
    }
}
```

## No prepared statements?

Following the logic of the above approach, on the client side, there is no place for a `prepare()` method in the API.
If you want to take advantage of the performance and security gains of prepared queries,
you should do so via the `PREPARE FROM` query.
There's no point in sparing even the parsing of an `EXECUTE` query.

In the `jdbc` project there are some helper `PreparedStatement` providers
for RDBMS backends that do not support `PREPARE FROM` queries.
One of these implementations emulates the execution of prepared queries by using user variables (default)
and another by manipulating the query.
Alternatively, as a last resort, you can easily implement `PREPARE FROM` and `EXECUTE` on the server side.

## Database engines

The simple session API makes it easy to make a connector to any custom database.

The `rdbms-framework` sub-project provides a framework for implementing
a MiniConnect driver (or even a complete database engine).

[HoloDB](https://github.com/miniconnect/holodb) is a storage engine for `rdbms-framework`.
It requires only a configuration file and provides an arbitrarily large database filled with random data.

## JDBC compatibility

*Note: one of the major goals of MiniConnect is to relieve the pains of JDBC users and implementors.*
**

There are built-in JDBC->MiniConnect and MiniConnect->JDBC bridges,
so any tool that understands JDBC (e. g. Hibernate) can use your MiniConnect driver,
and vice versa: any JDBC connection can be used via MiniConnect.

Using a MiniConnect session via JDBC:

```java
Connection connection = new MiniJdbcConnection(miniSession, provider);
```

Using a JDBC connection via MiniConnect:

```java
MiniSession session = new JdbcAdapterSession(connection, largeDataPutter);
```

The `jdbc` project provides an SPI service implementation for `java.sql.Driver`.
You can use the following JDBC URL syntax to connect a MiniConnect server:

```
jdbc:miniconnect://<host>:<port>/[<schema>[?<key1>=<value1>[&<keyN>=<valueN>]+]]
```

For example:

```
jdbc:miniconnect://localhost:3430/economy
```

The default port is `3430`.

Also, there is an SPI service implementation for Hibernate's `org.hibernate.boot.spi.MetadataBuilderInitializer`.
So any MiniConnect server or session factory can be used with Hibernate without complicated configuration.

## Changelog

See [CHANGELOG.md](CHANGELOG.md).
