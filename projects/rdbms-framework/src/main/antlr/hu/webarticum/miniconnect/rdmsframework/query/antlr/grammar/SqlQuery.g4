grammar SqlQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

sqlQuery: (
    selectQuery |
    selectCountQuery |
    selectSpecialQuery |
    selectValueQuery |
    updateQuery |
    insertQuery |
    deleteQuery |
    showSchemasQuery |
    showTablesQuery |
    useQuery |
    setVariableQuery
) EOF ;

selectQuery: (
    SELECT selectPart
    FROM ( schemaName '.' )? tableName ( AS? tableAlias=identifier )?
    joinPart*
    wherePart?
    orderByPart?
    limitPart?
);

joinPart: (
	( innerJoin | leftJoin )
	( targetSchemaName=schemaName '.' )? targetTableName=tableName ( AS? tableAlias=identifier )?
	ON scope1=tableName '.' field1=fieldName '=' scope2=tableName '.' field2=fieldName
);
innerJoin: INNER? JOIN;
leftJoin: LEFT OUTER? JOIN;

selectCountQuery: (
    SELECT COUNT PAR_START wildcardSelectItem PAR_END
    FROM ( schemaName '.' )? tableName ( AS? tableAlias=identifier )?
    wherePart?
);

selectPart: selectItem ( ',' selectItem )*;
selectItem: fieldSelectItem | wildcardSelectItem;
fieldSelectItem: scopeableFieldName ( AS? alias=identifier )?;
wildcardSelectItem: ( tableName '.' )? WILDCARD;
limitPart: LIMIT TOKEN_INTEGER;

selectSpecialQuery: ( SELECT | SHOW | CALL ) specialSelectable ( AS? alias=identifier )?;
specialSelectable: specialSelectableName ( parentheses )?;
specialSelectableName:
    CURRENT_USER |
    CURRENT_SCHEMA |
    CURRENT_CATALOG |
    READONLY |
    AUTOCOMMIT |
    IDENTITY |
    LAST_INSERT_ID;

selectValueQuery: SELECT extendedValue ( AS? alias=identifier )?;

updateQuery: UPDATE ( schemaName '.' )? tableName updatePart wherePart?;
updatePart: SET updateItem ( ',' updateItem )*;
updateItem: fieldName '=' extendedValue;

insertQuery: ( INSERT | REPLACE ) INTO ( schemaName '.' )? tableName fieldList? VALUES valueList;
fieldList: '(' fieldName ( ',' fieldName )* ')';
valueList: '(' extendedValue ( ',' extendedValue )* ')';

deleteQuery: DELETE FROM ( schemaName '.' )? tableName wherePart?;

showSchemasQuery: SHOW ( SCHEMAS | DATABASES ) likePart?;

showTablesQuery: SHOW TABLES ( FROM schemaName )? likePart?;

useQuery: USE schemaName;

setVariableQuery: SET variable '=' extendedValue;

wherePart: WHERE whereItem ( AND whereItem )*;
whereItem: scopeableFieldName postfixCondition | '(' whereItem ')';
postfixCondition: '=' extendedValue | isNull | isNotNull;
isNull: IS NULL;
isNotNull: IS NOT NULL;
orderByPart: ORDER BY orderByItem ( ',' orderByItem )*;
orderByItem: ( scopeableFieldName | orderByPosition ) ( ASC | DESC )? ( nullsFirst | nullsLast )?;
nullsFirst: NULLS FIRST;
nullsLast: NULLS LAST;
orderByPosition: TOKEN_INTEGER;
scopeableFieldName: ( tableName '.' )? fieldName;
extendedValue: literal | variable | NULL;
variable: '@' identifier;
fieldName: identifier;
tableName: identifier;
identifier: TOKEN_SIMPLENAME | TOKEN_QUOTEDNAME | TOKEN_BACKTICKEDNAME;
literal: TOKEN_STRING | TOKEN_INTEGER;
likePart: LIKE TOKEN_STRING;
schemaName: identifier;
parentheses: PAR_START PAR_END;

SELECT: S E L E C T;
INSERT: I N S E R T;
REPLACE: R E P L A C E;
UPDATE: U P D A T E;
DELETE: D E L E T E;
SHOW: S H O W;
CALL: C A L L;
USE: U S E;
SET: S E T;

CURRENT_USER: C U R R E N T UNDERSCORE U S E R;
CURRENT_SCHEMA: C U R R E N T UNDERSCORE S C H E M A;
CURRENT_CATALOG: C U R R E N T UNDERSCORE C A T A L O G;
READONLY: R E A D O N L Y;
AUTOCOMMIT: A U T O C O M M I T;
IDENTITY: I D E N T I T Y;
LAST_INSERT_ID: L A S T UNDERSCORE I N S E R T UNDERSCORE I D;

AS: A S;
COUNT: C O U N T;
FROM: F R O M;
INTO: I N T O;
WHERE: W H E R E;
AND: A N D;
ORDER: O R D E R;
BY: B Y;
ASC: A S C;
DESC: D E S C;
LIMIT: L I M I T;
NULLS: N U L L S;
FIRST: F I R S T;
LAST: L A S T;
VALUES: V A L U E S;
IS: I S;
NOT: N O T;
NULL: N U L L;
SCHEMAS: S C H E M A S;
DATABASES: D A T A B A S E S;
TABLES: T A B L E S;
LIKE: L I K E;
LEFT: L E F T;
INNER: I N N E R;
OUTER: O U T E R;
JOIN: J O I N;
ON: O N;

TOKEN_SIMPLENAME: [\p{L}_] [\p{N}\p{L}_]* ;
TOKEN_QUOTEDNAME: '"' ( '\\' . | '""' | ~[\\"] )* '"';
TOKEN_BACKTICKEDNAME: '`' ( '``' | ~[`] )* '`';

TOKEN_STRING: '\'' ( '\\' . | '\'\'' | ~[\\'] )* '\'';
TOKEN_INTEGER: '-'? [0-9]+;

WILDCARD: '*';

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
