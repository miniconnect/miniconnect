grammar SqlQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

sqlQuery: (
    selectQuery |
    specialSelectQuery |
    updateQuery |
    insertQuery |
    deleteQuery |
    showSchemasQuery |
    showTablesQuery |
    useQuery
) EOF ;

selectQuery: (
    SELECT selectPart
    FROM ( schemaName '.' )? tableName ( AS? tableAlias=identifier )?
    wherePart?
    orderByPart?
    limitPart?
);

selectPart: selectItems | '*';
selectItems: selectItem ( ',' selectItem )*;
selectItem: scopeableFieldName ( AS? alias=identifier )?;
limitPart: LIMIT LIT_INTEGER;

specialSelectQuery: ( SELECT | SHOW | CALL ) specialSelectable ( AS? alias=identifier )?;
specialSelectable: specialSelectableName ( parentheses )?;
specialSelectableName:
    CURRENT_USER |
    CURRENT_SCHEMA |
    CURRENT_CATALOG |
    READONLY |
    AUTOCOMMIT |
    IDENTITY |
    LAST_INSERT_ID;

updateQuery: UPDATE ( schemaName '.' )? tableName updatePart wherePart?;
updatePart: SET updateItem ( ',' updateItem )*;
updateItem: fieldName '=' value;

insertQuery: INSERT INTO ( schemaName '.' )? tableName fieldList VALUES valueList;
fieldList: '(' fieldName ( ',' fieldName )* ')';
valueList: '(' nullableValue ( ',' nullableValue )* ')';

deleteQuery: DELETE FROM ( schemaName '.' )? tableName wherePart?;

showSchemasQuery: SHOW ( SCHEMAS | DATABASES ) likePart?;

showTablesQuery: SHOW TABLES ( FROM schemaName )? likePart?;

useQuery: USE schemaName;

wherePart: WHERE whereItem ( AND whereItem )*;
whereItem: scopeableFieldName postfixCondition | '(' whereItem ')';
postfixCondition: '=' value | isNull | isNotNull;
isNull: IS NULL;
isNotNull: IS NOT NULL;
orderByPart: ORDER BY orderByItem ( ',' orderByItem )*;
orderByItem: scopeableFieldName ( ASC | DESC )?;
scopeableFieldName: ( tableName '.' )? fieldName;
fieldName: identifier;
tableName: identifier;
identifier: SIMPLENAME | QUOTEDNAME | BACKTICKEDNAME;
nullableValue: value | NULL;
value: LIT_STRING | LIT_INTEGER;
likePart: LIKE LIT_STRING;
schemaName: identifier;
parentheses: PAR_START PAR_END;

SELECT: S E L E C T;
INSERT: I N S E R T;
UPDATE: U P D A T E;
DELETE: D E L E T E;
SHOW: S H O W;
CALL: C A L L;
USE: U S E;

CURRENT_USER: C U R R E N T UNDERSCORE U S E R;
CURRENT_SCHEMA: C U R R E N T UNDERSCORE S C H E M A;
CURRENT_CATALOG: C U R R E N T UNDERSCORE C A T A L O G;
READONLY: R E A D O N L Y;
AUTOCOMMIT: A U T O C O M M I T;
IDENTITY: I D E N T I T Y;
LAST_INSERT_ID: L A S T UNDERSCORE I N S E R T UNDERSCORE I D;

AS: A S;
FROM: F R O M;
INTO: I N T O;
WHERE: W H E R E;
AND: A N D;
ORDER: O R D E R;
BY: B Y;
ASC: A S C;
DESC: D E S C;
LIMIT: L I M I T;
VALUES: V A L U E S;
SET: S E T;
IS: I S;
NOT: N O T;
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

PAR_START: '(';
PAR_END: ')';

WHITESPACE: [ \n\t\r] -> skip;

fragment UNDERSCORE: [_];

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
