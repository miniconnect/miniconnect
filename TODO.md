# MiniConnect TODOs

## Interproject changes

- create custom github organization for `holodb` related projects
- move MiniBase to its own repository
- move `lang` and `api` to their common repository (for independent versioning)
- 


## Necessary adjustments

- improve connect (send initialization data, specifiy initial schema, handle fail etc.)
- decide: replace all isNull() with isPresent()? (or something else?)
- decide: add error info to session init (and close?) responses?


## Must-to-have features

- improve and extend tests
- add authentication/authorization support to connections


## Nice-to-have features

- generate this README.md (substitute dynamic parts)
- decide: add support for ping request and broadcast response?
- support for other programming languages (especially: c++, js, php, python)
- add TLS support
- add a proxy/load-balancer


## MiniBase

- fix/improve `TableIndex` interface (especially null-handling)
- add (auto-generated) `INFORMATION_SCHEMA`
- add a query plan system
- implement full standard compliant (decide: SQL:2003?) SELECT query
- add limited support for create/update/delete
- implement proper transaction management
