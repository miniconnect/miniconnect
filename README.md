# MiniConnect

Minimalistic database API and JDBC bridge.

> :construction: This project is in an incubating state.

## Overview of subprojects

It consists of several separated components:

| Subproject | Description |
| ---------- | ----------- |
| :green_circle: `api` | Minimalistic API for database access |
| :computer: `client` | Lightweight client implementation of `api` (:zzz: planned) |
| :old_key: `jdbc` | Makes any `api` session available as a JDBC connection |
| :electric_plug: `jdbc-adapter` | Makes any JDBC connection available as an `api` session |
| :envelope: `messenger` | Message definitions and messaging implementation of `api` |
| :repeat: `repl` | REPL client for `api` (:zzz: planned) |
| :building_construction: `rdbms-framework` | Framework for building database engines or drivers for `api` |
| :desktop_computer: `server` | Lightweight server implementation of `messenger` (:zzz: planned) |
| :gear: `tool` | Miscellaneous tools (like REPL etc.) |
| :truck: `transfer` | Commons for transfering messages (:zzz: planned) |
| :hammer_and_wrench: `util` | Essential interfaces (like `ImmutableList` or `ByteString`) |

And there are some related repositories:

| Repository | Description |
| ---------- | ----------- |
| [HoloDB](https://github.com/davidsusu/holodb) | Holographical database engine |
| HoloDB value sets | Useful value sets for HoloDB (:zzz: planned) |

From a user perspective, the session API is most interesting.

## Session API usage

The session API is an alternative to JDBC.
The philosophy is, that a minimalistic database access API should
do two things and nothing more:

- send SQL queries and input data to the server
- accept the results

That's exactly what MiniConnect session API provides.
Here is a minimal example:

```java
try (MiniSession session = connectionFactory.connect()) {
    MiniResult result = session.execute("SELECT label FROM data");
    for (ImmutableList<MiniValue> row : result.resultSet()) {
        String label = row.get(0).contentAccess().get().toString();
        System.out.println("label: " + label);
    }
}
```

No odd abstractions like `startTransaction()` or `setCatalog()`.
No JDBC freaks like `nativeSQL()` or `setTypeMap()`.
Just a lightweight, REPL-able SQL interpreter.

At the same time there are some cons.
The main difficulty comes with prepared queries.
Most databases support the `PREPARE FROM` SQL statement,
while some others, such as H2, just implement JDBC's `prepareStatement()`,
and have no SQL equivalent.
The best solution is to supplement the H2 driver with the ability
to interpret the `PREPARE FROM` query.

## Database engines

The simple session API make it very easy to make a connector to any custom database.

The `rdbms-framework` subproject provides a framework for implementing
a MiniConnect driver (or even a complete database engine).

There are built-in JDBC->MiniConnect and MiniConnect->JDBC bridges,
so any tool that understands JDBC (e. g. Hibernate) can use your MiniConnect driver,
and, vica versa, any JDBC connection can be used via MiniConnect.

Using a MiniConnect session via JDBC:

```java
Connection connection = new MiniJdbcConnection(miniSession, provider);
```

Using a JDBC connection via MiniConnect:

```java
MiniSession session = new JdbcAdapterSession(connection, largeDataPutter);
```

## HoloDB

HoloDB is a storage engine for the `rdbms-framework`,
which introduces the theory of holographic databases.

It provides an arbitrarily large database filled with constrained random data.
Parameters and constraints can be specified in a configuration file.
Initialization ("filling" with data) of the tables is a no-op.
Query results are calculated on-the-fly.
Value providers are encouraged to calculate any single field of a column
practically in `O(1)`, but at most in `O(log(tableSize))` time.

As initialization is a no-op, it's particularly suitable for testing
and, in the case of a read-only database,
flexible orchestration, replication like a static content.

HoloDB can be found in its own repository:

https://github.com/davidsusu/holodb
