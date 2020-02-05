---
title: The tweakflow language
---

## Motivation

Tweakflow offers a way for JVM applications to evaluate user-supplied expressions in a formula-like notation. Tweakflow supports user-defined functions, libraries, and modules, to support applications in which the user-supplied computations can grow to non-trivial size and complexity. The host application is in control of how much sophistication is available to users.

## Requirements

Tweakflow runs on the JVM. Java 8 or later is required. Builds are tested against JDK 8 and JDK 11.


## Design principles

The following sections outline fundamental principles which inform the design of tweakflow.

### Everything is a value

All data and functions in tweakflow are immutable values. You can always create, inspect, compare and pass them around without worrying about object identity or unexpected modifications. There is no way a value in tweakflow can change.

Tweakflow uses [persistent data structures](https://en.wikipedia.org/wiki/Persistent_data_structure) for its collections. It comes with a set of functions in the standard library to make computations with immutable values easy.

### Reproducible calculations

All functions in tweakflow are pure. The practical consequence is that user-expressions do not maintain state, and cannot trigger any side-effects. All effectful operations like file I/O are performed by the host application. The results of such operations can be passed to user expressions as values, but user expressions cannot introduce any side-effects to the application themselves.

The above paradigm is familiar from spreadsheet applications. Spreadsheets allow users to work with data using formula expressions, but the formulas themselves are deterministic. The outcome of a spreadsheet calculation does not depend on when the spreadsheet was opened and by whom. Similarly, the outcome of tweakflow expressions is guaranteed to be reproducible and side-effect free.

### The host application is in control

Allowing users to perform computations in an application has implications, especially when users can access application internals. Many general-purpose languages on the JVM, like JRuby, Closure, Scala, or various implementations of Javascript have excellent Java interop capabilities. Tweakflow deliberately does not offer such features.

While tweakflow functions can be written in Java for performance reasons, they must implement an interface and be on the classpath. Calling arbitrary Java code is not possible. When embedding tweakflow, the host application sets up a load path that controls which tweakflow code can contain functions implemented in Java. In addition, the host application can remove or replace the default standard library that comes with tweakflow. As a result applications control precisely what user expressions can call or have access to.

## Lexical structure

### Boolean literals

The tokens `true` and `false` are interpreted as boolean literals.

### The nil literal

The `nil` literal represents the singleton [nil value](#nil).

### Long literals

Decimal digits are read as 64-bit signed integers of type `long`. A prefix of `-` or `+` indicates a negative or positive value respectively.
Non-leading `_` characters can be used to help visually format the number.

```tweakflow
42
-2
+3
100_000
```

Long literals can also be written in hexadecimal form. They are notated as `0x` followed by up to  8 bytes. Each byte consists of two hexadecimal digits from `[0-9a-fA-F]`. The bytes are given in  big-endian order, meaning that the most significant byte is written first. If less than 8 bytes are provided, missing leading bytes are filled up with zeros. The resulting bit pattern is interpreted as a [two's complement](https://en.wikipedia.org/wiki/Two%27s_complement) signed 64-bit integer, exactly like a Java long value.

```tweakflow
> 0x00
0

> 0xFF
255

> 0xE5E7
58855

> 0xFFFFFFFFFFFFFFFF
-1

> 0x7FFFFFFFFFFFFFFF
9223372036854775807

> 0x8000000000000000
-9223372036854775808
```

### Double literals

Floating point numbers are read as 64 bit double precision literals of type `double` based on the IEEE 754 specification, exactly like double values in Java.

There are several ways to notate a double literal:

- As integer followed by decimal dot, followed by fraction digits and optional exponent notation
- As decimal dot, followed by fraction digits and optional exponent notation
- As integer followed by exponent notation

Exponent notation is given by an `e` or `E` character and followed by the powers of ten to multiply with.
Non-empty digit sequences denoting the integer, fraction, or exponent can additionally contain
`_` characters in non-leading positions to visually format the number.

```tweakflow
# various ways to write the double number 3.1315

> 3.1315
3.1315

> 3.13_15
3.1315

> 0.31315e1
3.1315

> .31315E1
3.1315

> 31315_e-4
3.1315
```

Tweakflow does not support hexadecimal notation for doubles.

In addition to regular numbers `NaN` (Not a Number) and `Infinity` literals can be used.

```tweakflow
> Infinity
Infinity

> -Infinity
-Infinity

> Infinity - Infinity
NaN

> Infinity * 2.0
Infinity

> NaN + 1
NaN

> typeof Infinity
"double"

> typeof NaN
"double"
```

### Decimal literals

Decimal numbers are arbitrary precision numeric values, internally represented by a [BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html).

Decimal literals are notated as decimal long values, or finite double values, followed by the characters `d` or `D`.

```tweakflow
# various ways to write the decimal number 3.1315

> 3.1315d
3.1315d

> 3.13_15_d
3.1315d

> 0.31315e1d
3.1315d

> .31315E1D
3.1315d

> 31315_e-4d
3.1315d
```

The scale of the resulting decimal is the number of fractional digits minus the given exponent.

```tweakflow
> decimals.scale(3.1314d)
4

> decimals.scale(3.1314000d)
7

> decimals.scale(1e+6d)
-6

> decimals.scale(1.1e+6d)
-5
```

### Binary literals

Binary literals represent sequence of of bytes. They are notated as `0b` followed by zero or more pairs of hex digits from `[0-9a-fA-F]`.
The separator character `_` can used to separate the hex-digit pairs and visually format the data.
It can appear after the `0b` prefix in leading position, trailing position, and before and after any hex-digit pair.
The separator character may be repeated.

```tweakflow
# empty byte array
> 0b
0b

# single byte: 00
> 0b00
0b00

# four bytes: 01, 02, 03, FF
> 0b010203FF
0b010203FF

# eight bytes: 40, 09, 21, fb, 54, 44, 2d, 18
> 0b_4009_21fb__5444_2d18
0b8400b821fb8544682d18
```

### String literals

Strings can occur in many places of an expression or program, playing different semantic roles. Stings appear as keys in dictionaries, as computation values, or as documentation strings, for example. Tweakflow offers several ways to write a literal string. The notations are interchangeable. Each of the notations is valid at any place a string is valid.

#### Single-quoted strings

A single quoted string begins and ends with a single quote character `'`. Line breaks, tabs, and other special characters are allowed, and included in the string verbatim. If a single-quote character is to be included in the string, it must be escaped with another single-quote. Aside from that, a single-quoted string does not expand any escape sequences.

```tweakflow
> 'hello world'
"hello world"

> 'a single quote: '''
"a single quote: '"

> 'Joe''s Bar'
"Joe's Bar"

> \e
'Line 1
Line 2
Line 3'
\e
"Line 1
Line 2
Line 3"
```

#### Double-quoted strings

A double-quoted string begins and ends with a double quote character `"`. Line breaks, tabs, and other special characters are allowed, and included in the string verbatim. The following escape sequences are expanded:

| Escape sequence | Expands to                               |
| --------------- | ---------------------------------------- |
| `\\`            | `\` backslash                            |
| `\"`            | `"` double quote                         |
| `\t`            | `⇥` tab character
| `\n`            | `⏎` newline character                    |
| `\r`            | `⏎` carriage return character            |
| `\u[byte]{2}`   | unicode character from the [basic multilingual plane](https://en.wikipedia.org/wiki/Plane_(Unicode)#Basic_Multilingual_Plane) given by the two-byte address |
| `\U[byte]{4}`   | any unicode character given by the full four-byte address |
| `#{expression}`  | value of expression cast to string |
| `\#{`           | `#{` literal hash character followed by curly brace |

To prevent expansion of escape sequences beginning with a backslash, escape the backslash, so it is interpreted literally. To prevent expansion of an expression, escape the hash character that opens the sequence, so it is interpreted literally.

```tweakflow
> "hello world"
"hello world"

> "hello\nworld"
"hello
world"

> "hello\\nworld"
"hello\\nworld"

> "A \u2287 B"
"A ⊇ B"

> "I like \U0001d11e"
"I like 𝄞"

> name: "Joe"
"Joe"
> "#{name}'s Bar"
"Joe's Bar"
```

#### Here document strings

The [here document](https://en.wikipedia.org/wiki/Here_document) string notation starts with `~~~\r?\n` and ends with `\r?\n~~~`. All characters in between are preserved as a literal string that does not expand any escape sequences.

```text
​~~~
[literal string]
​~~~
```

This style of string is useful when the role of a string is to represent a separate document. It is typically used for documentation or embedded documents.

```tweakflow
> \e
~~~
Hello World
~~~
\e
"Hello World"

> \e
~~~
<Contact>
  <Name>John Doe</Name>
  <Title>CEO</Title>
  <Phone>
    <Number>555-8401</Number>
    <Type>Voice</Type>
  </Phone>
</Contact>
~~~
\e
"<Contact>
  <Name>John Doe</Name>
  <Title>CEO</Title>
  <Phone>
    <Number>555-8401</Number>
    <Type>Voice</Type>
  </Phone>
</Contact>"
```

#### Symbol strings

Tweakflow allows symbol notation for strings. It can be useful to visually distinguish strings filling the semantic role of keys or enumeration items from data strings. Symbol strings are prepended with a `:`. Their name is their value.

A symbol string can consist of letters, digits, and the characters `.-+/?`

A symbol string must not end with the `.` character.

Formally a symbol string is written as:

```text
:([.]?[-+/a-zA-Z_0-9?]+)+
```

The escaped form with backticks allows an unconstrained set of characters, with the exception of the backtick itself:

```text
:`.+?`
```
Symbol strings are regular strings. They are merely a notational convenience to distinguish data strings from strings used as keys. Therefore symbol notation is allowed in all places a string is accepted.

```tweakflow
> :foo
"foo"

> :`Hello World`
"Hello World"

> nums: {:one 1, :two 2, :three 3}
{
  :one 1,
  :two 2,
  :three 3
}

> nums[:one]
1

> nums["two"]
2

> :Hello .. :` ` .. :World
"Hello World"
```

### Datetime literals

Datetime literals can be specified at various levels of granularity. Starting at the level of days, the datetime literals take the form `[year]-[month]-[day]T`  with year given as four to nine digits and month and day given as one or two digits each. The year may carry a `-` or `+` prefix.

```tweakflow
> 2019-4-30T
2019-04-30T00:00:00Z@UTC

> -0001-1-1T
-0001-01-01T00:00:00Z@UTC

> +999999-03-12T
+999999-03-12T00:00:00Z@UTC
```

The basic form is extended to specify the local time in 24 hour format as `[hours]:[minutes]:[seconds](.[fraction_of_seconds])?` with one or two digits for hours, minutes, and seconds, and up to nine digits for the optional fraction of seconds.

```tweakflow
> 2017-04-30T21:32:11
2017-04-30T21:32:11Z@UTC

> 2017-04-30T21:32:11.123456789
2017-04-30T21:32:11.123456789Z@UTC
```

The local time form is extended to specify an offset from UTC of the form `((+|-)[offset_hours]:[offset_minutes])|Z`  where offset hours and offset minutes is specified with one or two digits each. The shorthand Z means UTC time, no offset.

```tweakflow
> 2017-04-30T21:32:11+02:00
2017-04-30T21:32:11+02:00@`UTC+02:00`

> 2017-04-30T21:32:11Z
2017-04-30T21:32:11Z@UTC
```

The UTC offset form can be further refined to include the regional time zone, ensuring consistency of local time calculations while observing daylight saving time. The regional time zone form appends an `@` sign, followed by the id of the desired time zone.

A time zone id is valid if recognized by Java's [ZoneId.of](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-java.lang.String-). A list of known regional zone ids can be obtained by calling [time.zones](/modules/std.html#time-zones) of the tweakflow standard module. In addition, time zones giving a constant offset from UTC or GMT are accepted, as per the documentation of [ZoneId.of](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-java.lang.String-).

If a time zone id matches one of the following syntax rules, it may but need not be escaped by backticks:
```text
[a-zA-Z_][a-zA-Z_0-9?]*            # regular identifier syntax
[a-zA-Z_]+('/'[a-zA-Z0-9_?]+)+     # extended syntax for time zone ids, allowing sections separated by the '/' character
```

If a time zone id does not match above syntax, it must be escaped by backticks, just like [identifiers](#identifiers).

Examples:
```tweakflow
> 2017-04-30T21:32:11+02:00@`Europe/Berlin`     # can always escape a time zone id
2017-04-30T21:32:11+02:00@Europe/Berlin

> 2017-04-30T21:32:11+02:00@Europe/Berlin       # most regional time zone ids need no escaping
2017-04-30T21:32:11+02:00@Europe/Berlin

> 2017-04-30T21:32:11+02:00@`UTC+02:00`         # escaped time zone id that needs escaping
2017-04-30T21:32:11+02:00@`UTC+02:00`

> 2017-04-30T21:32:11+02:00@UTC+02:00           # missing escaping on time zone id
ERROR:
  code: PARSE_ERROR
```

In case the UTC offset is omitted, but a time zone id is provided, an offset implied by the time zone id is used.

```tweakflow
> 2019-01-01T00:00:00@Europe/Berlin
2019-01-01T00:00:00+01:00@Europe/Berlin # note that +01:00 is used
```

If in the case of omitted offset the datetime is ambiguous because of daylight saving mechanics, the following rules apply:

 - if the datetime falls into a gap, the length of the gap is added
 - if the datetime falls into an overlap, the earlier point in time is used

```tweakflow
# time does not exist as given, it falls into a 1-hour DST gap, so 1 hour is added
> 2019-03-31T02:30:00@Europe/Berlin
2019-03-31T03:30:00+02:00@Europe/Berlin

# time falls into a DST overlap, the earlier point in time is used.
# In this case +02:00 in favor of +01:00
> 2019-10-27T02:30:00@Europe/Berlin
2019-10-27T02:30:00+02:00@Europe/Berlin

# you can notate the later point in time by manually supplying the offset
> 2019-10-27T02:30:00+01:00@Europe/Berlin
2019-10-27T02:30:00+01:00@Europe/Berlin
```

### List literals

Lists are notated as a sequence of values inside square brackets. Commas are separating entries. The empty list is written as `[]`.

A splat expression can be used to concatenate lists inline.

The formal syntax of a list literal is as follows:

```text
listLiteral
   : '['']'
   | '[' (expression|splat) (',' (expression|splat))* ','? ']'
   ;

splat
  : '...' expression
  ;
```

A few example lists:

```tweakflow
> [] # empty list
[]

> [1, 2, 3] # a basic list
[1, 2, 3]

> [[1, 2], [3, 4]] # lists can be nested
[[1, 2], [3, 4]]

> [{:id 1, :name "Johne Doe"}, {:id 2, :name "Jane Doe"}] # lists nest with dicts
[{
  :name "Johne Doe",
  :id 1
}, {
  :name "Jane Doe",
  :id 2
}]
```

When a splat expression is encountered, it is evaluated, cast to list and concatenated with any previous list items. A few examples of splats:

```tweakflow
> [1, 2, ...[3, 4, 5]]
[1, 2, 3, 4, 5]

> [1, 2, ...{:key "value"}, 3] # the splat dict is cast to a list before concat
[1, 2, "key", "value", 3]

> prepend: (x, list xs) -> list [x, ...xs]
function

> prepend("a", ["b", "c"])
["a", "b", "c"]

> append: (list xs, x) -> list [...xs, x]
function

> append(["x", "y"], "z")
["x", "y", "z"]
```

### Dict literals

Dicts are notated as a sequence of key and value pairs inside curly brackets. Keys are implicitly cast to strings. Commas are separating entries. The empty dict is written as `{}`.

A splat expression can be used to merge dicts inline.

The formal syntax of a dict literal is as follows:

```text
dictLiteral
   : '{' '}'
   | '{' ((expression expression)|(splat)) (',' ((expression expression)|(splat)))*  ','? '}'
   ;

splat
  : '...' expression
  ;
```

A few example dicts:

```tweakflow
> {:code 200, :status "found", :size 1232}
{
  :size 1232,
  :code 200,
  :status "found"
}

> {"one" 1, "two" 2}
{
  :one 1,
  :two 2
}

> {:result "ok", :content_types ["xml", "json"]} # dicts nest with lists
{
  :content_types ["xml", "json"],
  :result "ok"
}

# dicts nest with other dicts
> {:people {"1" {:id 1, :name "John Doe"}, "2" {:id 2, :name "Jane Doe"}}}
{
  :people {
    :`1` {
      :name "John Doe",
      :id 1
    },
    :`2` {
      :name "Jane Doe",
      :id 2
    }
  }
}
```

When a splat expression is encountered, the splat value is cast to dict and merged with the existing dict. The rightmost merged dict values take precedence in case splats contain keys that are already present.

```tweakflow
> {:code 200, ...{:status "found", :size 1232}}
{
  :size 1232,
  :code 200,
  :status "found"
}

# rightmost value for key :status is preserved
> {:request_id 8273, :status "ok", ...{:code 403, :status "forbidden"}}
{
  :request_id 8273,
  :code 403,
  :status "forbidden"
}
```

### Function literals

Function notation has two parts: function head, and body. The head holds the function signature: parameter list and return type. The body is either an expression that evaluates to the function's return value, or a structure specifying the Java class that is implementing the function.

Formally the syntax is as follows:

```text
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

viaDec
  : 'via' literal
  ;
```

A function head specifies a parameter list, and an optional return type. If the return type is omitted `any` is used. Parameter list items are delimited by commas. Each parameter has a name, an optional data type, and an optional default value. If the data type is omitted, `any` is used, if the default value is omitted `nil` is used.


Some examples:

```tweakflow
# A function with no parameters, returning a constant of any type
> f: () -> 1
function

> f()
1

# A function taking two strings and returning a string
> f: (string x, string y) -> string    x .. y
function

> f("John", "Doe")
"JohnDoe"

# A function taking a list and returning a list
> f: (list xs) -> list    data.map(xs, (_, i) -> xs[data.size(xs)-1-i])

function
> f([1, 2, 3])
[3, 2, 1]

# A function taking two doubles, each having a default value, returning a double
> f: (double x=1.0, double y=0.0) -> double    x+y
function

> f(3, 4)
7.0

> f()
1.0

> f(0)
0.0

> f(x: 2, y: 3)
5.0

> f(y: 7)
8.0

> f: (string x) -> long via {:class "com.twineworks.tweakflow.std.Strings$length"}
function

> f("foo")
3
```

### Identifiers

Tweakflow identifiers take one of the following forms.

The first form consists of an alphabetic or underscore character, followed by zero or more alphanumeric, underscore or question mark characters:

```text
[a-zA-Z_][a-zA-Z_0-9?]*
```

The escaped form with backticks allows an unconstrained set of characters, with the exception of the backtick itself:

```text
`.+?`
```

The following example uses both variants of the syntax. The variable `%name%` has characters that are not permitted in an identifier, and is therefore escaped:

```tweakflow
> \e
let {
  greeting: "Hello";
  `%name%`: "Joe";
}
greeting .. " " .. `%name%`
\e
"Hello Joe"
```

### Comments

Tweakflow supports line comments and span comments. Each form catering to different uses of comments in code.

#### Line comments

The `#` token signifies a line comment. The `#` character and all subsequent characters to the next newline are ignored.

```tweakflow
> 3 # This is a comment
3
```

#### Span comments

The comment markers `/*` and `*/` enclose a comment that can span multiple lines. Span comments can be nested.

```tweakflow
> 3 /* This is a comment */
3

> \e
/*
this comment
spans multiple lines
*/
"hello"
\e
"hello"
```

## Semantic structure

### Modules

Modules are the top level organizational unit in tweakflow. A module is typically a file, but host applications are free to supply modules as in-memory text as well. There is exactly one module per file.

Module files must be encoded in UTF-8. The default file extension for modules is `.tf`.

The role of a module is to provide a top level grouping of related functions and values. In this sense modules act like namespaces or packages.

A module usually starts with the `module` keyword. If there are no [annotations](#annotations), the `module` keyword becomes optional, and the beginning of the file is treated as the beginning of the module.

[Global modules](#global-modules) need to announce the fact that they are global, and provide a global identifier to store the module in. A global module starts with the keywords `global` `module` followed by an identifier. Any  [annotations](#annotations) go before that sequence.

Formally modules have the following syntax:

```text
module
  : moduleHead moduleComponent* EOF
  ;

moduleHead
  : nameDec? (importDef | aliasDef | exportDef) *
  ;

moduleComponent
  : library
  ;

nameDec
  : metaDef 'module' ';'
  | metaDef 'global' 'module' identifier ';'
  ;
```

#### Global modules

Modules can declare themselves available under a specific name in global scope. It is an error to load more than one module claiming the same name.

Global modules are designed to facilitate project-wide configuration and global libraries of which there must be exactly one in scope at all times. Individual modules remain in control of their functional dependencies through imports, but their global dependencies are controlled from the outside. When using tweakflow standalone, a global module would be loaded from the command line. When using tweakflow embedded, typically the host application would be loading global modules.

Please note that a module referencing a global module cannot work standalone. Tweakflow resolves references after all modules are loaded, and unresolved references to global modules cause errors.

**Example use of global modules for configuration**

The following set of files constitute configuration variants, available globally as `env`.

```tweakflow
# environments/local.tf
global module env;

export library conf {
  string data_path: "/home/me/my_project/data/";
}
```

```tweakflow
# environments/production.tf
global module env;

export library conf {
  string data_path: "/var/incoming/data/";
}
```

This file uses environment configuration through the global reference `$env`.

```tweakflow
# main.tf
library app {
  file_path: (string prefix) -> $env.conf.data_path .. prefix .. "_data.csv";
}
```
On the REPL:
```tweakflow
> \load main.tf environments/local.tf
> app.file_path("foo")
"/home/me/my_project/data/foo_data.csv"

> \load main.tf environments/production.tf
> app.file_path("foo")
"/var/incoming/data/foo_data.csv"
```

#### Imports

Import statements bring names exported from other modules into the current module. The syntax allows importing individual names, or all exported names at once. If imported individually, names may be bound to local names that are different from the names in the source module.

It is an error to import a name that is not explicitly exported.

Imported names are placed in module scope.

Imports have the following syntax:

```text

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
```

The given module path is first appended the default module extension if not present. Then the module path is searched for on the load path. If the module path starts with a dot, tweakflow searches for the file relative to the module doing the import. The resulting path must still be on the load path. If the module path does not start with a dot, tweakflow searches all load path locations in their specified order. The order is typically specified on the command line when using language tools or by the host application when embedding.

Two modules may import each other's exports. However, an import must ultimately refer to a concrete entity. It must not refer back to itself through a circular chain of imports, aliases, and exports.

**Examples**

Module `"./util/strings.tf"` is imported as a whole below. Any exported name `x` is available as `utils.x` locally.

```tweakflow
import * as utils from "./util/strings.tf";
```

A specific library `conversion_lib` is imported from `"./util/strings.tf"` below. Its local name remains `conversion_lib`.

```tweakflow
import conversion_lib from "./util/strings.tf";
```

Specific entities are imported individually below. The import statement references two exported libraries from module `"./util/strings.tf"`, making them available under local names `str` and `conv`.

```tweakflow
import string_lib as str, conversion_lib as conv from "./util/strings.tf";
```

#### Aliases

Tweakflow allows local aliases to shorten or relabel names, making local code independent of name conventions in other modules. Aliases are placed in module scope. Circular aliases are not allowed. An alias must ultimately point to a concrete entity.

Aliases have the following syntax:

```text
aliasDef
  : 'alias' reference 'as' aliasName ';'
  ;

aliasName
  : IDENTIFIER
  ;
```

The following module uses aliases.

```tweakflow
# file: aliases.tf
import * as std from "std";

# s can be used as shortcut to std.strings
alias std.strings as s;

# map can be used as shortcut to std.data.map
alias std.data.map as map;

# aliases can be aliased again
alias map as m;

library my_util {

  # s alias used here
  greeting: s.concat(["Hello", " ", "World!"]); 	# "Hello World!"

  # m alias used here
  mapped: m([1,2,3], (x) -> x*x);   # [1, 4, 9]
}
```

#### Exports

A module defines its public interface using exports. Libraries can be exported inline when defined, or explicitly in an export statement. An export statement refers to a name and makes it available for other modules to import. You can optionally specify an export name, that is only visible to other modules when importing, but not within the exporting module itself. Exporting an imported or aliased name is allowed.

```text
exportDef
  : 'export' reference ('as' exportName)? ';'
  ;

exportName
  : IDENTIFIER
  ;
```

Below example exports the strings standard library under the name `str`, and a local library `common` under the name `util` .

```tweakflow
# lib.tf
import * as std from "std";
export std.strings as str;
export common as util;

library common {
  ...
}
```

The following file imports both the `str` and the `util` export.

```tweakflow
# main.tf
import util, str from "./lib.tf";
```

### Libraries

A  library is a named collection of variables. The variables typically hold functions, but they can hold any data type. Libraries can be marked as exports as part of their definition, in which case they are exported from the enclosing module using their given name. All contained variables are placed in library scope. They can also be annotated by [docs and metadata](#annotations).

**Syntax**

```text
library
  : metaDef 'export'? 'library' identifier '{' (libVar ';')* '}'
  ;

libVar
  : varDef
  | varDec
  ;
```

Below is an exported library holding some functions.

```tweakflow
export library nums {
  function square: (x) 	-> x**2;
  function root: (x) 	-> x**0.5;
}
```

### Variables
A variable is a named entity that holds a value. Variables are placed in [libraries](#libraries). Variables have a name, a  [type](#data-types), and a value. They can also be annotated by [docs and metadata](#annotations).

```text
varDef
  : metaDef dataType? identifier ':' expression
  ;

varDec
  : metaDef provided dataType? identifier
  ;

provided
  : 'provided'
  ;
```

The type of variables guarantees that referencing a variable results in a value of the specified type. Variable values are cast implicitly if necessary.

```tweakflow
> boolean bool_var: 1
true

> boolean bool_var: 0
false
```

If unspecified, the variable type is `any`, and no implicit casts take place.

```tweakflow
> some_var: 1
1

> any some_var: 1
1
```

Variable values are either given directly as [expressions](#expressions), or the variables are marked as `provided`. The host application is required to set the values of `provided` variables through the embedding API. Initially, all provided variables have the value `nil`. The host application can determine whether a provided variable is referenced, thus allowing the host application to omit providing values if they are not needed.

Tweakflow uses strict evaluation. All variables of a library are guaranteed to evaluate even if they are not referenced by other expressions.

### Scopes

All named entities like variables, libraries, aliases, and exports are placed in a scope in which each name must be unique. Tweakflow has four different kinds of scope ordered into a hierarchy:

- global scope
- module scope
- library scope
- local scope

There is exactly one global scope per tweakflow program. It contains the names of [global modules](#global-modules), if any are loaded.

There is a distinct module scope per loaded module. It contains the names of the module's imports, aliases, and libraries.

There is a distinct library scope per library in a module. It contains the names of all library variables.

There is a local scope per variable in a module. Nested local scopes are created as part of expressions that introduce identifiers, like [local variables](#local-variables) for example.

Name resolution for references generally starts in the scope the reference appears in. If the name is not found the search propagates one level up until it stops after searching module scope. Global scope is not searched as part of the algorithm. Any references to global names must be explicitly marked as global references. See [references](#references) for syntax details.

### Annotations

Modules, libraries, and variables support documentation and metadata annotations. Annotations are literal values associated a module, library or variable. They can be inspected in the REPL. Language processing tools like [doc](/tools.html#metadata-extraction) can extract them to generate project documentation.

Both doc and meta annotations are optional. They can occur in any order before the definition of a module, library or variable. Doc annotations begin with the keyword `doc` followed by an expression. Meta annotations begin with the keyword `meta` followed by an expression.

The doc and meta expressions must consist of value literals that evaluate to themselves. They cannot contain any form of computation like  operators or function calls. Function literals are not permitted.

The formal syntax is:
```text
metaDef
  : ((meta doc) | (doc meta) | meta | doc | ())
  ;

meta
  : 'meta' literal
  ;

doc
  : 'doc' literal
  ;
```
The following module contains a single library with a single function:

```tweakflow
library bar {
  function baz: (x) -> x*x;
}
```

The same module with a full set of annotations at the module, library, and variable level:

```tweakflow
# module foo.tf
doc
~~~
This is documentation at the module level.
~~~
meta {
  :title       "foo",
  :description "Description of the module",
  :version     "4.2"
}
module;

# library bar
doc 'This is documentation for library bar.'

meta {
  :author "John Doe et al.",
  :since  "2.3"
}

library bar {

  # function baz
  doc 'This is documentation for function baz.'

  meta {
    :author "John Doe",
    :date 2017-03-12T
  }

  function baz: (x) -> x*x;
}
```

You can inspect the documentation and metadata of modules, libraries and variables at the REPL using the `\doc` and `\meta` commands.

```text
foo.tf> \doc
This is documentation at the module level.
foo.tf> \meta
{
  :version "4.2",
  :title "foo",
  :description "Description of the module"
}

foo.tf> \doc bar
This is documentation for library bar.
foo.tf> \meta bar
{
  :author "John Doe et al.",
  :since "2.3"
}

foo.tf> \doc bar.baz
This is documentation for function baz.
foo.tf> \meta bar.baz
{
  :author "John Doe",
  :date 2017-03-12T00:00:00Z@UTC
}
```

## Data types

Tweakflow supports a fixed set of data types. Each data type has literal notation, and a set of supported cast targets to other types. The following sections highlight all available types and their characteristics.

### Boolean

The `boolean` type holds the values `true` and `false`. Booleans are notated using [boolean literals](#boolean-literals). The following type casts are supported:

Boolean as long

Boolean `true` is cast to `1` and boolean `false` is cast to `0`.

Boolean as double

Boolean `true` is cast to `1.0` and boolean `false` is cast to `0.0`.

Boolean as decimal

Boolean `true` is cast to `1d` and boolean `false` is cast to `0d`.

Boolean as string

Boolean `true` is cast to `"true"` and boolean `false` is cast to `"false"`.

### Long

The `long` type holds 64-bit signed integers. Integers are notated using [long literals](#long-literals). The following type casts are supported:

Long as boolean

The long `0` is converted to `false`. Any other long value is converted to `true`.

Long as double

The long number is converted to to closest double value possible.

Long as decimal

The long number is converted to the corresponding decimal value.

Long as string

The long is converted to a decimal number with a potential leading minus sign.

### Double

The `double` type holds 64-bit double-precision IEEE 754 floating point numbers. Literal floating point numbers are notated using [double literals](#double-literals). The following type casts are supported:

Double as boolean

The double values `0.0`,  `-0.0`, and `NaN` are converted to `false`. Any other values are converted to `true`.

Double as long

The double value is truncated at the decimal point and converted to the closest long value.

If the double is `NaN`, the converted value is `0`.

If the double is `Infinity`, the converted value is `math.max_long`.

If the double is `-Infinity`, the converted value is `math.min_long`.

Double as decimal

The double value is converted to its [string representation](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#toString-double-), which is then converted to a decimal. The intermediate conversion to string compensates for erratic results caused by the inexact representation of floating point numbers.

If the double value is not finite, the resulting decimal is `0d`.

Double as string

- If the double value is `NaN`, the string is `"NaN"`.
- If the double value is positive infinity, the string is `"Infinity"`.
- if the double value is negative infinity, the string is `"-Infinity"`.
- if the double value is 0.0, the string is `"0.0"`
- if the double value is -0.0, the string is `"-0.0"`

For any other double value, the [conventions the Java language](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#toString-double-) are used.

Casting doubles to string should only be done for non-functional purposes like data-inspection, debugging or logging. The standard library offers [formatters](/modules/std.html#math-formatter) to to convert double values to strings in a controlled output format.

### Decimal

The `decimal` type holds an exact arbitrary precision numeric value, stored internally as a [BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html). Literal decimals are notated using [decimal literals](#decimal-literals). The following type casts are supported:

Decimal as boolean

Decimal values equal to `0d` are converted to `false`. Any other values are converted to `true`.

Decimal as long

The decimal value is truncated at the decimal point and converted to the closest long value.

Decimal as double

The decimal value is converted to the closest double value.

  - If the decimal value is greater than the maximum double value, the result is `Infinity`.
  - If the decimal value is less than the minimum double value, the result is `-Infinity`.

Decimal as string

The decimal value is converted to a string, possibly using exponent notation, which preserves all digits and scale of the decimal value. The resulting string value can be safely cast back to the original decimal value.

### Binary

The `binary` type holds an array of bytes. Binary values are notated using [binary literals](#binary-literals). The following type casts are supported:

Binary as boolean

The empty binary of 0 bytes is converted to `false`. Any non-empty binary value is converted to `true`.

### String

The `string` type holds text information. Strings are notated using [string literals](#string-literals). The following type casts are supported:

String as boolean

The empty string `""` is cast to `false`. Any other string value is cast to `true`.

String as long

The string is first trimmed of whitespace on both sides. It is then interpreted as a decimal number with an optional leading `+` or `-` sign, any leading zeros, and digits `0-9`. The trimmed string must conform to the regular expression:

```text
[+-]?[0-9]+
```

If the resulting number does not fit in a 64-bit signed integer, an error is thrown.

String as double

Strings cast to doubles successfully if they pass the following regular expression:

```text
[\x00-\x20]*                                 # optional leading whitespace
[-+]?                                        # optional sign
(
(NaN)|                                       # Not a Number
(Infinity)|                                  # Infinity
([0-9]+(\.[0-9]+)?([eE][-+]?[0-9]+)?)|       # Digits optionally followed by decimal dot
                                             # fractional digits, and exponent
(\.[0-9]+([eE][-+]?[0-9]+)?)                 # decimal dot followed by fractional digits
                                             # and exponent
)
[\x00-\x20]*                                 # optional trailing whitespace
```

Examples for casts from string to double:

```tweakflow
> "1.0" as double
1.0

> "2e3" as double
2000.0

> "2230.3e-1" as double
223.03

> ".98e2" as double
98.0

> "200.0kg" as double
ERROR:
  code: CAST_ERROR
  message: Cannot cast 200.0kg to double
```

String as decimal

Strings cast to decimals successfully if they pass the following regular expression:

```text
[\x00-\x20]*                                 # optional leading whitespace
[-+]?                                        # optional sign
(
([0-9]+(\.[0-9]+)?([eE][-+]?[0-9]+)?)|       # Digits optionally followed by decimal dot
                                             # fractional digits, and exponent
(\.[0-9]+([eE][-+]?[0-9]+)?)                 # decimal dot followed by fractional digits
                                             # and exponent
)
[\x00-\x20]*                                 # optional trailing whitespace
```

Examples for casts from string to decimal:

```tweakflow
> "1.0" as decimal
1.0d

> "2e3" as decimal
2E+3d

> "2230.3e-1" as decimal
223.03d

> ".98e2" as decimal
98d

> "200.0kg" as decimal
ERROR:
  code: CAST_ERROR
  message: Cannot cast 200.0kg to decimal
```

String as list

A string is converted to a list of individual character strings. More precisely, it is converted to a list of its unicode code points.

```tweakflow
> "" as list
[]

> "hello" as list
["h", "e", "l", "l", "o"]

> "I love 𝄞" as list
["I", " ", "l", "o", "v", "e", " ", "𝄞"]
```

### Datetime

The `datetime` type represents points in time while also carrying regional time zone information. Datetimes are notated using [datetime literals](#datetime-literals).

The following type casts are supported:

Datetime as boolean

All datetime values cast to boolean `true`.

Datetime as string

A datetime value casts to a string compatible with [datetime literal](#datetime-literals) notation.

```tweakflow
> time.epoch as string
"1970-01-01T00:00:00Z@UTC"
```

Casting datetimes to string should only be done for non-functional purposes like data-inspection, debugging or logging. The standard library offers [formatters](/modules/std.html#time-formatter) to to convert datetime values to strings in a controlled output format.

Datetime as dict

A datetime value casts to a dict that contains all of its fields together with day of week, day of year, and week of year information. Supposing `x` is the datetime to cast, and `time` is the library from the standard module, the dict is equivalent to the following definition:

```tweakflow
{
  :year              time.year(x),
  :month             time.month(x),
  :day_of_month      time.day_of_month(x),
  :hour              time.hour(x),
  :minute            time.minute(x),
  :second            time.second(x),
  :nano_of_second    time.nano_of_second(x),
  :day_of_year       time.day_of_year(x),
  :day_of_week       time.day_of_week(x)
  :week_of_year      time.week_of_year(x),
  :offset_seconds    time.offset_seconds(x),
  :zone              time.zone(x)
}
```

For example:

```tweakflow
> time.epoch as dict
{
  :month 1,
  :day_of_year 1,
  :hour 0,
  :zone "UTC",
  :nano_of_second 0,
  :offset_seconds 0,
  :second 0,
  :minute 0,
  :day_of_week 4,
  :week_of_year 1,
  :day_of_month 1,
  :year 1970
}

> 2017-07-23T23:12:32.298+02:00@Europe/Berlin as dict
{
  :month 7,
  :day_of_year 204,
  :hour 23,
  :zone "Europe/Berlin",
  :nano_of_second 298000000,
  :offset_seconds 7200,
  :second 32,
  :minute 12,
  :day_of_week 7,
  :week_of_year 29,
  :day_of_month 23,
  :year 2017
}
```

### List

The `list` type holds a finite sequence of values in a defined order. It is equivalent to array types in other languages. They are indexed by long values, starting at index 0. Lists are internally indexed by integers and have a capacity limit of 2^31 = 2.147.483.648 elements.

Lists are notated as [list literals](#list-literals). The following type casts are supported:

List as boolean

An empty list `[]` converts to `false`. Any other list value converts to `true`.

List as dict

Lists are converted as sequences of key-value pairs. `["a", 1, "b", 2]` is converted to `{:a 1, :b 2}`. Items in key position are cast to strings. The conversion proceeds left to right, with any duplicate keys being replaced with the rightmost occurrence. If the list has an odd number of items, an error is thrown. If any of the keys is `nil` or cannot be cast to string, an error is thrown.

```tweakflow
> ["a", 1, "b", 2, "c", 3] as dict
{
  :a 1,
  :b 2,
  :c 3
}

> [1, 2, 3, 4] as dict
{
  :`1` 2,
  :`3` 4
}

> [] as dict
{}

> ["a", "b", "a", "d"] as dict # rightmost key "a" takes precedence
{
  :a "d"
}

> ["a", nil, "b", 1] as dict # nil values are allowed
{
  :a nil,
  :b 1
}

> ["a", "b", nil, "d"] as dict # nil keys are not allowed
ERROR:
  code: CAST_ERROR
  message: Cannot cast list to dict with nil key at index: 2
  ...

> [1, 2, 3] as dict
ERROR:
  code: CAST_ERROR
  message: Cannot cast list with odd number of items to dict
  ...

```

### Dict

The `dict` type is an associative structure that maps string keys are to arbitrary values. Dicts do not support `nil` keys, but `nil` values are permitted. The order of keys in a dict is undefined. Dicts are notated as [dict literals](#dict-literals).

The following type casts are supported:

Dict as boolean

The empty dict `{}` converts to `false`. Any other dict value converts to `true`.

Dict as list

Dicts are converted to lists as a sequence of key-value pairs. An empty dict gives an empty list. Keys and values appear in pairs, but the order of the pairs is not defined.

```tweakflow
> {} as list
[]

> {:a "foo", :b "bar"} as list
["a", "foo", "b", "bar"]

> {:a 1, :b 2}  as list
["a", 1, "b", 2]

> {:b 1, :a 2}  as list
["a", 2, "b", 1]
```

### Function

The `function` type holds callable functions. There is only one data type for functions. It encompasses functions of all signatures.

Functions are notated as [function literals](#function-literals). The body is either an expression that evaluates to the function's return value, or a structure specifying the Java class that is implementing the function.

When a function is invoked, all arguments are implicitly cast to the declared parameter types.

If a function body is an expression, it is evaluated to the return value when the function is called. The return value is implicitly cast to the declared return value of the function.

```tweakflow
# A function with no parameters, returning a constant of any type
> f: () -> 1

function
> f()
1

# A function taking two strings and returning a string
> f: (string x, string y) -> string    x .. y
function

> f("John", "Doe")
"JohnDoe"

# A function taking a list and returning a list
> f: (list xs) -> list    data.map(xs, (_, i) -> xs[data.size(xs)-1-i])
function

> f([1, 2, 3])
[3, 2, 1]

# A function taking two doubles, each having a default value, returning a double
> f: (double x=1.0, double y=0.0) -> double    x+y
function

> f(3, 4)
7.0

> f()
1.0

> f(0)
0.0

> f(x: 2, y: 3)
5.0

> f(y: 7)
8.0

# reminder: string cast to list gives the characters in a list
> "hello" as list
["h", "e", "l", "l", "o"]

# the returned value, a string, is cast to the declared return type, a list
> f: (string x, string y) -> list  x..y
function

> f("Foo", "Bar")
["F", "o", "o", "B", "a", "r"]

```

The following type casts are supported:

Function as boolean

All function values cast to boolean `true`.

The signature of a function can be inspected calling [fun.signature](/modules/std.html#fun-signature) from the standard library.

```tweakflow
> f: (double x=1.0, double y=0.0) -> double    x+y
function

> fun.signature(f)
{
  :return_type "double",
  :parameters [{
    :name "x",
    :index 0,
    :default_value 1.0,
    :declared_type "double"
  }, {
    :name "y",
    :index 1,
    :default_value 0.0,
    :declared_type "double"
  }]
}
```

#### Functions in Java

Instead of a body expression, tweakflow functions can specify a Java class that implements the function. The notation is the keyword `via` followed by a map literal containing the key `:class` which points to a Java class. The Java class must implement the tag interface [UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/UserFunction.java), as well as a exactly one of the following interfaces governing parameter passing.

| Interface                                | Purpose                                  |
| ---------------------------------------- | ---------------------------------------- |
| [Arity0UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Arity0UserFunction.java) | Implements zero-parameter functions.     |
| [Arity1UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Arity1UserFunction.java) | Implements single-parameter functions.   |
| [Arity2UserFunction](https://github.com/twineworks/tweakflow/blob/releases/0.0.1/src/main/java/com/twineworks/tweakflow/lang/values/Arity2UserFunction.java) | Implements functions with two parameters |
| [Arity3UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Arity3UserFunction.java) | Implements functions with three paramters. |
| [Arity4UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Arity4UserFunction.java) | Implements functions with four parameters |
| [ArityNUserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/ArityNUserFunction.java) | Implements functions with any number of parameters. Arguments are passed as an array of values. |

For example, the inner class [com.twineworks.tweakflow.std.Strings$concat](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/std/Strings.java#L43) implements the `strings.concat` function of the standard library.

```tweakflow
> f: (list xs) -> string via {:class "com.twineworks.tweakflow.std.Strings$concat"}
function

> f(["Foo", "Bar", "Baz"])
"FooBarBaz"
```
It is worth noting that return values coming from java implementations of functions are still cast to return types as per the function declaration in tweakflow.

```tweakflow
> f: (string x) -> long via {:class "com.twineworks.tweakflow.std.Strings$length"}
function

> g: (string x) -> string via {:class "com.twineworks.tweakflow.std.Strings$length"}
function

> f("foo")
3

> g("foo")
"3"
```

See the standard library functions in [std](https://github.com/twineworks/tweakflow/tree/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/std) for examples of functions implemented in Java.

### Void

The `void` type is a type that has `nil` as its only value. The `nil` value reports `void` as its type, even though it is a valid member of any type.

The only `void` value `nil` casts successfully to any type, and remains `nil`.

### Any

The `any` type is a type is not concrete type of its own, but it is used to indicate the possibility of any type being present in the given context. The `any` type is used as a default in type declarations for variables, parameters and return types.

## Expressions

Tweakflow expressions evaluate to values. All data types can be written as literals. Tweakflow also has function calls, conditionals, list comprehensions, pattern matching, type casts, and several operators for many common computations.

### Nil

The `nil` value is written as simply `nil`. Semantically, a `nil` value indicates the absence of a value. The `nil` value is special because it is a valid member of all data types. It casts to any type successfully as `nil`.

### Value literals

All tweakflow data types have a literal notation outlined in their respective section under [data types](#data-types).

### Container access

List and dict contents are accessed using square brackets. Tweakflow supports traversing through deep structure by giving several keys at a time. Splat keys allow traversing paths given by a list at runtime.

The formal structure of container access expressions is as follows:

```text
containerAccess
  : expression'['containerAccessKeySequence']'
  ;

containerAccessKeySequence
  : ((expression | splat)) (',' (expression | splat))*
  ;

splat
  : '...' expression
  ;
```

#### List access

Indexes supplied for a list are automatically cast to long values.  If the given index does not exist in the list, the value of the access expression is `nil`. Accessing a `nil` list yields `nil`.

```tweakflow
> items: ["a", "b", "c"]
["a", "b", "c"]

> items[0]
"a"

> items[1]
"b"

> items[2]
"c"

> items["2"] # list access indexes are cast to int
"c"

> items[3]
nil

> items[-1]
nil

> items[nil]
nil

> nil[0] # accessing nil yields nil
nil
```

#### Dict access

Dicts are indexed with strings. Keys are automatically cast to strings. If a given key does not exist, the value of the access expression is `nil`. Accessing `nil` yields `nil`.

```tweakflow
> bag: {:a "alpha", :b "beta", "1" "one", "2" "two"}
{
  :a "alpha",
  :b "beta",
  :`1` "one",
  :`2` "two"
}

> bag[:a]
"alpha"

> bag[:b]
"beta"

> bag[:c]
nil

> bag[1]  # key is cast to string automatically
"one"

> bag[2] # key is cast to string automatically
"two"

> bag[3] # key is cast to string automatically
nil

> bag[nil]
nil

> nil[:key] # accessing a nil dict yields nil
nil
```

#### Container traversal

Container access expressions can be chained to access nested data.

```tweakflow
> \e
story: {
  :name "A Study in Scarlet",
  :adaptations [
    {:year 1914, :media "silent film"},
    {:year 1968, :media "television series"}
  ]
}
\e

{
  :adaptations [{
    :media "silent film",
    :year 1914
  }, {
    :media "television series",
    :year 1968
  }],
  :name "A Study in Scarlet"
}

> story[:adaptations]
[{
  :media "silent film",
  :year 1914
}, {
  :media "television series",
  :year 1968
}]

> story[:adaptations][1]
{
  :media "television series",
  :year 1968
}
> story[:adaptations][1][:media]
"television series"
```

Tweakflow supports placing the keys of chained access inside a single set of square brackets. The semantics are exactly the same as chaining container access.

```tweakflow
> story[:adaptations][1][:media]
"television series"

> story[:adaptations, 1, :media]
"television series"
```

It is worth noting that if the traversal yields `nil` at any intermediate point, the result is `nil`, which is consistent with `nil[x]` being `nil`.

```tweakflow
> story[:adaptations][4][:media] # there is no adaptation at index 4
nil

> story[:adaptations, 4, :media]
nil
```

The list of keys in the traversal form can be interspersed with splat expressions. The splat expression must be a list containing the keys to access. Each splat expression is expanded, and concatenated with any existing items just as in [list literals](#list).

```tweakflow
> path: [:adaptations, 1, :media]
["adaptations", 1, "media"]

> story[...path]
"television series"

> story[:adaptations, ...[0, :year]]
1914

> story[...[:adaptations], ...[1], ...[:year]]
1968
```

### Function calls

Function calls are notated by giving the function and following with round parentheses containing any arguments.

Function calls have the following formal structure:

```text
functionCall
  :expression'('args')'
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
  : '...' expression
  ;
```

A basic example defining a function, and calling it immediately:

```tweakflow
> ((x) -> x*x)(2) # call with argument x=2
4
```

You can reference the function and call it:

```tweakflow
> f: (x) -> x*x  # define a function and place it in f
function

> f(2) # call function f
4

> strings.length("foo") # call standard library function strings.length with one argument "foo"
3
```

 For the purposes of further discussion, let's define function `f` as:

```tweakflow
f: (long id = 0, string name = "n/a") -> string id .. "-" .. name
```

Above function has parameters `id` of type long and `name` of type string. Both have non-nil default values.

When calling a function it is possible to specify arguments values using position, name, or a mix of both.

#### Positional arguments

Arguments given by position just list the values in parameter order and are separated by comma. The following call passes `42` as `id` and `"test"` as `name`.

```tweakflow
> f(42, "test")
"42-test"
```

Passing more than the declared number of positional arguments is an error.

```tweakflow
> f(42, "test", "too much")
ERROR:
  code: UNEXPECTED_ARGUMENT
  message: cannot call function with 3 arguments
```

Passing less than the declared number of positional arguments results in the missing arguments being supplied through default values of the missing parameters. All parameters of a function have the default value `nil` unless specified otherwise in the function definition.

```tweakflow
> f(12)
"12-n/a"

> f()
"0-n/a"

> g: (x) -> x # default value of x is nil
function

> g(1)
1

> g() # x attains its default value nil
nil
```

#### Named arguments

When calling function, you can also pass arguments by name. Arguments given by name are listed in pairs of names and values separated by commas. The following call passes `42` as `id` and `"test"`  as `name` again, but uses named arguments this time. The order of named arguments does not matter.

```tweakflow
> f(id: 42, name: "test")
"42-test"

> f(name: "test", id: 42)
"42-test"
```

Omitted arguments are assigned their default parameter value.

```tweakflow
> f(id: 42)
"42-n/a"

> f(name: "test")
"0-test"
```

It is an error to supply argument names not present in function parameters:

```tweakflow
> f(id: 42, name: "foo", country: "US")
ERROR:
  code: UNEXPECTED_ARGUMENT
  message: Function does not have parameter named: country
```

#### Mixed positional and named arguments

Position and named arguments can be mixed in a single call. Positional arguments are listed first. Named arguments follow.

The following call passes `42` as `id` and `"test"`  as `name`. It mixes positional and named arguments.

```tweakflow
> f(42, name: "test")
"42-test"
```

It is an error to supply any positional arguments after named arguments.

```tweakflow
> f(id: 42, "test") # error, positional arguments cannot follow named arguments
ERROR:
  code: UNEXPECTED_ARGUMENT
  message: Positional argument cannot follow named arguments.
```

It is possible to specify a parameter in both positional and named arguments. The rightmost specified value is used.

```tweakflow
> f(42, "test", id: 7)
"7-test"

> f(42, "test", id: 7, id: 8)
"8-test"
```

Mixed style arguments are a useful idiom when a function exposes a set of leading arguments that have intuitive order, but allows for a set of less common option-style parameters to configure details.

The function [add_period](/modules/std.html#time-add_period) from the standard library for example:

```tweakflow
> time.add_period(time.epoch, years: 1000)
2970-01-01T00:00:00Z@UTC

> time.add_period(time.epoch, days: 2)
1970-01-03T00:00:00Z@UTC
```

#### Splat arguments

Both positional arguments and named arguments can be supplied via a splat expression. This offers a notational convenience for cases where arguments have been collected into a list or dict.

Whenever positional arguments are allowed, and a splat expression evaluates to a list, the items from the list are used as positional arguments in order.

```tweakflow
> args: [42, "name"]
[42, "name"]

> f(...args) # splat positional arguments
"42-name"
```

Positional arguments can be interspersed with splats. The resulting arguments are concatenated left to right:

```tweakflow
> f(42, ...["name"])
"42-name"

> f(...[42], "name")
"42-name"

> f(...[42], ...["name"])
"42-name"
```

Whenever named arguments are allowed, and a splat expression evaluates to a dict, the items from the dict are used as named arguments.

Below example supplies the `id` and `name` named arguments.

```tweakflow
> person: {:id 42, :name "test"}
{
  :name "test",
  :id 42
}
> f(...person) # splat named arguments
"42-test"
```

Named arguments can be interspersed with splats. The resulting arguments dict is then merged left to right, with rightmost keys taking precedence in case of duplicates. Below example again effectively passes `42` as `id` and `"test"` as `name`.

```tweakflow
> person: {:id 0, :name "test"}
{
  :name "test",
  :id 0
}
> f(...person, id: 42)
"42-test"
```

Splats can be mixed as long as positional splats come first.

```tweakflow
> f(...[42, "testing"], ...{:name "foo"})
"42-foo"

> f(...{:name "foo"}, ...[42, "testing"]) # no positional args allowed after named args
ERROR:
  code: UNEXPECTED_ARGUMENT
  message: List splat provides positional arguments and cannot follow named arguments.
```

#### Argument casting

Arguments given in function calls are automatically cast to the declared parameter type:

```tweakflow
> f("3", 9837) # casts the first argument to long, and the second argument to string
"3-9837"

> f("abc", "def") # cannot cast first argument to long
ERROR:
  code: CAST_ERROR
  message: Cannot cast abc to long
```

#### Return value casting

Every function declares a return type. It is `any` by default. Every value a function returns is cast to the declared return type implicitly. If the return type of a function is `any`, the cast is not performed.

```tweakflow
> sum: (long x, long y) -> long        x+y
function

> sum_d: (long x, long y) -> double    x+y
function

> sum_s: (long x, long y) -> string    x+y
function

> id: (x) -> x # return type is default: any
function

> sum(1, 2)
3

> sum_d(1, 2)
3.0

> sum_s(1, 2)
"3"

> id([])
[]

> id("foo")
"foo"
```

### Partial function application
You can create a new function from an existing function by binding values to a subset of the original function's parameters. This process is called [partial function application](https://en.wikipedia.org/wiki/Partial_application).

The resulting function's signature contains only parameters that were left unbound.

The syntax is as follows:

```text
partialApplication
  : expression '(' partialArgs ')'
  ;

partialArgs
  : (namedPartialArg) (',' (namedPartialArg))*
  ;

namedPartialArg
  : identifier '=' expression
  ;
```

As an example consider a function that checks whether an list contains only long values.
The function is created by binding a predicate function to the parameter `p` of [data.all?](/modules/std.html#data-all?).

```tweakflow
> fun.signature(data.all?)
{
  :return_type "boolean",
  :parameters [{
    :name "xs",
    :index 0,
    :default_value nil,
    :declared_type "list"
  }, {
    :name "p",
    :index 1,
    :default_value nil,
    :declared_type "function"
  }]
}

> all_longs?: data.all?(p=(x) -> x is long)
function

> fun.signature(all_longs?)
{
  :return_type "boolean",
  :parameters [{
    :name "xs",
    :index 0,
    :default_value nil,
    :declared_type "list"
  }]
}

> all_longs?([1, 2, 3])
true

> all_longs?([1, "a", 3])
false
```

### Call chaining

A computation might consist of a linear series of function calls feeding their output into the next function's input. Tweakflow supports a special syntax for that situation.

The syntax is as follows:

```text
callChain
  : '->>' '('threadArg')' expression (',' expression)*
  ;

threadArg
  : expression
  ;
```

The symbol `->>` is a mnemonic for a threading needle. `threadArg` is passed into a list of expressions, each expression in the list must evaluate to a callable function. Each function is called with a single argument in order. The return value of each function becomes the first argument of the next function. The return value of the last function becomes the value of the expression as a whole.

As an example, consider the normalization of a string value representing a product code: the string must be cleaned of whitespace, any existing dashes must be removed, new dashes must be included to create groups of four characters, and finally all characters must be upper case.

```tweakflow
> \e
normalize: (string pn) ->
  ->> (pn)
	  # remove whitespace
      (x) -> regex.replacing('\s', "")(x),
      # remove any dashes
      (x) -> strings.replace(x, "-", ""),
      # split to a list of blocks of up to 4 chars
      (x) -> regex.splitting('(?<=\G.{4})')(x),
      # place dashes between blocks converting to single string
      (xs) -> strings.join(xs, "-"),
      # upper case all characters
      strings.upper_case
\e
function

> normalize("39 hd-sd-asdi3437")
"39HD-SDAS-DI34-37"
```

### Local variables

Local variables are useful as named temporary results. The `let` expression defines a set of variables that are bound in a newly created local scope.

The formal syntax is as follows:

```text
let
  :'let' '{' (varDef ';')* '}' expression
  ;

varDef
  : dataType? identifier ':' expression
  ;
```

Examples:

```tweakflow
> let {a: 1; b: 2;} a + b
3

> \e
  can_vote: (datetime born, datetime at) ->
    let {
      age: time.years_between(born, at);
      is_eighteen: age >= 18;
    }
    is_eighteen
\e
function

> can_vote(born: 1981-08-16T, at: 1998-11-02T)
false

> can_vote(born: 1981-08-16T, at: 1999-11-02T)
true

> can_vote(born: 1981-08-16T, at: 2016-11-02T)
true

```

Local variables shadow any existing variables:

```tweakflow
> \e
let {
  x: "foo";
  y: let {
       x: "bar";
     }
     x; # "bar"
}
x .. y # "foo".."bar"
\e
"foobar"
```

### Conditional evaluation

Conditional evaluation is done using a traditional `if` construct.

```text
'if' condition 'then'? then_expression 'else'? else_expression
```

The `condition` expression is evaluated and cast to boolean. If the condition evalautes to `true`, the `then_expression` is evaluated and is the result of the expression. If the condition evaluates to `false` or `nil`, the `else_expression` is evaluated and is the result of the expression.  The `then` and `else` keywords are optional.

Some examples:

```tweakflow
> if true then 1 else 2
1

> f: (string x) -> if strings.length(x) > 2 then "long" else "short"
function

> f("hello")
"long"

> f("hi")
"short"

> \e
greeting: (string language) ->
  if language == "en" then "Good afternoon"
  if language == "de" then "Guten Tag"
  if language == "es" then "Hola"
  if language == "zh" then "你好"
  if language == "hi" then "नमस्ते"
  else "Hello"
\e
function
> greeting("de")
"Guten Tag"

> greeting("es")
"Hola"

> greeting()
"Hello"
```

### Default values

Default values notation is a shorthand for replacing `nil` values with non-nil defaults.

```text
expression 'default' default_expression
```

It is semantically identical to:

```text
if expression != nil then expression else default_expression
```

Given customer records, the following function creates a greeting line:

```tweakflow
> greeting: (dict customer) -> "Dear "..(customer[:name] default "customer")
function

> greeting({:id 723, :name "Jane Doe", :type "user"})
"Dear Jane Doe"

> greeting({:id 0, :type "admin"})
"Dear customer"
```

### List comprehensions

Tweakflow supports list comprehensions allowing to generate, transform, combine and filter lists.

```text
listComprehension
  : 'for' forHead ',' expression
  ;

forHead
  : generator (',' (generator | varDef | filter))*
  ;

generator
  : dataType? identifier '<-' expression
  ;

varDef
  : dataType? identifier ':' expression
  ;

filter
  : expression
  ;
```

A list comprehension uses generators to define variables that loop over list items. They nest in order if more than one generator is present.

Create a list of coordinates from given axes:

```tweakflow
> \e
for
  x <- ["a", "b", "c"],
  y <- [1, 2, 3, 4, 5, 6],
  x .. y
\e
["a1", "a2", "a3", "a4", "a5", "a6", "b1", "b2", "b3", "b4", "b5", "b6", "c1", "c2", "c3", "c4", "c5", "c6"]
```

Variable definitions in list comprehensions create helper variables. They are in scope for all subsequent expressions in the list comprehension.

```tweakflow
> \e
for
  x  <- data.range(1, 3),
  y  <- data.range(x, 3),
  p: x*y,
  "#{x} * #{y} = #{p}"
\e
["1 * 1 = 1", "1 * 2 = 2", "1 * 3 = 3", "2 * 2 = 4", "2 * 3 = 6", "3 * 3 = 9"]
```

Free-standing expressions act as filters. They are evaluated and cast to boolean. If they evaluate to `true`, the current entry is part of the result list, if they evaluate to `false` or `nil`, the current element is omitted.

Create a list of [pythagorean triples](https://en.wikipedia.org/wiki/Pythagorean_triple) trying sides up to the size of 15.

```tweakflow
> \e
for
  a <- data.range(1, 15),
  b <- data.range(a, 15),
  c: math.sqrt(a*a + b*b),
  (c as long) == c, # filter: only pass if is c an integer
  [a, b, c as long]
\e
[[3, 4, 5], [5, 12, 13], [6, 8, 10], [8, 15, 17], [9, 12, 15]]
```

Above example loops over `a` going from 1 to 15, and `b` going from `a` to `15`, calculates `c`, and filters out any non-integer `c` values. If `c` happens to be an integer, the triple `[a, b, c]` is included in the result list.

### Pattern Matching

Tweakflow supports matching on value, type and structure of an input value, additionally supporting a guard expression before a match is accepted.

The formal syntax is as follows:

```text
match:
  'match' expression matchBody
  ;

matchBody
  : matchLine (',' matchLine)*
  ;

matchLine
  : matchPattern (',' matchGuard)? '->' expression
  | 'default' '->'  expression
  ;

matchGuard
  : expression
  ;
```

A match expression consists of the `match` keyword, the value to match, an one or more match lines. A match line consists of a pattern, an optional guard expression, and a result expression. Alternatively, a match line can be the `default` line, which provides the default evaluation value in case no other lines match. If a match expression has a default line, it must appear last.

The match expression is evaluated by testing the value to match against each non-default match line in order. If the pattern of the line matches and there is no guard expression, the result expression is evaluated, and becomes the evaluation value of the whole match. If there is a match guard expression, it is evaluated first and cast to boolean. If it evaluates to `true` the match evaluates to the line's result expression. If the guard expression evaluates to `false` or `nil`,  the match line does not match, and the algorithm proceeds to test the next match line. After all match lines are tested, and none matches, there are two possibilities: If there is a `default` line, the match evaluates to the default value. If there is no `default` line, the match evaluates to `nil`.

The patterns available for matching include existence matches, value matches, predicate matches, type matches and structural matches.

#### Existence and capturing patterns

Existence matches are the simplest form of match. They match any value including `nil`. An existence match is indicated by the `@` operator. If that operator is followed by an identifier, it becomes a capturing match, binding the identifier to the matched value in the guard and result expressions of the line. Formally the syntax is as follows:

```text
matchPattern
  : '@' identifier?
  ;
```

An example:

```tweakflow
> \e
f: (long x) ->
  match x
    @ -> true # always matches
\e
function
> f(0)
true

> f(1)
true

> f(nil)
true
```

Existence matches are not very useful for matching simple values, but they are useful when nested in list or dict patterns to assert element existence and extract element values from these structures.

```tweakflow
> \e
pair?: (list xs) ->
  match xs
    [@, @]  -> true,
    default -> false
\e
function
> pair?([1, 2])
true

> pair?([1, 2, 3])
false

> pair?(nil)
false

> \e
sequence_pair?: (list xs) ->
  match xs
#   capture   guard       result
    [@a, @b], a+1 == b -> true,
    default -> false
\e
function
> sequence_pair?([1, 2])
true

> sequence_pair?([2, 4])
false
```

#### Value patterns

A value pattern compares against a concrete value. The comparison is done using the `==` operator. The syntax is:

```text
matchPattern
  : expression capture?
  ;

capture
  : '@' identifier?
  ;
```

An optional capture pattern is allowed after the expression, to capture the matched value. If the capture does not specify an identifier, it has no effect.

```tweakflow
> \e
low_prime?: (long x) ->
  match x
    2 -> true,
    3 -> true,
    5 -> true,
    7 -> true,
    default -> false
\e
function
> low_prime?(1)
false

> low_prime?(2)
true

> low_prime?(3)
true

> low_prime?(4)
false

> low_prime?(5)
true

> low_prime?(nil)
false
```

Value patterns have a special case: functions. Functions never compare as equal. Therefore a value pattern that compares against a function would never match. Instead, tweakflow uses function values in patterns as predicates.

#### Predicate patterns

Predicate patterns are syntactically identical to value patterns, since they are merely a special case of the match expression evaluating to a function.

```text
matchPattern
  : expression capture?
  ;

capture
  : '@' identifier?
  ;
```

If the pattern expression evaluates to a function, it is treated as a predicate: the function is called with the matched value as first argument, and the result is cast to boolean. If it evaluates to `true`, the pattern matches, if it evaluates to `false` or `nil`, the pattern does not match.

```tweakflow
> div_by_4?: (long x) -> x % 4 == 0
function

> div_by_400?: (long x) -> x % 400 == 0
function

> div_by_100?: (long x) -> x % 100 == 0
function

> \e
leap_year?: (long x) ->
  match x
    div_by_400? -> true,
    div_by_100? -> false,
    div_by_4?   -> true,
    default     -> false
\e
function

> leap_year?(1900)
false

> leap_year?(1904)
true

> leap_year?(2000)
true

> leap_year?(2004)
true

> leap_year?(2016)
true

> leap_year?(2017)
false
```

#### Type patterns

Type patterns match on the data type of the matched value. Their syntax is as follows:

```text
matchPattern
  : dataType capture?
  ;

capture
  : '@' identifier?
  ;
```

The pattern matches only if the matched value is of the given type. The `nil` value only matches the `void` data type. Any non-nil value matches the `any` type.

As an example, consider the `int?` function, which returns true if the argument is a whole number given as long, double, or as a string.

```tweakflow
> \e
int?: (x) ->
  match x
    long                      -> true,
    double, (x as long) == x  -> true,
    string                    -> try int?(x as double) catch false,
    default                   -> false
\e
function
> int?(1)
true

> int?(1.0)
true

> int?(1.5)
false

> int?("2")
true

> int?("2e3")  # 2000
true

> int?("-2e3") # -2000
true

> int?("2e-3") # 0.002
false

> int?("2m")   # does not parse as double
false

> int?(nil)
false
```

#### Full list patterns

Full list patterns match a list in its entirety. It is a sequence of patterns corresponding to list items. The syntax for full list patterns is a non-empty list of match patterns of any kind, separated by comma:

```text
matchPattern
  : '[' (matchPattern ',') * matchPattern ']' capture?
  ;

capture
  : '@' identifier?
  ;
```

Each pattern in the pattern list must match the items of the matched value in order. The optional capture contains the entire matched list.

```tweakflow
> \e
  num?: (x) ->
    (x is long) ||
    (x is double && !math.NaN?(x) && math.abs(x) != Infinity)
\e
function

> \e
vector2d?: (list xs) ->
  match xs
    # match 2-element list, use num? as predicate for each item
    [num?, num?] -> true,
    default -> false
\e
function
> vector2d?([1, 2])
true

> vector2d?(["a", "b"])
false

> vector2d?([8, 2, 2.0])
false

> vector2d?([8.0, 2.0])
true

> vector2d?([nil, nil])
false

> vector2d?(nil)
false
```

#### Head list patterns

A head list pattern matches the beginning of a list with item patterns, optionally also capturing the tail.

The syntax for head list patterns is a list beginning with match patterns of any kind, separated by comma, then followed by a splat expression representing the tail:

```text
matchPattern
  : '[' (matchPattern ',') * splatCapture ']' capture?
  ;

splatCapture
  : '@' '...' identifier?
  ;

capture
  : '@' identifier?
  ;
```

Each pattern in the pattern list must match the items of the matched value in order, after which follows a tail of zero or more items. The tail can be captured into a variable. The optional final capture contains the entire matched list.

The following function recursively checks whether the argument is a list of pairs of keys and values. All key positions must contain strings beginning with the letter `"a"`.

```tweakflow
> \e
valid_list?: (list xs) ->
  match xs
    [] -> true,
    [string @key, @, @...tail], strings.starts_with?(key, "a") -> valid_list?(tail),
    default -> false
\e
function
> valid_list?(["adam", 2, "abner", 7])
true

> valid_list?(["adam", 2, "eve", 7])
false

> valid_list?([1, "a"])
false

> valid_list?(["a1", nil, "a2", nil, "a3", "hello"])
true

> valid_list?(nil)
false
```

#### Tail list patterns

A tail list pattern matches the end of a list with item patterns, optionally also capturing the initial part of the list.

The syntax for tail list patterns is a list beginning with a splat expression representing the initial list, followed by a sequence of match patterns of any kind, separated by comma:

```text
matchPattern
  : '[' splatCapture ',' (matchPattern ',') * matchPattern ']' capture?
  ;

splatCapture
  : '@' '...' identifier?
  ;

capture
  : '@' identifier?
  ;
```

The initial splat capture matches zero or more items, after which each pattern in the pattern list must match the items of the matched value in order until the end of the list. The ends of the pattern list and the checked value must coincide. The initial part of the list can be captured into a variable. The optional final capture contains the entire matched list.

The following function checks whether a list's last element is a non-nil datetime.

```tweakflow
> \e
ends_in_datetime?: (list xs) ->
  match xs
    [@..., datetime] -> true,
    default -> false
\e

> ends_in_datetime?(["a", "b"])
false

> ends_in_datetime?([])
false

> ends_in_datetime?([2017-02-24T, 2017-02-25T])
true

> ends_in_datetime?(nil)
false

> ends_in_datetime?([1, nil])
false
```

#### Head-and-tail list patterns

A head-and-tail list pattern matches the beginning and end of a list with item patterns, optionally also capturing the middle part of the list.

The syntax for head-and-tail list pattern is a list beginning with a sequence of patterns of any kind,  a splat expression representing the middle of the list, followed again by a sequence of match patterns of any kind, separated by comma:

```text
matchPattern
  : '[' (matchPattern ',') + splatCapture ',' (matchPattern ',')* matchPattern ']' capture?
  ;

splatCapture
  : '@' '...' identifier?
  ;

capture
  : '@' identifier?
  ;
```

The initial patterns must match the initial items in the list, the splat capture matches zero or more items following that, after which each pattern in the pattern list must match the items of the matched value in order until the end of the list. The middle part of the list can be captured into a variable. The optional final capture contains the entire matched list.

The following function checks that a list starts with a non-nil string and ends with a non-nil datetime, with zero or more non-nil longs in between, which must all be between 0 and 100 inclusively.

```tweakflow
> \e
measures?: (list xs) ->
  match xs
    [string, @...nums, datetime], data.all?(nums, (x) -> x is long && x >= 0 && x <= 100) -> true,
    default -> false
\e
function
> measures?([:p1, 0, 2, 3, 4, 99, 2017-04-22T])
true

> measures?([:p2, 99, 2015-02-11T])
true

> measures?([:p3, 2016-02-11T])
true

> measures?([2016-02-11T])
false

> measures?([])
false

> measures?([:p4, 201, 2017-04-22T]) # number out of range
false
```

#### Full dict patterns

Full dict patterns match dictionaries as a whole. All expected keys are specified by the patterns, and any matched dict must have the given keys and only the given keys.

```text
matchPattern
  : '{' ((stringConstant matchPattern) ',' )* (stringConstant matchPattern) '}' capture?
  ;

capture
  : '@' identifier?
  ;
```

All keys are specified as constants, and their values must match the supplied value patterns. If a dict is missing any of the pattern keys, or contains more than the given pattern keys, it does not match. An optional final capture matches the entire matched dict.

The following function tests whether the supplied dict is a vector with non-nil double coordinates x and y. Only those two keys are allowed.

```tweakflow
> \e
vector_dict?: (dict v) ->
  match v
    {:x double, :y double} -> true,
    default -> false
\e
function
> vector_dict?({:x 10, :y 20})
false

> vector_dict?({:x 10.0, :y 20.0})
true

> vector_dict?({:x 10.0, :y nil})
false

> vector_dict?({:x 10.0, :y 20.0, :z 14.9})
false

> vector_dict?({:x 10.0})
false

> vector_dict?({:a "one" :b "two"})
false

> vector_dict?(nil)
false
```

#### Partial dict patterns

Partial dict patterns match a subset of a dictionary's keys. Any remaining keys can be captured into a variable.  The syntax uses pairs of constant keys and value patterns. A splat capture is used to capture any keys and values not specified by the patterns. There can be only one splat capture, but its position can be freely chosen.

```text
matchPattern
  :| '{' (((stringConstant matchPattern)|splatCapture) ',' )* ((stringConstant matchPattern)|splatCapture) '}' capture?
  ;

splatCapture
  : '@' '...' identifier?
  ;

capture
  : '@' identifier?
  ;
```

All keys are specified as constants, and their values must match the supplied value patterns. If a dict is missing any of the keys, it does not match. Additional keys are allowed. The optional final capture matches the entire matched dict.

The following function checks if the given dict contains a "name" key with a string, and a "born" key with a datetime. Any additional keys are ignored.

```tweakflow
> \e
person?: (dict x) ->
  match x
    {:name string, :born datetime, @...} -> true,
    default -> false
\e
function
> person?({:name "Mark Twain", :born 1835-11-30T})
true

> person?({:name "Mark Twain", :born 1835-11-30T, :profession "author"})
true

> person?({:name "Mark Twain", :profession "author"})
false

> person?({:x 1, :y 2})
false

> person?(nil)
false
```

The following function checks if the given dict contains a "name" key with a string, and a "born" key with a datetime. In addition, at least a key "job", or "profession" must be present.

```tweakflow
> \e
person?: (dict x) ->
  match x
    {:name string, :born datetime, @...rest}, (rest[:job] is string || rest[:profession] is string) -> true,
    default -> false
\e
function
> person?({:name "Mark Twain", :born 1835-11-30T})
false

> person?({:name "Mark Twain", :born 1835-11-30T, :profession "author"})
true

> person?({:name "Mark Twain" :profession "author"})
false

> person?({:x 1, :y 2})
false

> person?(nil)
false
```

#### Nesting patterns

List and dict patterns nest naturally. The following function returns the most recent of an author's books.

```tweakflow
> mark_twain: {:profession "author", :books ["The Gilded Age: A Tale of Today", "Personal Recollections of Joan of Arc"]}
{
  :books ["The Gilded Age: A Tale of Today", "Personal Recollections of Joan of Arc"],
  :profession "author"
}

> \e
latest_book: (dict person) ->
  match person
    {:profession "author", :books [@..., @latest_book]} -> latest_book,
    default -> nil
\e
function

> latest_book(mark_twain)
"Personal Recollections of Joan of Arc"
```

The ability to capture a whole matching pattern can be useful when nesting. The following example uses a list pattern to extract the latest book, while also capturing the whole books list into a variable.

```tweakflow
> \e
latest_book_with_nr: (dict person) ->
  match person
    {
     :profession "author",
     :books [@..., @latest_book] @books
    } ->
      "The latest book is book nr. ".. data.size(books) .. ": " .. latest_book,
    default -> nil
\e
function

> latest_book_with_nr(mark_twain)
"The latest book is book nr. 2: Personal Recollections of Joan of Arc"
```

### Errors

Tweakflow supports throwing arbitrary values as errors. If an error is thrown inside the try branch of a try/catch block, it is caught and the error value, as well as the stack trace can be inspected, handled, and re-thrown if necessary.

#### Throwing errors

The syntax for throwing an error is as follows:

```text
'throw' expression
```

As an example, consider the following add function, which throws on binary overflow/underflow when adding longs.

```tweakflow
> \e
add: (long x=0, long y=0) ->
  let {
    long sum: x + y;
  }
  if x > 0 and y > 0 and sum <= 0
    throw {:code "overflow", :message "binary overflow adding #{x} and #{y}"}
  if x < 0 and y < 0 and sum >= 0
    throw {:code "overflow", :message "binary underflow adding #{x} and #{y}"}
  else
    sum
\e
function

> add(1, 2)
3

> add(math.max_long, 1)
ERROR:
  code: CUSTOM_ERROR
  message: CUSTOM_ERROR
  at: [interactive]:8:5
  source: throw {:code "overflow", :message "binary overflow adding #{x} and #{y}"}
  value: {
  :message "binary overflow adding 9223372036854775807 and 1",
  :code "overflow"
}


> add(math.min_long, -1)
ERROR:
  code: CUSTOM_ERROR
  message: CUSTOM_ERROR
  at: [interactive]:11:5
  source: throw {:code "overflow", :message "binary underflow adding #{x} and #{y}"}
  value: {
  :message "binary underflow adding -9223372036854775808 and -1",
  :code "overflow"
}

```

#### Catching errors

Errors can be caught if they thrown inside a try expression. The error value and stack trace can each be bound to an identifier in the catch block.

```text
tryCatch
  : 'try' expression 'catch' catchDeclaration expression
  ;

catchDeclaration
  :                               # catches discarding error
  | identifier                    # catches error
  | identifier ',' identifier     # catches error and trace
  ;
```

The whole try-catch block is an expression. It evaluates the expression in the try block. If that does not throw it becomes the result of the entire try-catch block. If evaluation of the try block throws, then the error value and trace values are bound to the catch block identifiers in order. The catch expression is evaluated and becomes the result of the try-catch block. If evaluation of the catch block throws, the error is propagated up.

```tweakflow
> \e
# add two longs, revert to fallback_value if overflow or underflow happens
add_safe: (long x=0, long y=0, long fallback_value=nil) -> long
  try
    add(x, y)
  catch error
    if (error[:code] == "overflow") # overflow?
      fallback_value
    else
      throw error # some other error, re-throw

\e
function

> add_safe(1, 2)
3

> add_safe(math.max_long, 1)
nil
```

#### Error conventions
By convention, the tweakflow runtime and standard libraries throw dict errors that contain the following keys:

| Key       | Content                                                         |
| --------- | --------------------------------------------------------------- |
| code      | a short mnemonic string code indicating the nature of the error |
| message   | a text description of the problem                               |

Additional keys may be present depending on the nature of the error.

```tweakflow
> try 1//0 catch error error
{
  :message "division by zero",
  :code "DIVISION_BY_ZERO"
}
> try throw "foo" catch error error
"foo"
```

#### Capturing the error trace
When catching errors with two identifiers, the second identifier contains a detailed trace of where the error occurred. The trace is a dict containing the following keys:

| Key                | Content                                                         |
| ------------------ | --------------------------------------------------------------- |
| code               | a short mnemonic string code indicating the nature of the error |
| message            | a text description of the problem                               |
| at (optional)      | location where the error was thrown as string `file:line:char`  |
| source (optional)  | source code of the expression throwing the exception            |
| value (optional)   | error value as caught in first catch identifier                 |
| stack (optional)   | list of string locations leading to the error, each in `file:line:char` format |

Additional keys may be present depending on the nature of the error.

```tweakflow
> try 1//0 catch _, trace trace
{
  :message "division by zero",
  :stack ["[interactive]:3:5", "[interactive]:2:3", "[interactive]:1:1"],
  :code "DIVISION_BY_ZERO",
  :at "[interactive]:3:14",
  :source "1//0"
}
> try throw "foo" catch _, trace trace
{
  :message "CUSTOM_ERROR",
  :stack ["[interactive]:3:14", "[interactive]:3:5", "[interactive]:2:3", "[interactive]:1:1"],
  :code "CUSTOM_ERROR",
  :value "foo",
  :at "[interactive]:3:14",
  :source "throw \"foo\""
}
```

### References

References point to named values. There are four variants of references: unscoped references, library scope references, module scope references, and global scope references. All variants have a basic structure: a sequence of identifiers separated by the dot character. Scoped references include an anchor prefix specifying where to begin name resolution.

The syntax is as follows:

```text
reference
  :                   identifier ('.' identifier)*   # unscoped reference
  | ('library::')     identifier ('.' identifier)*   # library reference
  | ('::'|'module::') identifier ('.' identifier)*   # module reference
  | ('$'|'global::')  identifier ('.' identifier)*   # global reference
  ;
```

The initial identifier is resolved based on the variant of the reference. Variant-specific details are given in later sections. Any identifiers after the first one are then resolved strictly inside the last found entity's scope.

For example: the reference `foo.bar.baz` might point to a module import `foo` which contains a library `bar` which in turn contains a variable named `baz`.

#### Unscoped references

Unscoped references are the most common form of reference. They have no anchor prefix. Unscoped references' initial identifiers are resolved starting in the scope they appear in, searching up the scope hierarchy towards module scope inclusively if the identifier cannot be found. If the reference appears in a local scope, all parent local scopes are searched. The search does not propagate into global scope.

An example file with comments highlighting scope changes and references:

```tweakflow
# scopes.tf

# introduces 'strings' in module scope
import strings from "std";

# introduces 'len' in module scope
# references 'strings' in module scope
alias strings.length as len;

# introduces 'utils' in module scope
library utils {

  # introduces 'f' in library scope
  # references 'len' from module scope
  f: (x) -> len(x);

  # introduces 'g' in library scope
  # references 'f' in library scope
  g: (x) -> f(x) + 1;
}

# introduces 'foo' in module scope
library foo {

  # introduces 'f' in library scope
  # references 'utils' from module scope
  f: (x) -> utils.f(x);
}
```

An example nesting local scopes:

```tweakflow
> \e
let {
  a: "outer a";
  b: let {
      a: "inner a";
     }
     a;       # references inner a
}
a .. " / ".. b
\e
"outer a / inner a"
```

#### Library scope references

Library scope references must appear inside a library. They limit the resolution process of the initial identifier to the containing library's scope.  They are prefixed with the `library::` anchor.

```tweakflow
# libary-refs.tf

import strings from "std";

library utils {
  f: (x) -> strings.length(x);
  g: (x) -> let {
              f: (n) -> n+1;
            }
            f(library::f(x)); # f(utils.f(x))
}
```

Loading the module on the REPL:

```tweakflow
> \load /path/to/library-refs.tf
library-refs.tf> utils.g("abc")
4
```

#### Module scope references

Module scope references limit the resolution process of the initial identifier to module scope. They are prefixed with the `::` or `module::` anchors.

```tweakflow
# module-refs.tf

# introduce 's' in module scope
import strings as s from "std";

library utils {
  s: "variable s";
  # reference 's' in module scope
  f: (x) -> ::s.length(x);
}
```

Loading the module on the REPL:

```tweakflow
> \load /path/to/module-refs.tf
module-refs.tf> utils.f("foo")
3
```

#### Global scope references

Global scope references limit the resolution process of the initial identifier to global scope. They are prefixed with `$` or `global::` anchors.

See [global modules](#global-modules) for an example of global references pointing to global modules.

#### Referencing values

References in expressions must point to values. If `foo.bar.baz` points to a module import, a library and finally a variable, then `foo` on its own is an invalid value reference, as it points to a module, which is not a value. `foo.bar` is not a valid reference either. It points to a library which is not a value. Only the full reference `foo.bar.baz` points to a value.

The REPL evaluates input as expressions. It gives the following output when referencing a variable and a library respectively:

```tweakflow
> strings.length
function

> strings
ERROR:
  code: INVALID_REFERENCE_TARGET
  message: Cannot reference LIBRARY. Not a value.
```

#### Referencing non-value entities

References in aliases and exports may point to any kind of entity. Aliases provide local names for imported libraries and functions. For example:

```tweakflow
# file aliases.tf
import * as std from "std";

# local alias for imported library
alias std.strings as str;

library util {
  # uses aliased library name
  len: str.length;
}
```

On the REPL:

```tweakflow
> \load /path/to/aliases.tf
aliases.tf> util.len("foo")
3
```

#### Circular references

Circular references are not allowed. Aliases, imports, and variables must not refer back to their values in their definitions. References to called functions are exempted from circular dependency analysis. Recursive calls are therefore permitted.

```tweakflow
> \e
let {
  a: d;
  b: a;
  c: b;
  d: c;
} [a, b, c, d]
\e
ERROR:
  code: CYCLIC_REFERENCE
  message: CYCLIC_REFERENCE
  at: [interactive]:7:3
  source: d: c
  cycle: "d@[interactive]:7:3 -> c@[interactive]:6:3 -> b@[interactive]:5:3 -> a@[interactive]:4:3 -> d@[interactive]:7:3"
```

A recursive definition of the factorial function:

```tweakflow
> \e
factorial: (long x) -> long
  if x < 0 then throw "cannot calc factorial of negative number: #{x}"
  if x <= 1 then 1
  factorial(x-1)*x # recursive definition
\e
function
> factorial(1)
1

> factorial(2)
2

> factorial(3)
6

> factorial(4)
24

> factorial(5)
120

> factorial(10)
3628800
```

#### Closures

Function bodies can close over non-local values. The references are evaluated at the time the function is defined. The value is closed over, not the reference.

The following example creates a sequence of functions, each multiplying its input by a number it closes over:

```tweakflow
> \e
fs:
  for i <- [1, 2, 3],
      (x) -> x*i
\e
[function, function, function]
```

Each function has closed over the value of `i`, not a reference to `i`. Therefore each function multiplies by a different number:

```tweakflow
> fs[0](10) # first function multiplies by 1
10

> fs[1](10) # second function multiplies by 2
20

> fs[2](10) # third function multiplies by 3
30
```

### Operators

#### Precedence grouping

Syntax: `(a)`

Sub-expressions in parentheses define evaluation precedence. `(a+b)*c` multiplies a sum with c, whereas `a+(b*c)` adds a product to a.

#### Boolean not

Syntax: `!a` or `not a`

The operand is cast to boolean and a negation is performed resulting in another boolean.

`!nil` evaluates to `true`

```tweakflow
> !false
true

> !true
false

> !"foo"
false
```

#### Boolean and

Syntax: `a&&b` or `a and b`

This operation is a short-circuiting boolean and. The first operand `a` is evaluated and cast to boolean. If `a` evaluates to `false` or `nil`, the whole expression evaluates to `false`, and `b` is not evaluated. If `a` evaluates to `true`, b is evaluated and cast to boolean. If `b` evaluates to `true` the whole expression evaluates to `true`. Otherwise the whole expression evaluates to `false`.

```tweakflow
> 1 && 2
true

> 1 && 0
false

> false && throw "not evaluated"
false

> true && true
true

> [] && 1
false

> ["foo"] && 1
true
```

#### Boolean or

Syntax: `a||b` or `a or b`

This operation is a short-circuiting boolean or. The first operand `a` is evaluated and cast to boolean. If `a` evaluates to `true`, the whole expression evaluates to `true`, and `b` is not evaluated. If `a` evaluates to `false` or `nil`, b is evaluated and cast to boolean. If `b` evaluates to `true` the whole expression evaluates to `true`. Otherwise the whole expression evaluates to `false`.

```tweakflow
> true || false
true

> true || throw "not evaluated"
true

> false || true
true

> [] || []
false

> [] || [1]
true
```

#### Unary minus

Syntax: `-a`

The operand must be of type long, double or decimal. Any other types throw an error. The operand is negated retaining its original type. If the operand is `nil` the result is `nil`.

The following special cases are defined:

| Expression       | Result          |
| ---------------- | --------------- |
| `-(Infinity)`    | `-Infinity`     |
| `-NaN`           | `NaN`           |
| `-math.min_long` | `math.min_long` |

```tweakflow
> -(1)
-1

> -(-1d)
1d

> -(-2.3)
2.3

> -(Infinity)
-Infinity

> -(NaN)
NaN

> -("foo")
ERROR:
  code: CAST_ERROR
  message: Cannot negate type: string
```

#### Addition

Syntax: `a+b`

Evaluates to the sum of a and b.

Each operand must be either a long, double or decimal. Any other types throw an error. The following rules are applied in order:

  - If any operand is `nil`, the result is `nil`.
  - If any operand is `NaN` the result is `NaN`.
  - If both operands are longs, integer addition is performed and the result is a long. Overflows and underflows do not throw.
  - If both operands are of type double, a floating point sum is performed. The result is a double.
  - If both operands are of type decimal, a decimal exact sum is performed. The result is a decimal.
  - If operands are of types long and double, the long operand is cast to double, and a floating point sum is performed. The result is a double.
  - If one operand is `Infinity` and the other operand is finite, the result is `Infinity`.
  - If one operand is `-Infinity` and the other operand is finite, the result is `-Infinity`.
  - If only one operand is of type decimal, the non-decimal operand is cast to decimal, and a decimal exact sum is performed. The result is a decimal.

Special cases involving `Infinity` and `NaN` are defined as follows:

| Expression              | Result      |
| ----------------------- | ----------- |
| `Infinity + Infinity`   | `Infinity`  |
| `-Infinity + Infinity`  | `NaN`       |
| `Infinity + -Infinity`  | `NaN`       |
| `-Infinity + -Infinity` | `-Infinity` |

```tweakflow
> 1+2
3

> 2.0+2
4.0

> 4d + 2
6d

> Infinity + 3
Infinity

> math.max_long + 1   # binary overflow
-9223372036854775808
```

#### Subtraction

Syntax: `a-b`

Evaluates to the value of a with b subtracted.

Each operand must be either a long, double or decimal. Any other types throw an error. The following rules are applied in order:

  - If any operand is `nil`, the result is `nil`.
  - If any operand is `NaN` the result is `NaN`.
  - If both operands are longs, integer subtraction is performed and the result is a long. Overflows and underflows do not throw.
  - If both operands are of type double, a floating point subtraction is performed. The result is a double.
  - If both operands are of type decimal, a decimal exact subtraction is performed. The result is a decimal.
  - If operands are of types long and double, the long operand is cast to double, and a floating point subtraction is performed. The result is a double.
  - If operand `a` is `Infinity` and the other operand is finite, the result is `Infinity`.
  - If operand `b` is `Infinity` and the other operand is finite, the result is `-Infinity`.
  - If operand `a` is `-Infinity` and the other operand is finite, the result is `-Infinity`.
  - If operand `b` is `-Infinity` and the other operand is finite, the result is `Infinity`.
  - If only one operand is of type decimal, the non-decimal operand is cast to decimal, and a decimal exact subtraction is performed. The result is a decimal.

Special cases involving `Infinity` and `NaN` are defined as follows:

| Expression                  | Result      |
| --------------------------- | ----------- |
| `Infinity - Infinity`       | `NaN`       |
| `(-Infinity) - Infinity`    | `-Infinity` |
| `Infinity - (-Infinity`)    | `Infinity`  |
| `(-Infinity) - (-Infinity)` | `NaN`       |

```tweakflow
> 5-3
2

> 5-10
-5

> 2.3-9
-6.7

> 0.1d-0.2d
-0.1d

> math.min_long - 1 # binary underflow
9223372036854775807

> Infinity - 100
Infinity
```


#### Multiplication

Syntax: `a*b`

Operands are multiplied. Evaluation proceeds as follows:

Each operand must be either a long, double, or decimal. Any other types throw an error.

  - If any operand is `nil`, the result is `nil`.
  - If any operand is `NaN` the result is `NaN`.
  - If both operands are longs, integer multiplication is performed and the result is a long. Overflows and underflows do not throw.
  - If both operands are of type double, a floating point multiplication is performed. The result is a double.
  - If both operands are of type decimal, a decimal exact multiplication is performed. The result is a decimal.
  - If operands are of types long and double, the long operand is cast to double, and a floating point multiplication is performed. The result is a double.
  - If one operand is `Infinity` and the other operand is finite and positive, the result is `Infinity`.
  - If one operand is `Infinity` and the other operand is finite and negative, the result is `-Infinity`.
  - If one operand is `-Infinity` and the other operand is finite and positive, the result is `-Infinity`.
  - If one operand is `-Infinity` and the other operand is finite and negative, the result is `Infinity`.
  - If only one operand is of type decimal, the non-decimal operand is cast to decimal, and a decimal exact multiplication is performed. The result is a decimal.

Special cases involving `NaN` and `Infinity` are defined as follows:

| Expression                               | Result      |
| ---------------------------------------- | ----------- |
| `Infinity * Infinity`                    | `Infinity`  |
| `-Infinity * Infinity` `Infinity * -Infinity` | `-Infinity` |
| `-Infinity * -Infinity`                  | `Infinity`  |
| `Infinity * 0` `0 * Infinity`            | `NaN`       |
| `-Infinity * 0` `0 * -Infinity`          | `NaN`       |

```tweakflow
> 2 * 3
6

> 2 * 3.3
6.6

> 1.1d * 3.3
3.63d

> 1.1 * 2.9
3.19

> math.max_long * math.max_long # binary overflow
1

> math.max_long as double * math.max_long # floating point multiplication
8.507059173023462E37

> math.max_long as decimal * math.max_long # decimal multiplication
85070591730234615847396907784232501249d
```

#### Division

Syntax: `a/b`

Evaluates to a divided by b.

Each operand must be either a long, double, or decimal. Any other types throw an error.

  - If any operand is `nil`, the result is `nil`.
  - If any operand is `NaN` the result is `NaN`.
  - If both operands are of type long, floating point division is performed and the result is a double.
  - If both operands are of type double, a floating point division is performed. The result is a double.
  - If both operands are of type decimal, a decimal division is performed. The result is a decimal.
  - If operands are of types long and double, the long operand is cast to double, and a floating point division is performed. The result is a double.
  - If operand `a` is `Infinity` and the other operand is finite and positive, the result is `Infinity`.
  - If operand `a` is `-Infinity` and the other operand is finite and negative, the result is `-Infinity`.
  - If operand `b` is `Infinity` and the other operand is finite and positive, the result is `0`.
  - If operand `b` is `-Infinity` and the other operand is finite and negative, the result is `0`.
  - If only one operand is of type decimal, the non-decimal operand is cast to decimal, and a decimal division is performed. The result is a decimal.

In case a decimal division is performed, the following rules apply:

Operand b must not be zero. The scale of the result is no less than the scale of operand a.
The result's scale includes up to 20 fractional digits, and is rounded if necessary. Rounding occurs towards the nearest neighbor, with ties rounded away from zero.
For more control over the scale and rounding of results see [decimals.divide](/modules/std.html#decimals-divide).

Special cases involving `Infinity` and `NaN` are defined as follows:

| Expression                      | Result      |
| ------------------------------- | ----------- |
| `x / 0`  (x > 0)                | `Infinity`  |
| `x / 0`  (x < 0)                | `-Infinity` |
| `0 / 0`                         | `NaN`       |
| `[+¦-]Infinity / [+¦-]Infinity` | `NaN`       |

```tweakflow
> 1 / 2
0.5

> 5 / 0.5
10.0

> nil / 2
nil
```

#### Integer division

Syntax: `a//b`

Casts a and b to long, and performs integer division.

Each operand must be either a long, double, or decimal. Any other types throw an error.

If any operands are `nil`, the result is `nil`.

Division by zero throws an error.

Both operands are cast to long before division is performed. The result of the division is a long. Any remainder value is ignored.

```tweakflow
> 10 // 2
5

> 10 // 3
3

> 10 // 4
2

> 10 // 1
10

> 10 // -3
-3

> 10 // 0
ERROR:
  code: DIVISION_BY_ZERO
  message: division by zero
```

#### Division remainder

Syntax: `a%b`

Evaluates to the remainder after a is divided by b.

Each operand must be either a long, double, or decimal. Any other types throw an error.

  - If any operand is `nil`, the result is `nil`.
  - If any operand is `NaN` the result is `NaN`.
  - If both operands are longs, an integer remainder calculation is performed, and the result is a long. `b` cannot be zero in this case. A division by zero throws an error.
  - If both operands are decimals, a decimal remainder calculation is performed, and the result is a decimal. `b` cannot be zero in this case. A division by zero throws an error.
  - If both operands are of type double, a floating point calculation is performed. The result is a double. The floating point calculation evaluates to `NaN` when dividing by zero.
  - If the operands are of type long and double, the long operand is cast to double, and a floating point calculation is performed. The result is a double. The floating point calculation evaluates to `NaN` when dividing by zero.
  - If one of the operands is of type decimal, the other operand is cast to decimal, a decimal remainder calculation is performed, and the result is a decimal. `b` cannot be zero in this case. A division by zero throws an error.


The sign of the result depends on the sign of `a`.

| Signs    | Result |
| -------- | ------ |
| `a >= 0` | `>=0`  |
| `a < 0`  | `<=0`  |

Special cases involving `Infinity` and `NaN` are defined as follows:

| Expression                      | Result |
| ------------------------------- | ------ |
| `x % 0.0`                       | `NaN`  |
| `[+¦-]Infinity % x`             | `NaN`  |
| `[+¦-]Infinity % [+¦-]Infinity` | `NaN`  |
| `x % [+¦-]Infinity`             | `x`    |

```tweakflow
> 10 % 4
2

> 10 % 3
1

> 10 % 2.5
0.0

> 5 % 1.5
0.5

> -5 % 1.5
-0.5

# the inexact nature of floating point numbers shows here
> 100.0 % 0.1
0.09999999999999445

# the decimal result is exact
> 100d % 0.1d
0d
```

#### Exponentiation

Syntax: `a**b`

Operand a is raised to the power of b.

Each operand must be of type long, double or decimal. Any other types throw an error.

  - If any operand is `nil`, the result is `nil`.
  - If `a` is a decimal and `b` is a long, exact exponentiation is performed. The result is of type decimal. Operand `b` must be within `0` and `999_999_999`.
  - In all other cases `a` and `b` are cast to double and a floating point exponentiation is performed. The result is of type double.

Special cases involving `NaN` and `Infinity` are defined as follows:

| Expression                | Result     |
| ------------------------- | ---------- |
| `Infinity ** Infinity`    | `Infinity` |
| `-Infinity ** Infinity`   | `Infinity` |
| `Infinity ** -Infinity`   | `0.0`      |
| `-Infinity ** -Infinity`  | `0.0`      |
| `Infinity ** 0`           | `1.0`      |
| `-Infinity ** 0`          | `1.0`      |
| `NaN ** 0`                | `1.0`      |
| `0 ** Infinity`           | `0.0`      |
| `0 ** -Infinity`          | `Infinity` |
| `NaN ** x` for all x != 0 | `NaN`      |
| `x ** NaN`                | `NaN`      |

```tweakflow
> 2**3
8.0

> 4**0.5
2.0

> 2**10
1024.0

# floating point exponentiation
> 2.2 ** 2
4.840000000000001

# exact exponentiation
> 2.2d ** 2
4.84d

> nil**nil
nil

> "2"**"3"
ERROR:
  code: CAST_ERROR
  message: cannot lift base of type string to exponent of type string
  at: [interactive]:3:10
  source: "2"**"3"
```

#### Equality

Syntax: `a==b`

Evaluates to `true` if a is equal to b. Returns `false` otherwise.

Some type-specific rules apply in determining equality.

The double special value `NaN` is not equal to anything, not even to itself.

```tweakflow
> NaN == NaN
false
```

Values of type double, long, or decimal are equal if the they have the same magnitude.
When comparing finite double and decimal types, the double value is cast to decimal first.
Decimal values are equal if they are mathematically equal, regardless of scale.

```tweakflow
> 0 == 0.0
true

> 0 == 0.000d
true

> 0.1 == 0.1d
true

> 0.1d == 0.1000d
true

> 3 == 3.0
true

> -4 == 4.0
false

> 0 == NaN
false
```

Datetime values are equal only if their date, time, and timezone components match. They are not considered equal if they merely happen to represent to the same point in time. Use [time.compare](/modules/std.html#time-compare) to determine whether datetime values represent the same point in time.

```tweakflow
# same points in time, but different local time and time zone
> time.compare(1970-01-01T01:00:00+01:00, time.epoch)
0
# same points in time are not equal
> 1970-01-01T01:00:00+01:00 == time.epoch
false
# going back to UTC offset of time.epoch, they are equal
> 1970-01-01T00:00:00+00:00 == time.epoch
true
```

Function values are never equal to anything, not even to themselves.

```tweakflow
> strings.length("foo")
3

> strings.length == strings.length
false
```

Lists are equal if they contain items that compare as equal.

```tweakflow
> [1, 2] == [1.0, 2.0]
true

> [NaN] == [NaN]
false
```

Dicts are equal if they have the same keyset and values associated with the same keys compare as equal.

```tweakflow
> {:a 1} == {:a 1.0}
true

> {:a NaN} == {:a NaN}
false
```

#### Inequality

Syntax: `a!=b`

Inversion of equality. Evaluates to `true` if `a==b` evaluates to `false`. Evaluates to `false` if `a==b` evaluates to true.

#### Less than

Syntax: `a<b`

Evaluates to `true` if a is less than b, `false` otherwise.

Each operand must be a long, double, or decimal. Supplying any other types throws an error.

  - If either operand is `nil`, or `NaN` the result is `false`.
  - If both operands are long, an integer comparison is performed.
  - If both operands are double, a floating point comparison is performed.
  - If both operands are decimal, a decimal comparison is performed.
  - If the operands are long and double, the long operand is cast to double, and a floating point comparison is performed.
  - If the operands are long and decimal, the long operand is cast to decimal, and a decimal comparison is performed.
  - If the operands are finite double and decimal, the double is cast to decimal, and a decimal comparison is performed.
  - -Infinity is less than any finite number
  - Infinity is greater than an finite number

```tweakflow
> 1 < 2
true

> 1 < 6d
true

> 1 < 1
false

> 1.0 < 1
false

> -Infinity < 5
true

> "1" < 1
ERROR:
  code: CAST_ERROR
  message: cannot compare types string and long

```

#### Less than or equal

Syntax: `a<=b`

Evaluates to true if a is less than b, or equal to b.

Each operand must be a long, double, or decimal. Supplying any other types throws an error.

  - If both operands are `nil`, the result is `true`.
  - If exactly one operand is `nil` the result is `false`.
  - If any operand is `NaN` the result is `false`.
  - If both operands are long, an integer comparison is performed.
  - If both operands are double, a floating point comparison is performed.
  - If both operands are decimal, a decimal comparison is performed.
  - If the operands are long and double, the long operand is cast to double, and a floating point comparison is performed.
  - If the operands are long and decimal, the long operand is cast to decimal, and a decimal comparison is performed.
  - If the operands are finite double and decimal, the double is cast to decimal, and a decimal comparison is performed.
  - -Infinity is less than any finite number
  - Infinity is greater than an finite number

```tweakflow
> 1 <= 3
true

> 1 <= 1d
true

> 1.0 <= Infinity
true

> NaN <= NaN
false

> nil <= nil
true
```

#### Greater than

Syntax: `a>b`

Evaluates to `true` if a is greater than b, `false` otherwise.

Each operand must be a long, double, or decimal. Supplying any other types throws an error.

  - If either operand is `nil`, or `NaN` the result is `false`.
  - If both operands are long, an integer comparison is performed.
  - If both operands are double, a floating point comparison is performed.
  - If both operands are decimal, a decimal comparison is performed.
  - If the operands are long and double, the long operand is cast to double, and a floating point comparison is performed.
  - If the operands are long and decimal, the long operand is cast to decimal, and a decimal comparison is performed.
  - If the operands are finite double and decimal, the double is cast to decimal, and a decimal comparison is performed.
  - -Infinity is less than any finite number
  - Infinity is greater than an finite number

```tweakflow
> 1 > 2
false

> Infinity > 4
true

> 5 > 3d
true

> NaN > 2
false

> Infinity > NaN
false

> 4.0 > 2
true
```

#### Greater than or equal

Syntax: `a>=b`

Evaluates to true if a is greater than b, or equal to b.

Each operand must be a long, double, or decimal. Supplying any other types throws an error.

  - If both operands are `nil`, the result is `true`.
  - If exactly one operand is `nil` the result is `false`.
  - If any operand is `NaN` the result is `false`.
  - If both operands are long, an integer comparison is performed.
  - If both operands are double, a floating point comparison is performed.
  - If both operands are decimal, a decimal comparison is performed.
  - If the operands are long and double, the long operand is cast to double, and a floating point comparison is performed.
  - If the operands are long and decimal, the long operand is cast to decimal, and a decimal comparison is performed.
  - If the operands are finite double and decimal, the double is cast to decimal, and a decimal comparison is performed.
  - -Infinity is less than any finite number
  - Infinity is greater than an finite number

```tweakflow
> 1 >= 2
false

> Infinity >= 2
true

> 2.0 >= 2d
true

> NaN > 2
false

> nil >= nil
true

> Infinity >= -Infinity
true
```

#### Equality with type identity

Syntax: `a===b`

Evaluates to `true` if a is equal to b as per the semantics of the [equality](#equality) operator `==`, and in addition a and b are of the same type. Evaluates to `false` otherwise. Lists and dicts compare as equal with type identity if their elements compare as equal with type identity.

```tweakflow
> 0 === -0
true

> 1 === 1
true

> 1 === 1.0
false

> 1 === 1d
false

> 1d === 1.0000d
true

> "foo" === "foo"
true

> {:a 1.0} === {:a 1.0}
true

> {:a 1.0} === {:a 1}
false

> [1.0] === [1.0]
true

> [1.0] === [1]
false
```

#### Inequality with type identity

Syntax: `a!==b`

Evaluates to `false` if a is equal to b as per the semantics of the equality operator `==`, and in addition a and b are of the same type. Evaluates to `true` otherwise. Lists and dicts compare as not equal with type identity if their elements compare as not equal with type identity.

```tweakflow
> 0 !== 1
true

> 0 !== 0
false

> 1 !== 1.0
true

> 1 !== 1d
true

> "foo" !== "foo"
false

> {:a 1.0} !== {:a 1.0}
false

> {:a 1.0} !== {:a 1}
true

> [1.0] !== [1.0]
false

> [1.0] !== [1]
true
```

#### String concatenation

Syntax: `a..b`

Both operands are cast to string, then they are concatenated to form the result string. A `nil` value is converted to the string `"nil"` before concatenation.

```tweakflow
> "Hello".." ".."World"
"Hello World"

> "foo"..1
"foo1"
```

#### Type check

Syntax: `a is datatype`

```text
datatype
  : (boolean|string|long|double|decimal|datetime|list|dict|function|void|any)
  ;
```

The check is a boolean expression and it evaluates to `true` or `false` depending on whether `a` evaluates to a  member of the given data type.

As a special case, the `nil` value, even though a member of any type, only yields true when checked as being member of the `void` type. Therefore `a is string` implies that `a` is a non-nil string.

As a special case, if `any` is given as data type, the result is true only if the expression evaluates to a value other than `nil`,  making  `a is any` equivalent to `a != nil`.

```tweakflow
> "" is string
true

> nil is string
false

> 42 is string
false

> {} is list
false

> [] is list
true

> {} is dict
true

> [1,2] is dict
false

> nil is void
true

> "foo" is any
true

> nil is any
false
```

#### Type name

Syntax: `typeof a`

The expression returns the name of a value's type. The possible results are: `"boolean"`, `"string"`, `"long"`, `"double"`, `"decimal"`, `"datetime"`, `"list"`, `"dict"`, `"function"`, or `"void"`. Any non-nil value yields its type. The `nil` value yields `"void"`.

```tweakflow
> typeof "foo"
"string"

> typeof (x) -> x+1
"function"

> typeof 1
"long"

> typeof 1.0
"double"

> typeof 3d
"decimal"

> typeof false
"boolean"

> typeof {}
"dict"

> typeof []
"list"

> typeof 2017-03-12T
"datetime"

> typeof 0babcdef
"binary"

> typeof nil
"void"
```

#### Type cast

Syntax: `a as datatype`

```text
datatype
  : (boolean|string|long|double|decimal|datetime|list|dict|function|void|any)
  ;
```

The expression explicitly casts `a` to a given type.

Type casts may throw errors if the types are incompatible or the specific value is not convertible. In general, type casts only succeed if there is either no information loss, or the amount of information loss is no greater than to be expected from the types involved.

Supported type casts are listed for each type in their respective section of [data types](#data-types). Type casts to `any` always succeed, and leave the value unchanged. Type casts to `void` only succeed if `a` is `nil`.

```tweakflow
> "1.4" as double # string to double
1.4

> ["a", "b", "c", "d"] as dict # list to dict
{
  :a "b",
  :c "d"
}

> 2017-07-23T23:12:32.298+02:00@Europe/Berlin as dict # datetime as dict
{
  :month 7,
  :day_of_year 204,
  :hour 23,
  :zone "Europe/Berlin",
  :nano_of_second 298000000,
  :offset_seconds 7200,
  :second 32,
  :minute 12,
  :day_of_week 7,
  :week_of_year 29,
  :day_of_month 23,
  :year 2017
}

> nil as string # nil casts to any type
nil
```

#### Bitwise not

Syntax: `~a`

The operand is cast to long, and a bitwise not operation is performed on its two's complement representation, resulting in another long.

`~nil` evaluates to `nil`

```tweakflow
> ~0
-1

> ~(-1)
0
```

#### Bitwise shift left

Syntax: `a<<b`

Both operands are cast to long. An error is thrown if any operand cannot be cast to long. The long value of a is shifted left by b bits to form the result.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 1 << 2
4

> -1 << 8
-256

> 7 << 1
14

> 2.3 << 4.9 # operands are cast to 2 << 4
32

> "1" << 3.4 # operands are cast to 1 << 3
8

> nil << 1
nil
```

#### Bitwise shift right, sign preserving

Syntax: `a>>b`

Both operands are cast to long. An error is thrown if any operand cannot be cast to long. The long value of a is shifted right by b bits to form the result. Bits coming in on the left side are identical to the leftmost bit of a.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 8 >> 1
4

> 8 >> 8
0

> -1 >> 1
-1

> -1 >> 8
-1

> nil >> 2
nil
```

#### Bitwise shift right

Syntax: `a>>>b`

Both operands are cast to long. An error is thrown if any operand cannot be cast to long. The long value of a is shifted right by b bits to form the result. Bits coming in on the left side are set to 0.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 8 >>> 1
4

> 8 >>> 8
0

> -1 >>> 1
9223372036854775807

> -1 >>> 56
255

> nil >>> 2
nil
```

#### Bitwise and

Syntax: `a&b`

Both operands are cast to long and their two's complement representation bits are combined using the binary AND operation. The result is a long the resulting bits.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 1 & 2
0

> 7 & 15
7

> -1 & 29837
29837

> 3 & 2
2

> nil & 1
nil
```

#### Bitwise exclusive or

Syntax: `a^b`

Both operands are cast to long and their two's complement representation bits are combined using the binary XOR operation.The result is a long the resulting bits.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 1 ^ 1
0

> 1 ^ 2
3

> -1 ^ 0
-1

> -1 ^ 1
-2

> nil ^ 2
nil
```

#### Bitwise or

Syntax: `a|b`

Both operands are cast to long and their two's complement representation bits are combined using the binary OR operation. The result is a long the resulting bits.

If any operand is `nil`, the result is `nil`.

```tweakflow
> 1 | 3
3

> -1 | 0
-1

> 1 | 2 | 4 | 8
15

> nil | 2
nil
```

#### Operator precedence

The following table lists tweakflow operators and constructs in precedence order, starting with the highest precedence.

All operators an constructs are left-associative. When chaining operators of the same precedence, they are evaluated left to right. The expression `a+b+c` is evaluated as `(a+b)+c` for example.

| Operator                             | Example                                 |
| ------------------------------------ | --------------------------------------- |
| Fuction call                         | `f(1, 2, 3)`                            |
| Call chaining                        | `->> ("foo") f, g, h`                   |
| Container access                     | `d[:a]`, `a[1]`                         |
| Precedence grouping                  | `a+(b*c)`                               |
| Type cast                            | `"1.2" as double`                       |
| Default value                        | `name default "John Doe"`               |
| Bitwise not                          | `~bits`                                 |
| Boolean not                          | `!done`                                 |
| Unary minus                          | `-(2+2)`                                |
| Exponentiation                       | `2**10`                                 |
| Floating point division              | `4/2`                                   |
| Integer division                     | `4//2`                                  |
| Multiplication                       | `2*2`                                   |
| Division remainder                   | `length % 2`                            |
| Subtraction                          | `4-2`                                   |
| Addition                             | `4+2`                                   |
| String concatenation                 | `"Hello ".."World"`                     |
| Bitwise shift left                   | `1 << 8`                                |
| Bitwise shift right, sign preserving | `255 >> 2`                              |
| Bitwise shift right                  | `-1 >>> 1`                              |
| Less than                            | `a < max`                               |
| Less than or equal                   | `a <= max`                              |
| Greater than                         | `a > min`                               |
| Greater than or equal                | `a >= min`                              |
| Type check                           | `a is string`                           |
| Type name                            | `typeof a`                              |
| Equality with type identity          | `a === 1`                               |
| Inequality with type identity        | `a !== 1`                               |
| Equality                             | `a == "foo"`                            |
| Inequality                           | `a != "foo"`                            |
| Bitwise and                          | `bits & mask`                           |
| Bitwise xor                          | `bits ^ flip`                           |
| Bitwise or                           | `bits ¦ flags`                          |
| Boolean and                          | `locked && loaded`                      |
| Boolean or                           | `sleepy ¦¦ hungry`                      |
| Pattern matching                     | `match xs [@,@] -> true`                |
| List comprehension                   | `for x <- [1, 2, 3], x*x`               |
| Conditional evaluation               | `if sleepy then sleep(8) else party(4)` |
| Local variables                      | `let {a: 1; b: 2;} a+b`                  |
| Try / catch                          | `try sleep(:long) catch "tired"`        |
| Throw                                | `throw "cannot do this"`                |
| Debug                                | `debug("DEBUG x:", x)`                 |

### Debugging

The debug construct is used to inspect the value of any expression. The host application decides what happens with debugged values. The REPL just prints them to screen. The syntax is:

```text
'debug' '(' expression (',' expression)* ')'
```

Debug itself is an expression.

If a single expression is supplied, it is passed to the host application for debugging, and it is also what the whole debug evaluates to.

If multiple expressions are supplied to debug, all are passed to the host application for debugging, and the last given expression is what the debug expression evaluates to.

As an example, the following function has some conditional branches, and is debugging which branches are taken.

```tweakflow
> \e
sgn: (long x) -> long
  let {
    _: debug("DEBUG: calculating sign of x:", x);
  }
  if x > 0 then debug("DEBUG: x is positive", 1)
  if x < 0 then debug("DEBUG: x is negative", -1)
  else debug("DEBUG: x is zero or nil", 0)
\e
function

> sgn(10)
DEBUG: calculating sign of x: 10
DEBUG: x is positive 1
1

> sgn(-10)
DEBUG: calculating sign of x: -10
DEBUG: x is negative -1
-1

> sgn(0)
DEBUG: calculating sign of x: 0
DEBUG: x is zero or nil 0
0
```

## Specs

A spec module contains a library named `spec` which in turn includes a `spec` variable. The `spec.spec` variable contains the definition of the test suite.

See the [test module](https://github.com/twineworks/tweakflow/blob/{{<gitRef>}}/src/test/resources/spec/std/core/hash.spec.tf) for the standard library function [core.hash](/modules/std.html#core-hash) for reference.

A test suite consists of a tree of nodes. Each node is a dict. The root of the tree must be a [describe](#describe) node.

The [nodes](/modules/std/spec.html#nodes) library contains functions that create correct node structures.

### Describe

Describe nodes serve as nestable containers for related test cases. They can define a consistent test subject value for all descendant test cases. They can also provide before and after hooks for test suites involving effects.

Describe nodes have the following structure:

```tweakflow
{
  :type 'describe',
  :name string,
  :spec [effect|before|subject|subject_transform|subject_effect|describe|it|after],
  :at string,
  :tags [string],
  :context *
}
```

The `name` of the node contributes to the full name of any child `describe` and `it` blocks.

The `spec` key holds a list of child nodes. The following node types are permitted:

  - [effect](#effect)
  - [before](#before)
  - [subject](#constant-subject)
  - [subject_transform](#transform-subject)
  - [subject_effect](#effect-subject)
  - [describe](#describe)
  - [it](#it)
  - [after](#after)

Any [effect](#effect) nodes are invoked immediately after construction. The result of the call replaces the effect node in the list. This feature is useful to dynamically generate a nested describe block with tests that depend on an effect, such as filesystem information.

At most one node of type [subject](#constant-subject), [subject_transform](#transform-subject), or [subject_effect](#effect-subject) is permitted. If any such node is present, it must precede any nested [describe](#describe) and [it](#it) blocks.

The `at` string contains a human readable source location for the node.

The `tags` list contains strings with which the node is tagged.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, describe nodes execute their child nodes in the following order:

  - any [before](#before) nodes, in given order
  - any [subject](#constant-subject), [subject_transform](#transform-subject), or [subject_effect](#effect-subject) node
  - any [describe](#describe) and [it](#it) nodes
  - any [after](#after) nodes, in given order

### Subject

Subject nodes define the subject value under test. At most one subject can be defined per [describe](#describe) node. Variants of the subject node differ in how the value of the subject is determined.

#### Constant subject

A constant subject node has the following structure:

```tweakflow
{
  :type 'subject',
  :data data,
  :at string,
  :context *
}
```

The `data` key holds the subject value.

The `at` string contains a human readable source location for the node.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, the subject node returns the value at its `data` key, which defines the test subject.

#### Transform subject

A transform subject form takes an existing subject value inherited from a parent describe node, and transforms it into another value.

A transform subject node has the following structure:

```tweakflow
{
  :type 'subject_transform',
  :transform function,
  :at string,
  :context *
}
```

The `transform` key holds a function accepting a single argument.

The `at` string contains a human readable source location for the node.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, the node calls the function on its `transform` key with the current subject as argument. The function returns a value that becomes the new test subject.

#### Effect subject

An effect subject node determines the value of the subject by invoking an effect.

An effect subject node has the following structure:

```tweakflow
{
  :type 'subject_effect',
  :effect effect,
  :at string,
  :context *
}
```

The `effect` key holds an effect node.

The `at` string contains a human readable source location for the node.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, the node invokes the effect node on its `effect` key, and the resulting value becomes the new test subject.

### It

It nodes define functions that assert expected facts about the current test subject.

It nodes have the following structure:

```tweakflow
{
  :type 'it',
  :name string,
  :spec function,
  :at string,
  :tags [string],
  :context *
}
```

The `name` of the node contributes to the full name of the `it` block.

The `spec` key holds a function accepting zero or one arguments. This is the test function containing any assertions.

The `at` string contains a human readable source location for the node.

The `tags` list contains strings with which the node is tagged.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, `it` nodes invoke their `spec` function passing the current subject as the first argument. If the `spec` function returns normally, the node passes. If the function throws, the  node fails.

### Effect

Effect nodes describe values that are generated through code involving side effects.

An effect node has the following structure:

```tweakflow
{
  :type 'effect',
  :effect {
    :type string,
    *
  }
}
```
The type string corresponds with keys in registered [SpecEffects](https://github.com/twineworks/tweakflow/blob/{{<gitRef>}}/src/main/java/com/twineworks/tweakflow/spec/effects/SpecEffects.java) maps.

Additional keys on the nested effect dict typically contain effect-specific configuration.

**Execution**

When run, an effect node finds the instance of the effect using the type key, and invokes the effect's [execute](https://github.com/twineworks/tweakflow/blob/{{<gitRef>}}/src/main/java/com/twineworks/tweakflow/spec/effects/SpecEffect.java) method, passing in the runtime, the effect dict, and the current subject value. The return value of this call is the return value of the effect.

### Before
Before nodes are used to trigger side-effects before any tests of a describe block are run. The return value of a before effect is discarded.

A before node has the following structure:

```tweakflow
{
  :type "before",
  :name string,
  :effect effect,
  :at string,
  :context *
}
```

The `name` of the node is used in reporter messages.

The `effect` key contains an effect node.

The `at` string contains a human readable source location for the node.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, a before node executes its nested effect and discards the return value. If the effect execution throws an exception, any contained tests in the current describe block are marked as failed, and are not executed.

### After
After nodes are used to trigger side-effects after all tests of a describe block have run. The return value of an after effect is discarded.

An after node has the following structure:

```tweakflow
{
  :type "after",
  :name string,
  :effect effect,
  :at string,
  :context *
}
```

The `name` of the node is used in reporter messages.

The `effect` key contains an effect node.

The `at` string contains a human readable source location for the node.

The `context` key holds arbitrary data that can be used to communicate additional data to reporters.

**Execution**

When run, an after node executes its nested effect and discards the return value. If the effect execution throws an exception, the entire test-suite aborts with an error.