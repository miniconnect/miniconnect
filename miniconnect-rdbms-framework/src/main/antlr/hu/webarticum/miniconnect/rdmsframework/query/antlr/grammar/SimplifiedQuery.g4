grammar SimplifiedQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

simplifiedQuery: ( selectQuery | updateQuery | insertQuery | deleteQuery ) EOF ;

selectQuery: SELECT selectPart FROM tableName wherePart? orderPart?;
selectPart: selectItems | '*';
selectItems: selectItem ( ',' selectItem )*;
selectItem: field ( AS? alias=identifier )?;

updateQuery: UPDATE tableName SET updateItem ( ',' updateItem )* wherePart?;
updateItem: field '=' value;

insertQuery: INSERT INTO tableName fieldList? VALUES valueList;
fieldList: '(' field ( ',' field )* ')';
valueList: '(' value ( ',' value )* ')';

deleteQuery: DELETE FROM tableName wherePart?;

wherePart: WHERE whereItem ( AND whereItem )*;
whereItem: identifier '=' value | '(' whereItem ')';
orderPart: ORDER BY identifier ( ASC | DESC )?;
field: identifier;
tableName: identifier;
identifier: SIMPLENAME | QUOTEDNAME | BACKTICKEDNAME;
value: LIT_STRING | LIT_DECIMAL | LIT_INTEGER | NULL;

SELECT: S E L E C T;
INSERT: I N S E R T;
UPDATE: U P D A T E;
DELETE: D E L E T E;

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

SIMPLENAME: [a-zA-Z_] [a-zA-Z_0-9]+;
QUOTEDNAME: '"' ( '\\' . | '""' | ~[\\"] )* '"';
BACKTICKEDNAME: '`' ( '``' | ~[`] )* '`';

LIT_STRING: '\'' ( '\\' . | '\'\'' | ~[\\'] )* '\'';
LIT_DECIMAL: '-'? [0-9]+ '.' [0-9]+;
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
