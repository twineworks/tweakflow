lexer grammar TweakFlowLexer;

channels {
  WS, COMMENTS
}

// keywords

INTERACTIVE: 'interactive';
IN_SCOPE: 'in_scope';

GLOBAL: 'global';
MODULE: 'module';
IMPORT: 'import';
EXPORT: 'export';
AS : 'as';
FROM: 'from';
ALIAS: 'alias';

META: 'meta';
DOC: 'doc';
VIA: 'via';

NIL: 'nil';
TRUE: 'true';
FALSE: 'false';
NOT: 'not';
IS: 'is';
IF: 'if';
THEN: 'then';
ELSE: 'else';
FOR: 'for';
TRY: 'try';
CATCH: 'catch';
THROW: 'throw';
LET: 'let';
DEBUG: 'debug';
TYPE_OF: 'typeof';
DEFAULT: 'default';
MATCH: 'match';

PROVIDED: 'provided';

// data types
FUNCTION: 'function';
STRING: 'string';
BOOLEAN: 'boolean';
LONG: 'long';
DICT: 'dict';
LIST: 'list';
DOUBLE: 'double';
DATETIME: 'datetime';
ANY: 'any';
VOID: 'void';

LIBRARY: 'library';

// parens

LP: '(';
RP: ')';

LCURLY: '{';
RCURLY: '}';

LBLOCK: '[';
RBLOCK: ']';

// operators
GENERATOR: '<-';
THREAD: '->>';
ZERO_SHIFT_RIGHT: '>>>';
PRESERVING_SHIFT_RIGHT: '>>';
SHIFT_LEFT: '<<';
COMMA: ',';
IEQ: '===';
EQ: '==';
DEFEQ: '=';
COLON: ':';
EXCL: '!';
PLUS: '+';
MINUS: '-';
LTE: '<=';
LT: '<';
GTE: '>=';
GT: '>';
INTDIV: '//';
DIV: '/';
MOD: '%';
NIEQ: '!==';
NEQ: '!=';
POW: '**';
STAR: '*';
ANDT: 'and';
ORT: 'or';
AND: '&&';
BIT_AND: '&';
OR: '||';
BIT_OR: '|';
BIT_XOR: '^';
RET: '->';
SPLAT: '...';
CONCAT: '..';
DOT: '.';
SQ: '\'';
BACKSLASH: '\\';
TILDE: '~';
DOLLAR: '$';
DOUBLE_COLON: '::';
AT: '@';
MODULE_REF: 'module::';
LIBRARY_REF: 'library::';
GLOBAL_REF: 'global::';
LOCAL_REF: 'local::';


DT
  : DATE'T'(TIME(OFFSET|(OFFSET TZ))?)?
  ;

fragment DATE: [0-9][0-9][0-9][0-9]'-'[0-9][0-9]'-'[0-9][0-9];
fragment TIME: [0-9][0-9]':'[0-9][0-9]':'[0-9][0-9]('.'[0-9]+)?;
fragment OFFSET: (('+'|'-')[0-9][0-9]':'[0-9][0-9])|'Z';
fragment TZ: '@' (ID | ID_ESCAPED);

INT
  : [0-9][0-9]*
  ;

HEX
  : '0x' BYTE  BYTE? BYTE? BYTE?
         BYTE? BYTE? BYTE? BYTE?
  ;

fragment BYTE: HEXDIGIT HEXDIGIT;
fragment HEXDIGIT: [0-9a-fA-F];

DBL
  : INT? '.' INT EXP?
  | INT ('.'INT)? EXP
  | NAN
  | INFINITY
  ;

fragment EXP: ('e'|'E')('+'|'-')? INT;
fragment NAN: 'NaN';
fragment INFINITY: 'Infinity';

IDENTIFIER
  : ID
  | ID_ESCAPED
  ;

KEY
  : ':' ID
  | ':' ID_ESCAPED
  ;

WHITESPACE
  : [ \t\r\n]+ -> channel(WS)
  ;

END_OF_STATEMENT
  : (LINE_SPACE | NEWLINE | SEMICOLON)* SEMICOLON (LINE_SPACE | NEWLINE | SEMICOLON)*
  | (LINE_SPACE | NEWLINE | SEMICOLON)* NEWLINE (LINE_SPACE | NEWLINE | SEMICOLON)*
  ;

fragment SEMICOLON
  : ';'
  ;

fragment NEWLINE
  : '\r'? '\n'
  ;

fragment LINE_SPACE
  : '\t'
  | ' '
  ;

fragment ID: [a-zA-Z_][a-zA-Z_0-9?]*;
fragment ID_ESCAPED: '`'.+?'`';

//-----------
// Strings
//-----------

// heredoc string

HEREDOC_STRING
  : NEWLINE '~~~' NEWLINE '~~~' NEWLINE
  | NEWLINE '~~~' NEWLINE .*? NEWLINE '~~~' NEWLINE
  ;

// verbatim string
// no interpolation
// enclosed in single quotes
// single quote escaped by another single quote
//
//
VSTRING
  : '\'' VSTRING_ELEMENT* '\''
  ;

fragment VSTRING_ELEMENT
  : (VSTRING_CHAR+|VSTRING_ESCAPE_SEQUENCE)
  ;

fragment VSTRING_CHAR
  : ~('\'')
  ;

fragment VSTRING_ESCAPE_SEQUENCE
  : '\'\''
  ;

// string
// enclosed in double quotes
// standard escape sequences

STRING_BEGIN:
  '"' -> pushMode(StringMode)
  ;

INLINE_COMMENT
  : '/*' (INLINE_COMMENT|.)*? '*/' -> channel(COMMENTS)
  ;

MULTILINE_COMMENT
  : '###' .*? NEWLINE '###' -> channel(COMMENTS)
  ;

LINE_COMMENT
  : '#' .*? (NEWLINE|EOF) -> channel(COMMENTS)
  ;

mode StringMode;

STRING_TEXT
  : ~('"'|'\\'|'#')+
  ;

STRING_ESCAPE_SEQUENCE
  : '\\\\'
  | '\\"'
  | '\\t'
  | '\\#'
  | '\\n'
  | '\\r'
  | '\\u' BYTE BYTE
  | '\\U' BYTE BYTE BYTE BYTE
  ;

STRING_REFERENCE_INTERPOLATION
  : '#{'('$'|'@'|'::'|'~')?IDENTIFIER('.' IDENTIFIER)*'}'
  ;

STRING_END
  : '"' -> popMode
  ;

