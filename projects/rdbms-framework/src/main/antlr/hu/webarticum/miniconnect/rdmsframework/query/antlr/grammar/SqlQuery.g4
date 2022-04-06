grammar SqlQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

sqlQuery: (
    selectQuery |
    updateQuery |
    insertQuery |
    deleteQuery |
    showSchemasQuery |
    showTablesQuery |
    useQuery
) EOF ;

selectQuery: SELECT selectPart FROM tableName wherePart? orderByPart?;
selectPart: selectItems | '*';
selectItems: selectItem ( ',' selectItem )*;
selectItem: fieldName ( AS? alias=identifier )?;

updateQuery: UPDATE tableName updatePart wherePart?;
updatePart: SET updateItem ( ',' updateItem )*;
updateItem: fieldName '=' value;

insertQuery: INSERT INTO tableName fieldList? VALUES valueList;
fieldList: '(' fieldName ( ',' fieldName )* ')';
valueList: '(' value ( ',' value )* ')';

deleteQuery: DELETE FROM tableName wherePart?;

showSchemasQuery: SHOW ( SCHEMAS | DATABASES ) likePart?;

showTablesQuery: SHOW TABLES ( FROM schemaName ) likePart?;

useQuery: USE schemaName;

wherePart: WHERE whereItem ( AND whereItem )*;
whereItem: fieldName '=' value | '(' whereItem ')';
orderByPart: ORDER BY orderByItem ( ',' orderByItem )*;
orderByItem: fieldName ( ASC | DESC )?;
fieldName: identifier;
tableName: identifier;
identifier: SIMPLENAME | QUOTEDNAME | BACKTICKEDNAME;
value: LIT_STRING | LIT_INTEGER | NULL;
likePart: LIKE LIT_STRING;
schemaName: identifier;

SELECT: S E L E C T;
INSERT: I N S E R T;
UPDATE: U P D A T E;
DELETE: D E L E T E;
SHOW: S H O W;
USE: U S E;

AS: A S;
FROM: F R O M;
INTO: I N T O;
WHERE: W H E R E;
AND: A N D;
ORDER: O R D E R;
BY: B Y;
ASC: A S C;
DESC: D E S C;
VALUES: V A L U E S;
SET: S E T;
NULL: N U L L;
SCHEMAS: S C H E M A S;
DATABASES: D A T A B A S E S;
TABLES: T A B L E S;
LIKE: L I K E;

SIMPLENAME: [a-zA-Z_] [a-zA-Z_0-9]*;
QUOTEDNAME: '"' ( '\\' . | '""' | ~[\\"] )* '"';
BACKTICKEDNAME: '`' ( '``' | ~[`] )* '`';

LIT_STRING: '\'' ( '\\' . | '\'\'' | ~[\\'] )* '\'';
LIT_INTEGER: '-'? [0-9]+;

WHITESPACE: [ \n\t\r] -> skip;

fragment A: [Aa];
fragment B: [Bb];
fragment C: [Cc];
fragment D: [Dd];
fragment E: [Ee];
fragment F: [Ff];
fragment G: [Gg];
fragment H: [Hh];
fragment I: [Ii];
fragment J: [Jj];
fragment K: [Kk];
fragment L: [Ll];
fragment M: [Mm];
fragment N: [Nn];
fragment O: [Oo];
fragment P: [Pp];
fragment Q: [Qq];
fragment R: [Rr];
fragment S: [Ss];
fragment T: [Tt];
fragment U: [Uu];
fragment V: [Vv];
fragment W: [Ww];
fragment X: [Xx];
fragment Y: [Yy];
fragment Z: [Zz];
