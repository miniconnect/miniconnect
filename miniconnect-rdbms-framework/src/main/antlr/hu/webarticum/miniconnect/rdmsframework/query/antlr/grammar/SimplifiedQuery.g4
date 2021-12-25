grammar SimplifiedQuery;

@header {
package hu.webarticum.miniconnect.rdmsframework.query.antlr.grammar;
}

simplifiedQuery: selectQuery;

selectQuery: SELECT LIT_INTEGER alias?;
alias: AS NAME;

SELECT: S E L E C T;
AS: A S;
NAME: [a-zA-Z_] [a-zA-Z_0-9]+;
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
