parser grammar TweakFlowParser;

options {
  tokenVocab=TweakFlowLexer;
}

unit
  : module        # moduleUnit
  | interactive   # interactiveUnit
  ;

// interactive shell

interactive
  : endOfStatement? 'interactive' interactiveSection* EOF
  ;

interactiveSection
  : 'in_scope' reference (varDef endOfStatement?)* endOfStatement?
  ;

interactiveInput
  : varDef EOF
  | expression EOF
  | empty
  ;

standaloneExpression
  : expression EOF
  ;

standaloneReference
  : reference EOF
  ;

empty
  : EOF
  ;

// module

module
  : moduleHead moduleComponent* EOF
  ;

moduleHead
  : nameDec? (importDef | aliasDef | exportDef) *
  ;

moduleComponent
  : library           # libraryComponent
  | endOfStatement    # nopCompoment
  ;

endOfStatement
  : END_OF_STATEMENT
  ;

// name declaration of module

nameDec
  : metaDef 'module' endOfStatement
  | metaDef 'global' 'module' identifier endOfStatement
  ;

// import definition

importDef 
  : 'import' importMember (',' importMember)* 'from' modulePath endOfStatement
  ;

importMember
  : moduleImport
  | componentImport
  ; 

moduleImport
  : '*' 'as' importModuleName
  ;

componentImport
  : exportComponentName ('as' importComponentName)?
  ;

importModuleName
  : IDENTIFIER
  ;

importComponentName
  : IDENTIFIER
  ;

exportComponentName
  : IDENTIFIER
  ;

modulePath
  : stringLiteral
  ;

// alias definitions
aliasDef
  : 'alias' reference 'as' aliasName endOfStatement
  ;

aliasName
  : IDENTIFIER
  ;

// export definitions
exportDef
  : 'export' reference ('as' exportName)? endOfStatement
  ;

exportName
  : IDENTIFIER
  ;

// library

library
  : metaDef 'export'? 'library' identifier '{' (libVar endOfStatement)* '}'
  ;

libVar
  : varDef
  | varDec
  ;

varDef
  : metaDef dataType? identifier ':' expression
  ;

varDec
  : metaDef provided dataType? identifier
  ;

provided
  : 'provided'
  ;

literal
  : nilLiteral                                              # nilLiteralExp
  | booleanLiteral                                          # booleanLiteralExp
  | dateTimeLiteral                                         # dateTimeLiteralExp
  | stringConstant                                          # stringConstantExp
  | longLiteral                                             # longLiteralExp
  | doubleLiteral                                           # doubleLiteralExp
  | listLiteral                                             # listLiteralExp
  | dictLiteral                                             # dictLiteralExp
  | functionLiteral                                         # functionLiteralExp
  ;

expression
  : literal                                                 # literalExp
  | reference                                               # referenceExp
  | expression'('args')'                                    # callExp
  | '->>' '('threadArg')' expression (',' expression)*      # threadExp
  | expression'['containerAccessKeySequence']'              # containerAccessExp
  | '(' expression ')'                                      # nestedExp
  | expression 'as' dataType                                # castExp
  | expression 'default' expression                         # defaultExp
  | '~' expression                                          # bitwiseNotExp
  | '!' expression                                          # boolNotExp
  | 'not' expression                                        # boolNotExp
  | '-' expression                                          # unaryMinusExp
  | expression '**' expression                              # powExp
  | expression '/' expression                               # divExp
  | expression '//' expression                              # intDivExp
  | expression '*' expression                               # multExp
  | expression '%' expression                               # modExp
  | expression '-' expression                               # subExp
  | expression '+' expression                               # addExp
  | expression '..' expression                              # concatExp
  | expression '<<' expression                              # shiftLeftExp
  | expression '>>' expression                              # preservingShiftRightExp
  | expression '>>>' expression                             # zeroShiftRightExp
  | expression '<' expression                               # lessThanExp
  | expression '<=' expression                              # lessThanOrEqualToExp
  | expression '>' expression                               # greaterThanExp
  | expression '>=' expression                              # greaterThanOrEqualToExp
  | expression 'is' dataType                                # isExp
  | 'typeof' expression                                     # typeOfExp
  | expression '===' expression                             # valueAndTypeEqualsExp
  | expression '!==' expression                             # notValueAndTypeEqualsExp
  | expression '==' expression                              # equalExp
  | expression '!=' expression                              # notEqualExp
  | expression '&' expression                               # bitwiseAndExp
  | expression '^' expression                               # bitwiseXorExp
  | expression '|' expression                               # bitwiseOrExp
  | expression '&&' expression                              # boolAndExp
  | expression 'and' expression                             # boolAndExp
  | expression '||' expression                              # boolOrExp
  | expression 'or' expression                              # boolOrExp
  | 'match' expression matchBody                            # matchExp
  | 'for' forHead ',' expression                            # forExp
  | 'if' expression 'then'? expression 'else'? expression   # ifExp
  | 'let' '{' (varDef endOfStatement)* '}' expression                        # letExp
  | 'try' expression 'catch' catchDeclaration expression    # tryCatchExp
  | 'throw' expression                                      # throwErrorExp
  | 'debug' expression (',' expression)?                    # debugExp
  ;

