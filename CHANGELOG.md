# Changelog

## Version 0.4.0

Released on *2023-01-11*

###Removed:

- `api` and `lang` were moved to the new `miniconnect-api` repo
- `repl` were moved to the new `miniconnect-client` repo
- `rdbms-framework` were moved to the new `minibase` repo

### Improved:

- `BigInteger` was replaced by `LargeInteger` anywhere
- Unified and improved error messages
- Improved gradle build
- Some incorrect unit tests were fixed

## Version 0.3.0

Released on *2022-12-04*

### Added:

- Improved REPL with colored output
- Unique constraint
- Enum columns
- LEFT OUTER join and INNER join
- REPLACE INTO query
- Scoped wildcard syntax in SQL
- Simple COUNT queries
- NULLS FIRST and NULLS LAST clauses in SQL
- Support for unicode letters in unquoted identifiers

### Improved:

- Improved gradle build
- More improvements in SQL execution

### Fixed:

- Ensure full java8 compatibility

## Version 0.2.0

Released on *2022-08-10*

### Added:

- AUTO_INCREMENT columns
- User variables in SQL
- Storing LOBs in user variables
- Blanket adapters (e.g. for JDBC)

### Improved:

- MiniConnect API
- JDBC/Hibernate support
- Messaging
- SQL grammar
- NULL handling in SQL
- `lang` project

### Fixed:

- Prevented freezing by introducing timeouts
- Fixed LOB response handling
- Fixed some problems with NULL values in SQLs
- Prevented NPE on JDBC statement close

## Version 0.1.0

Released on *2022-04-19*

### Added:

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
