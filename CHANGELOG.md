# Changelog

## Version 0.2.0

Released on *2022-08-10*

**Added:**

- AUTO_INCREMENT columns
- User variables in SQL
- Storing LOBs in user variables
- Blanket adapters (e.g. for JDBC)

**Improved:**

- MiniConnect API
- JDBC/Hibernate support
- Messaging
- SQL grammar
- NULL handling in SQL
- `lang` project

**Fixed:**

- Prevented freezing by introducing timeouts
- Fixed LOB response handling
- Fixed some problems with NULL values in SQLs
- Prevented NPE on JDBC statement close

## Version 0.1.0

Released on *2022-04-19*

**Added:**

- MiniConnect API (minimalistic)
- User-friendly result set wrapper
- Client-server mode, messaging
- Embedded mode
- RDBMS framework
- JDBC driver (access via JDBC)
- JDBC wrapper (access any JDBC connection via MiniConnect API)
- REPL
- Automatic REST
- and more &hellip;
