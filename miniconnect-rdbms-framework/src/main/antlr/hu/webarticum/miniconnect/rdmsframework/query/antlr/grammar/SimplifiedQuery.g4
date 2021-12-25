grammar SimplifiedQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

simplifiedQuery: selectQuery;

selectQuery: SELECT ( selectFields | '*' ) FROM tableName=identifier wherePart? orderPart?;
selectFields: selectItem ( ',' selectItem )*;
selectItem: field=identifier ( AS? alias=identifier )?;

// TODO: insert, update, delete

wherePart: WHERE whereItem ( AND whereItem )*;
whereItem: identifier '=' value | '(' whereItem ')';
orderPart: ORDER BY identifier ( ASC | DESC )?;
identifier: SIMPLENAME | QUOTEDNAME;
value: LIT_STRING | LIT_DECIMAL | LIT_INTEGER;

SELECT: S E L E C T;
INSERT: I N S E R T;
UPDATE: U P D A T E;
DELETE: D E L E T E;
AS: A S;
FROM: F R O M;
WHERE: W H E R E;
AND: A N D;
ORDER: O R D E R;
BY: B Y;
ASC: A S C;
DESC: D E S C;
VALUES: V A L U E S;
SET: S E T;

QUOTEDNAME: '"' ('\\' . | '""' | ~[\\"] )* '"';
SIMPLENAME: [a-zA-Z_] [a-zA-Z_0-9]+;

LIT_STRING: '\'' ('\\' . | '\'\'' | ~[\\'] )* '\'';
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
