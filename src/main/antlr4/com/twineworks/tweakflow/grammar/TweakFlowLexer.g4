lexer grammar TweakFlowLexer;

@lexer::header
{
  import java.util.Stack;
}

channels {
  WS, COMMENTS
}

@lexer::members
{
  private int stringLevel;
  private Stack<Integer> curlyLevels = new Stack<Integer>();
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
BINARY: 'binary';
LONG: 'long';
DOUBLE: 'double';
DECIMAL: 'decimal';
DICT: 'dict';
LIST: 'list';
DATETIME: 'datetime';
ANY: 'any';
VOID: 'void';

LIBRARY: 'library';

// parens

LP: '(';
RP: ')';

LCURLY: '{' {
  if (stringLevel > 0){
    // increase curly level on current string
    curlyLevels.push(curlyLevels.pop() + 1);
  }
};

RCURLY: '}' {
  if (stringLevel > 0){
    // decrease curly level on current string
    curlyLevels.push(curlyLevels.pop() - 1);

    // if initial curly popped, we're back in string mode again
    if (curlyLevels.peek() == 0){
      curlyLevels.pop();
      popMode();
    }
  }
};

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

DT
  : DATE'T'(TIME(OFFSET|(OFFSET TZ)|TZ)?)?
  ;

fragment YEAR: ('+'|'-')? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT? DIGIT;
fragment DATE: YEAR '-' DIGIT+ '-' DIGIT+;
fragment TIME: DIGIT+':'DIGIT+':' DIGIT+('.'DIGIT+)?;
fragment OFFSET: (('+'|'-')DIGIT+':'DIGIT+)|'Z';
fragment TZ: '@' (TZ_NAME | ID_ESCAPED | ID);
fragment TZ_NAME: [a-zA-Z_]+('/'[a-zA-Z0-9_?]+)+;

INT
  : DIGIT ('_'|DIGIT)*
  ;

HEX
  : '0x' BYTE  BYTE? BYTE? BYTE?
         BYTE? BYTE? BYTE? BYTE?
  ;

BIN
  : '0b' (BYTE | '_')*
  ;

fragment DIGIT: [0-9];
fragment BYTE: HEXDIGIT HEXDIGIT;
fragment HEXDIGIT: DIGIT|[a-fA-F];

DEC
  : INT ('d'|'D')
  | INT? '.' INT EXP? ('d'|'D')
  | INT ('.'INT)? EXP ('d'|'D')
  ;

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
  : ':' KEY_NAME
  | ':' KEY_NAME_ESCAPED
  ;

WHITESPACE
  : [ \t\r\n]+ -> channel(WS)
  ;

END_OF_STATEMENT
  : ';'
  ;

fragment NEWLINE
  : '\r'? '\n'
  ;

fragment KEY_NAME: ([.]?[-+/a-zA-Z_0-9?]+)+;
fragment KEY_NAME_ESCAPED: '`'.+?'`';

fragment ID: [a-zA-Z_][a-zA-Z_0-9?]*;
fragment ID_ESCAPED: '`'.+?'`';

//-----------
// Strings
//-----------

// heredoc string

HEREDOC_STRING
  : '~~~' NEWLINE '~~~'
  | '~~~' NEWLINE .*? NEWLINE '~~~'
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

STRING_BEGIN
  : '"' { stringLevel++; } -> pushMode(StringMode)
  ;

INLINE_COMMENT
  : '/*' (INLINE_COMMENT|.)*? '*/' -> channel(COMMENTS)
  ;

LINE_COMMENT
  : '#' .*? (NEWLINE|EOF) -> channel(COMMENTS)
  ;

mode StringMode;

STRING_ESCAPE_SEQUENCE
  : '\\\\'
  | '\\"'
  | '\\t'
  | '\\#{'
  | '\\n'
  | '\\r'
  | '\\u' BYTE BYTE
  | '\\U' BYTE BYTE BYTE BYTE
  ;

STRING_INTERPOLATION
  : '#{' { curlyLevels.push(1); } -> pushMode(DEFAULT_MODE)
  ;

STRING_TEXT
  : ~('"'|'\\'|'#')+
  | '#'
  ;

STRING_END
  : '"' { stringLevel--; } -> popMode
  ;

UNRECOGNIZED_STRING_ESCAPE_SEQUENCE
  : '\\'(.)+?
  ;