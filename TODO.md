# TODO


## TODOs for components


### DB framework

#### MVP

- outer interface: server / session (etc.)
- minimalistic StorageAccess implementation
- minimal api for build / fill up a database

#### Later

- ...


### Parser and execution

#### MVP

- support for simplified queries in a minimalistic way
- ensure that the minimal crud is working with hibernate

#### Then

- transaction managment
- query execution plans
- reimplement execution of simplified queries
- add support for a single left join

#### Later

- support for complex queries


### Encoding

#### MVP

- support for int and string only
- default functional (decorable) encoder and decoder

#### Then

- support for all standard sql types
- support for custom types (interface)
- fallback mechanism (alien objects)
- managable encoder/decoder pair implementations
- user friendly wrappers (RowDecoder, Record)


### HoloDB

#### MVP

- standard stack: value-set / monotonic / permutation
- support for filling columns
- support for single column indexes
- init mechanism, minimalistic config file

#### Then

- robust framework for filling columns and (possibly multicolumn) indexes
- some dynamic value sets (restricted numbers, random formatted strings, generated images etc.)
- proper solution for handling null values
- improved init mechanism, feature rich config file
- instant docker image

### Later

- aspects, profiles
- advanced monotonic and permutation implementations (needs more research)
- improved dynamic value sets (reverse regex, advanced images, binary etc.)
- a repo of useful value sets in multiple languages (names, countries, fruits etc.)


### Transfer

#### MVP

Not essential

#### Then

- modules: transfer-commons / client / server
- encoding / decoding of messages
- simple client with a single socket connection
- simple server with a session manager

#### Later

- multi-socket communication
- robust, fault tolerant behavior
- middleware, router, load balancer
- support for encryption (tls?)


### Other tools and support

#### MVP

Just make it working in java

#### Later

- ...


## Some helpful info for the implementation

## Standard data types

TODO: add direct support for known SQL data types

| Java                   | JDBC                 |
|:-----------------------|:---------------------|
| `boolean`              | `BIT`                |
| `java.lang.Boolean`    | `BIT`                |
| `byte`                 | `TINYINT`            |
| `java.lang.Byte`       | `TINYINT`            |
| `double`               | `DOUBLE`             |
| `java.lang.Double`     | `DOUBLE`             |
| `float`                | `REAL`               |
| `java.lang.Float`      | `REAL`               |
| `int`                  | `INTEGER`            |
| `java.lang.Integer`    | `INTEGER`            |
| `long`                 | `BIGINT`             |
| `java.lang.Long`       | `BIGINT`             |
| `short`                | `SMALLINT`           |
| `java.lang.Short`      | `SMALLINT`           |
| `java.math.BigDecimal` | `DECIMAL`            |
| `java.math.BigInteger` | `DECIMAL`            |
| `char`                 | `CHAR`               |
| `java.lang.Character`  | `CHAR`               |
| `java.lang.String`     | `VARCHAR`/ `CLOB`    |
| `Serializable`         | `BLOB`               |
| `byte[]`               | `BLOB`               |
| `java.util.Date`       | `TIMESTAMP` (`DATE`) |
| `java.sql.Date`        | `DATE`               |
| `java.sql.Time`        | `TIME`               |
| `java.sql.Timestamp`   | `TIMESTAMP`          |

More info:

https://docs.oracle.com/cd/E19830-01/819-4721/beajw/index.html
