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
  : 'interactive' interactiveSection* EOF
  ;

interactiveSection
  : 'in_scope' reference '{' (varDef ';')* '}'
  ;

interactiveInput
  : varDef ';'? EOF
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
  ;

// name declaration of module

nameDec
  : metaDef 'module' ';'
  | metaDef 'global' 'module' identifier ';'
  ;

// import definition

importDef 
  : 'import' importMember (',' importMember)* 'from' modulePath ';'
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
  : 'alias' reference 'as' aliasName ';'
  ;

aliasName
  : IDENTIFIER
  ;

// export definitions
exportDef
  : 'export' reference ('as' exportName)? ';'
  ;

exportName
  : IDENTIFIER
  ;

// library

library
  : metaDef 'export'? 'library' identifier '{' (libVar ';')* '}'
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
  | binaryLiteral                                           # binaryLiteralExp
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
  | expression'('partialArgs')'                             # partialExp
  | expression'('badArgs')' {/* id: badCallArgs */ false}? # callExp
  | '->>' '('threadArg')' expression (',' expression)*      # threadExp
  | '->>' '('threadArg')' expression (',' expression)* expression {/* id: threadExpMissingSep */ false}? # threadExpErr
  | '->>' expression {/* id: threadExpMissingArg */ false}? # threadExpErr
  | expression'['containerAccessKeySequence']'              # containerAccessExp
  | expression'['badContainerAccessKeySequence']' {/* id: badContainerAccessKeySequence */ false}? # containerAccessExp
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
  | 'let' '{' (varDef ';')* '}' expression       # letExp
  | 'try' expression 'catch' catchDeclaration expression    # tryCatchExp
  | 'throw' expression                                      # throwErrorExp
  | 'debug' '(' expression (',' expression)* ')'            # debugExp
  ;

matchBody
  : matchLine (',' matchLine)*
  | matchLine (',' matchLine)* matchLine {/* id: matchMissingLineSep */ false}?
  ;

matchLine
  : matchPattern (',' matchGuard)? '->' expression                         # patternLine
  | matchPattern  matchGuard '->' {/* id: matchMissingGuardSep */ false}?  # patternLine
  | 'default' '->'  expression                                             # defaultLine
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
  : varCapture                                                                                  # capturePattern
  | dataType    varCapture?                                                                     # dataTypePattern
  | '[' (matchPattern ',') * splatCapture ']' varCapture?                                       # headTailListPattern
  | '[' splatCapture ',' (matchPattern ',') * matchPattern ']' varCapture?                      # initLastListPattern
  | '[' (matchPattern ',') + splatCapture ',' (matchPattern ',')* matchPattern ']' varCapture?  # midListPattern
  | '[' (matchPattern ',') * matchPattern ']' varCapture?                                       # listPattern
  | '{' ((stringConstant matchPattern) ',' )* (stringConstant matchPattern) '}' varCapture?     # dictPattern
  | '{' (((stringConstant matchPattern)|splatCapture) ',' )* ((stringConstant matchPattern)|splatCapture) '}' varCapture? # openDictPattern
  | expression  varCapture?                     # expPattern
  | '[' (',' | matchPattern | splatCapture | wrongSideColonKey) +']' {/* id: matchBadListPattern */ false}? # errListPattern
  | '{' (',' | stringConstant| matchPattern | splatCapture | expression | wrongSideColonKey)+ '}' {/* id: matchBadDictPattern */ false}? # errDictPattern
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
  | STRING_BEGIN (stringText|stringEscapeSequence|(STRING_INTERPOLATION expression RCURLY))* STRING_END # stringInterpolation
  | STRING_BEGIN (unrecognizedEscapeSequence|stringText|stringEscapeSequence|(STRING_INTERPOLATION expression RCURLY))+ STRING_END {/* id: badStringInterpolation */ false}?  # stringInterpolation
  ;

stringText
  : STRING_TEXT
  ;

stringEscapeSequence
  : STRING_ESCAPE_SEQUENCE
  ;

unrecognizedEscapeSequence
  : UNRECOGNIZED_STRING_ESCAPE_SEQUENCE
  ;

longLiteral
  : INT     # decLiteral
  | '-' INT # decLiteral
  | '+' INT # decLiteral
  | HEX # hexLiteral
  ;

binaryLiteral
  : BIN     # binLiteral
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
   | '[' (',' |expression|splat|wrongSideColonKey)+ ']' {/* id: badListLiteral */ false}?
   ;

dictLiteral
   : '{' '}'
   | '{' ((expression expression)|(splat)) (',' ((expression expression)|(splat)))* ','? '}'
   | '{' (',' |expression|splat|wrongSideColonKey)+ '}' {/* id: badDictLiteral */ false}?
   ;

wrongSideColonKey
  : identifier ':'
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
  | BINARY
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
  : ((expression | splat)) (',' (expression | splat))* ','?
  ;

badContainerAccessKeySequence
  : (',' |expression|splat|wrongSideColonKey)+
  ;

partialArgs
  : (namedPartialArg) (',' (namedPartialArg))*
  ;

namedPartialArg
  : identifier '=' expression
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

badArgs
  : (namedPartialArg|namedArg|positionalArg|splatArg|',') *
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