matchBody
  : matchLine (',' matchLine)*
  ;

matchLine
  : matchPattern (',' matchGuard)? '->' expression                  # patternLine
  | 'default' '->'  expression                                      # defaultLine
  ;

matchGuard
  : expression
  ;

varCapture
  : '@' identifier?
  ;

splatCapture
  : '@' '...' identifier?
  ;

matchPattern
  : varCapture                                  # capturePattern
  | dataType    varCapture?                     # dataTypePattern
  | '[' (matchPattern ',') * splatCapture ']' varCapture?  # headTailListPattern
  | '[' splatCapture ',' (matchPattern ',') * matchPattern ']' varCapture?                      # initLastListPattern
  | '[' (matchPattern ',') + splatCapture ',' (matchPattern ',')* matchPattern ']' varCapture?  # midListPattern
  | '[' (matchPattern ',') * matchPattern ']' varCapture?                                       # listPattern
  | '{' ((stringConstant matchPattern) ',' )* (stringConstant matchPattern) '}' varCapture?                               # dictPattern
  | '{' (((stringConstant matchPattern)|splatCapture) ',' )* ((stringConstant matchPattern)|splatCapture) '}' varCapture? # openDictPattern
  | expression  varCapture?                     # expPattern
  ;

threadArg
  : expression
  ;

generator
  : dataType? identifier '<-' expression
  ;

forHead
  : generator (',' (generator | varDef | expression))*
  ;

catchDeclaration
  :                               # catchAnonymous
  | identifier                    # catchError
  | identifier ',' identifier     # catchErrorAndTrace
  ;

// literals

nilLiteral
  : NIL
  ;

stringLiteral
  : VSTRING           # stringVerbatim
  | HEREDOC_STRING    # stringHereDoc
  | STRING_BEGIN (stringText|stringEscapeSequence|stringReferenceInterpolation)* STRING_END # stringInterpolation
  ;

stringText
  : STRING_TEXT
  ;

stringEscapeSequence
  : STRING_ESCAPE_SEQUENCE
  ;

stringReferenceInterpolation
  : STRING_REFERENCE_INTERPOLATION
  ;

longLiteral
  : INT     # decLiteral
  | '-' INT # decLiteral
  | '+' INT # decLiteral
  | HEX # hexLiteral
  ;

doubleLiteral
  : DBL
  | '-' DBL
  | '+' DBL
  ;

booleanLiteral
  : TRUE
  | FALSE
  ;

dateTimeLiteral
  : DT
  ;

listLiteral
   : '['']'
   | '[' (expression|splat) (',' (expression|splat))* ','? ']'
   ;

dictLiteral
   : '{' '}'
   | '{' ((expression expression)|(splat)) (',' ((expression expression)|(splat)))* ','? '}'
   ;


functionLiteral
  : functionHead (expression|viaDec)
  ;

functionHead
  : '(' paramsList ')' '->' dataType?
  ;

paramsList
  :
  | paramDef (',' paramDef) *
  ;

paramDef
  : dataType? identifier ('=' expression)?
  ;

splat
  : '...' expression
  ;

dataType
  : FUNCTION
  | STRING
  | LONG
  | DOUBLE
  | DICT
  | LIST
  | DATETIME
  | BOOLEAN
  | VOID
  | ANY
  ;

reference
  :                     identifier ('.' identifier)*          # localReference
  | ('$'|'global::')    identifier ('.' identifier)*          # globalReference
  | ('::'|'module::')   identifier ('.' identifier)*          # moduleReference
  | ('library::')       identifier ('.' identifier)*          # libraryReference
  ;

identifier
  : IDENTIFIER
  ;

containerAccessKeySequence
  : ((expression | splat)) (',' (expression | splat))*
  ;

args
  : ()
  | (positionalArg|namedArg|splatArg) (',' (positionalArg|namedArg|splatArg))*
  ;

positionalArg
  : expression
  ;

namedArg
  : identifier ':' expression
  ;

splatArg
  : splat
  ;

keyLiteral
  : KEY
  ;

stringConstant
  : keyLiteral                                              # keyLiteralExp
  | stringLiteral                                           # stringLiteralExp
  ;

// via native/builtin support

viaDec
  : VIA literal
  ;

// meta data

metaDef
  : ((meta doc) | (doc meta) | meta | doc | ())
  ;

meta
  : 'meta' literal
  ;

doc
  : 'doc' literal
  ;
