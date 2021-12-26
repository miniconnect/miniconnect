# MiniConnect

Minimalistic DB connector framework and JDBC bridge.

> :construction: This project is in an incubating state.

## Overview of subprojects

It consists of several independent components:

> TODO: table of components and their description

From a user perspective, the session API is most interesting.

## Session API usage

The session API is an alternative for JDBC.
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
Just a lightweight SQL interpreter.
(Also, to take advantage of this, there is a built-in REPL.)

At the some time there are some cons.
The main difficulty comes with prepared queries.
Most databases support the `PREPARE FROM` SQL statement,
while some others (such as H2) just implement JDBC's `prepareStatement()`,
and have no SQL equivalent.
The best solution is to supplement the H2 driver with the ability
to interpret the `PREPARE FROM` query.

## Custom database engines

The simple session API make it very easy to make a connector to any custom database.

There are built-in JDBC->MiniConnect and MiniConnect->JDBC bridges,
so any tool that understands JDBC (e. g. Hibernate) can use you MiniConnect driver,
and, vica versa, any JDBC connection can be used via MiniConnect.

The `rdbms-framework` subproject provides a framework for implementing
a MiniConnect driver (or even a complete database engine).

## HoloDB

HoloDB is a storage engine for the `rdbms-framework`,
which introduces the theory of holographic databases.

It provides an arbitrarily large database filled with constrained random data.
Parameters and constraints can be specified in a configuration file.
Initialization ("filling" with data) of the tables is a no-op.
Query results are calculated on-the-fly.
A single field of a column is calculated usually in `O(1)`, but at most in `O(log(tableSize))`.

As initialization is a no-op, it's particularly suitable for testing
and, in the case of a read-only database,
flexible orchestration, replication like a static content.

HoloDB can be found in a separated repository:

https://github.com/davidsusu/holodb
