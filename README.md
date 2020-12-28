# miniConnect

Minimalistic DB connector framework and JDBC bridge 

## TODO:

- Finalize api design
- Design and implement the client-server protocol
- - subprojects:
- - - miniconnect-protocol (with the main documentation)
- - - miniconnect-server
- - - miniconnect-client
- - later: client for other platforms (node, python, php, c++ etc.)
- Create dummy implementation
- - First create a minimalistic version for high-level testing
- - Create a sample implementation with a handy set of query types (with in-memory arrays)
- Create a JDBC implementation

## Data types

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
