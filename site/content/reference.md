---
title: The tweakflow data processing language
---

## Motivation

Tweakflow offers a way for JVM applications to evaluate user-supplied expressions in a formula-like notation. Tweakflow supports user-defined functions, libraries, and modules, to support applications in which the user-supplied computations can grow to non-trivial size and complexity. The host application is in control of how much sophistication is available to users. 

## Requirements

Tweakflow runs on the JVM. Java 8 or later is required.

## Design principles

The following sections outline fundamental principles which inform the design of tweakflow. 

### Everything is a value

All data and functions in tweakflow are immutable values. You can always create, inspect, compare and pass them around without worrying about object identity or unexpected modifications. There is no way a value in tweakflow can change. 

Tweakflow uses [persistent data structures](https://en.wikipedia.org/wiki/Persistent_data_structure) for its collections. It comes with a set of functions in the standard library to make compuations with immutable values easy. 

### Reproducible calculations

All functions in tweakflow are pure. The practical consequence is that user-expressions do not maintain state, and cannot trigger any side-effects. All effectful operations like file I/O are performed by the host application. The results of such operations can be passed to user expressions as values, but user expressions cannot introduce any side-effects to the application themselves. 

The above paradigm is familiar from spreadsheet applications. Spreadsheets allow users to work with data using formula expressions, but the formulas themselves are deterministic. The outcome of a spreadsheet calculation does not depend on when the spreadsheet was opened and by whom. Similarly, the outcome of tweakflow expressions is guaranteed to be reproducible and side-effect free.

### The host application is in control

Allowing users to perform computations in an application has implications, especially when users can access application internals. Many general-purpose languages on the JVM, like JRuby, Closure, Scala, or various implementations of Javascript have excellent Java interop capabilities. This is great for trusted code and admin-level features, but it comes at the cost of potential security problems when exposed to a broad audience of users. Java interop in a scripting language makes it realtively easy to call into application internals not intended to be accessed by user scripts, or to access application data that should not be exposed to the user. 

Tweakflow functions can be written in Java, but they must implement an interface. Calling arbitrary Java code is not possible. When embedding tweakflow, the host application can also set up a load path that controls which tweakflow code can contain functions implemented in Java. In addition, the host application can remove or replace the default standard library that comes with tweakflow. As a result applications control precisely what the user expressions can call or have access to. 

## Lexical structure



### Boolean literals

The tokens `true` and `false` are interpreted as boolean literals.

### The nil literal

The `nil` literal represents the singleton [nil value](#nil).

### Long literals

Decimal digits are read as 64-bit signed integers of type `long`. A prefix of `-` indicates a negative value.

```javascript
42
-2
```

Long literals can also be written in hexadecimal form. They are notated as `0x` followed by up to  8 bytes. Each byte consists of two hexadecimal digits from `[0-9a-fA-F]`. The bytes are given in  big-endian order, meaning that the most significant byte is written first. If less than 8 bytes are provided, missing leading bytes are filled up with zeros. The resulting bit pattern is interpreted as a [two's complement](https://en.wikipedia.org/wiki/Two%27s_complement) signed 64-bit integer, exactly like a Java long value.

```ruby
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

```ruby
# various ways to write the decimal number 3.1315

> 3.1315
3.1315

> 0.31315e1
3.1315

> .31315E1
3.1315

> 31315e-4
3.1315
```

Tweakflow does not support hexadecimal notation for doubles.

In addition to regular numbers `NaN` (Not a Number) and `Infinity` literals can be used. 

```ruby
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

### String literals

Strings can occur in many places of an expression or program, playing different semantic roles. Stings appear as keys in dictionaries, as computation values, or as documentation strings, for example. Tweakflow offers several ways to write a literal string. The notations are interchangeable. Each of the notations is valid at any place a string is valid. 

#### Single-quoted strings

A single quoted string begins and ends with a single quote character `'`. Line breaks, tabs, and other special characters are allowed, and included in the string verbatim. If a single-quote character is to be included in the string, it must be escaped with another single-quote. Aside from that, a single-quoted string does not expand any escape sequences.

```ruby
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
| `\t`            | `⇥` tab character                        |
| `\#`            | `#` hash character                       |
| `\n`            | `⏎` newline character                    |
| `\r`            | `⏎` carriage return character            |
| `\u[byte]{2}`   | unicode character from the [basic multilingual plane](https://en.wikipedia.org/wiki/Plane_(Unicode)#Basic_Multilingual_Plane) given by the two-byte address |
| `\U[byte]{4}`   | any unicode character given by the full four-byte address |
| `#{reference}`  | value given by the reference when cast to string |

To prevent expansion of escape sequences beginning with a backslash, escape the backslash, so it is interpreted literally. To prevent expansion of a variable reference, escape the hash character that opens the sequence, so it is interpreted literally.

```ruby
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

The [here document](https://en.wikipedia.org/wiki/Here_document) string notation starts with `~~~\r?\n` and ends with `\r?\n~~~`. All characters inbetween are preserved as a literal string that does not expand any escape sequences.  

```
​~~~
[literal string]
​~~~
```

This style of string is useful when the role of a string is to represent a seperate document. It is typically used for documentation or embedded documents.

```ruby
> \e
​~~~
Hello World
​~~~
\e
"Hello World"

> \e
​~~~
<Contact>  
  <Name>John Doe</Name>  
  <Title>CEO</Title>  
  <Phone>  
    <Number>555-8401</Number>  
    <Type>Voice</Type>  
  </Phone>  
</Contact>
​~~~
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

Tweakflow allows symbol notation for strings. It can be useful to distinguish strings used as keys in dicts or as enumeration items from data strings. Symbol strings follow identifier notation rules, but are prepended with a `:`. Their name is their value. 

A symbol string is written as:

```text
:[a-zA-Z_][a-zA-Z_0-9?]*
```

The escaped form with backticks allows an unconstrained set of characters, with the exception of the backtick itself:

```text
:`.+?`
```

Symbol strings are regular strings. They are merely a notational convenience to distinguish data strings from strings used as keys. Therefore symbol notation is allowed in all places a string is accepted.

```ruby
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

Datetime literals can be specified at various levels of granularity. Starting at the level of days, the datetime literals take the form `[year]-[month]-[day]T`  with year given as four digits, month and day give as two digits each.

```ruby
> 2017-04-30T
2017-04-30T00:00:00Z@UTC
```

The basic form is extended to specify the local time in 24 hour format as `[hours]:[minutes]:[seconds](.[fraction_of_seconds])?` with two digits for hours, minutes, and seconds, and up to nine digits for the optional fraction of seconds.

```ruby
> 2017-04-30T21:32:11
2017-04-30T21:32:11Z@UTC

> 2017-04-30T21:32:11.123456789
2017-04-30T21:32:11.123456789Z@UTC
```

The local time form is extended to specify an offset from UTC of the form `((+|-)[offset_hours]:[offset_minutes])|Z`  where offset hours and offset minutes is specified with 2 digits each. The shorthand Z means UTC time, no offset. 

```ruby
> 2017-04-30T21:32:11+02:00
2017-04-30T21:32:11+02:00@`UTC+02:00`

> 2017-04-30T21:32:11Z
2017-04-30T21:32:11Z@UTC
```

The UTC offset form can be further refined to include the regional time zone, ensuring consistency of local time calculations while observing daylight saving time. The regional time zone form appends an `@` sign, followed by the id of the desired time zone. The time zone id follows the same escaping rules as [identifiers](#identifiers), and will often have to be escaped by backticks. 

Time zones are valid if recognized by Java's [ZoneId.of](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-java.lang.String-). A list of known regional zone ids can be obtained by calling [time.zones](/tweakflow/modules/std.html#zones) of the tweakflow standard module. In addition, time zones giving a constant offset from UTC or GMT are accepted, as per the documentation of [ZoneId.of](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html#of-java.lang.String-).

```ruby
> 2017-04-30T21:32:11+02:00@`Europe/Berlin`
2017-04-30T21:32:11+02:00@`Europe/Berlin`

> 2017-04-30T21:32:11+02:00@`UTC+02:00`
2017-04-30T21:32:11+02:00@`UTC+02:00`
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

```ruby
> \e
let {
  greeting: "Hello"
  `%name%`: "Joe"
}
greeting .. " " .. `%name%`
\e
"Hello Joe"
```

### End-of-statement markers

Tweakflow allows to explicitly mark the end of a statement with a semicolon or newline. Any definition statements  for modules, libraries, imports, aliases, exports, and variables can be separated by any number of newlines or semicolons. 

Tweakflow is structured to not be ambiguous in the absence of end-of-statement markers, and as such any end-of-statement markers are entirely optional. The markers are useful to improve readability. They are parsed as whitespace and discarded. Consecutive end-of-statement markers are read as one.

Below example has variable definitions separated by end-of-statement markers. Both semicolon and newline are used to clearly separate the variable definitions:

```ruby
> \e
let {
  a: 1;
  b: 2;
} a+b
\e
3
```

Equivalent, but arguably less readable notation with variable definitions lacking end-of-statement separation:

```ruby
> let { a: 1 b: 2 } a+b
3
```

TODO: include endOfStatement Markers in their respective syntaxes

### Comments

Tweakflow supports line comments and span comments. Each form catering to different uses of comments in code.

#### Line comments

The `#` token signifies a line comment. The `#` character and all subsequent characters to the next newline are ignored.

```ruby
> 3 # This is a comment
3
```

#### Span comments

The comment markers `/*` and `*/` enclose a comment that can span multiple lines. Span comments can be nested.

```ruby
> 3 /* This is a comment */
3

> /* this is a /* nested */ comment */ 3
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

### Data types

Tweakflow supports a fixed set of data types. Each data type has literal notation, and a set of supported cast targets to other types. The following sections highlight all available types and their characteristics.

#### Boolean

The `boolean` type holds the values `true` and `false`. Booleans are notated using [boolean literals](#boolean-literals). The following type casts are supported:

##### Boolean as long

Boolean `true` is cast to `1` and boolean `false` is cast to `0`.

##### Boolean as double

Boolean `true` is cast to `1.0` and boolean `false` is cast to `0.0`.

##### Boolean as string

Boolean `true` is cast to `"true"` and boolean `false` is cast to `"false"`.

#### Long

The `long` type holds 64-bit signed integers. Integers are notated using [long literals](#long-literals). The following type casts are supported:

##### Long as boolean

The long `0` is converted to `false`. Any other long value is converted to `true`.

##### Long as double

The long number is converted to to closest double value possible.

##### Long as string

The long is converted to a decimal number with a potential leading minus sign.

##### Long as datetime

The long is interpreted as the number of milliseconds passed since `time.epoch` in UTC.

```ruby
> 0 as datetime
1970-01-01T00:00:00Z@UTC

> 1501757830000 as datetime
2017-08-03T10:57:10Z@UTC

> -1501757830000 as datetime
1922-05-31T13:02:50Z@UTC
```

#### Double

The `double` type holds 64-bit double-precision IEEE 754 floating point numbers. Literal floating point numbers are notated using [double literals](#double-literals). The following type casts are supported:

##### Double as boolean

The double values `0.0`,  `-0.0`, and `NaN` are converted to `false`. Any other values are converted to `true`.

##### Double as long

The double value is truncated at the decimal point and converted to the closest long value.

##### Double as string

- If the double value is `NaN`, the string is `"NaN"`.
- If the double value is positive infinity, the string is `"Infinity"`.
- if the double value is negative infinity, the string is `"-Infinity"`.
- if the double value is 0.0, the string is `"0.0"`
- if the double value is -0.0, the string is `"-0.0"`

For any other double value, the [conventions the Java language](https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html#toString-double-) are used.

Casting doubles to string should only be done for non-functional purposes like data-inspection, debugging or logging. The standard library offers [formatters](/tweakflow/modules/std.html#formatter-1) to to convert double values to strings in a controlled output format.

#### String

The `string` type holds text information. Strings are notated using [string literals](#string-literals). The following type casts are supported:

##### String as boolean

The empty string `""` is cast to `false`. Any other string value is cast to `true`.

##### String as long

The string is first trimmed of whitespace on both sides. It is then interpreted as a decimal number with an optional leading `+` or `-` sign, any leading zeros, and digits `0-9`. The trimmed string must conform to the regular expression:

```text
[+-]?[0-9]+
```

If the resulting number does not fit in a 64-bit signed integer, an error is thrown.

##### String as double

Strings cast to doubles successfully if they pass the following regular expression:

```text
[\x00-\x20]*                                 # optional leading whitespace
[+-]?                                        # optional sign
(NaN)|                                       # Not a Number
(Infinity)|                                  # Infinity
([0-9]+(\.[0-9]+)?([eE][+-]?[0-9]+)?)|       # Digits optionally followed by decimal dot
                                             # fractional digits, and exponent                                              
\.[0-9]+([eE][+-]?[0-9]+)?)                  # decimal dot followed by fractional digits
                                             # and exponent
[\x00-\x20]*                                 # optional trailing whitespace
```

Examples for casts from string to double:

```ruby
> "1.0" as double
1.0

> "2e3" as double
2000.0

> "2230.3e-1" as double
223.03

> ".98e2" as double
98.0

> "200.0kg" as double
ERROR: {
  :message "Cannot cast 200.0kg to double",
  ...
}
```

##### String as list

A string is converted to a list of individual character strings. More precisely, it is converted to a list of its unicode codepoints. 

```ruby
> "" as list
[]

> "hello" as list
["h", "e", "l", "l", "o"]

> "I love 𝄞" as list
["I", " ", "l", "o", "v", "e", " ", "𝄞"]
```

#### Datetime

The `datetime` type represents points in time while also carrying regional time zone information. Datetimes are notated using [datetime literals](#datetime-literals).

The following type casts are supported:

##### Datetime as boolean

All datetime values cast to boolean `true`.

##### Datetime as string

A datetime value casts to a string compatible with [datetime literal](#datetime-literals) notation. 

```ruby
> time.epoch as string
"1970-01-01T00:00:00Z@UTC"
```

Casting datetimes to string should only be done for non-functional purposes like data-inspection, debugging or logging. The standard library offers [formatters](/tweakflow/modules/std.html#formatter) to to convert datetime values to strings in a controlled output format.

##### Datetime as dict

A datetime value casts to a dict that contains all of its fields together with day of week, day of year, and week of year information. Supposing `x` is the datetime to cast, and `time` is the library from the standard module, the dict is equivalent to the following definition: 

```ruby
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

```ruby
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

> 2017-07-23T23:12:32.298+02:00@`Europe/Berlin` as dict
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

#### List

The `list` type holds a finite sequence of values in a defined order. It is equivalent to array types in other languages. They are indexed by long values, starting at index 0. Lists are internally indexed by integers and have a capacity limit of 2^31 = 2.147.483.648 elements.

Lists are notated as a sequence of values inside square brackets. Commas separating entries are optional. The empty list is written as `[]`.

A splat expression can be used to concatenate lists inline. 

The formal syntax of a list literal is as follows:

```text
listLiteral
   : '[' ((expression|splat) ','? )*  ']'
   ;

splat
  : '...' expression
  ;  
```

A few example lists:

```ruby
> [1, 2, 3] # a basic list
[1, 2, 3]

> [1 2 3] # commas are optional
[1, 2, 3]

> [] # empty list
[]

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

It is worth noting that [container access](#container-access) has precedence over sequencing items in a list literal, which can lead to unexpected results when nesting list literals. You can disambiguate by placing explicit commas to sequence list items.

```ruby
> [["a", "b"] [0]]   # container access x[0] has precedence over list literal sequence
["a"]

> [["a", "b"], [0]]  # ambiguity resolved
[["a", "b"], [0]]
```

When a splat expression is encountered, it is evaluated, cast to list and concatenated with any previous list items. A few examples of splats:

```ruby
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

The following type casts are supported:

##### List as boolean

An empty list `[]` converts to `false`. Any other list value converts to `true`.

##### List as dict

Lists are converted as sequences of key-value pairs. `["a" 1 "b" 2]` is converted to `{:a 1 :b 2}`. Items in key position are cast to strings. The conversion proceeds left to right, with any duplicate keys being replaced with the rightmost occurrence. If the list has an odd number of items, an error is thrown. If any of the keys is `nil` or cannot be cast to string, an error is thrown.

```ruby
> ["a" 1 "b" 2 "c" 3] as dict
{
  :a 1,
  :b 2,
  :c 3
}

> [1 2 3 4] as dict
{
  :`1` 2,
  :`3` 4
}

> [] as dict
{}

> ["a" "b" "a" "d"] as dict # rightmost key "a" takes precedence
{
  :a "d"
}

> ["a" nil "b" 1] as dict # nil values are allowed
{
  :a nil,
  :b 1
}

> ["a" "b" nil "d"] as dict # nil keys are not allowed
ERROR: {
  :message "Cannot cast list to dict with nil key at index: 2",
  ...
}

> [1 2 3] as dict
ERROR: {
  :message "Cannot cast list with odd number of items to dict",
  ...
}
```

#### Dict

The `dict` type is an associative structure that maps string keys are to arbitrary values. Dicts do not support `nil` keys, but `nil` values are permitted. The order of keys in a dict is undefined. 

Dicts are notated as a sequence of key and value pairs inside curly brackets. Keys are implicitly cast to strings. Commas separating entries are optional. The empty dict is written as `{}`.

A splat expression can be used to merge dicts inline. 

The formal syntax of a dict literal is as follows:

```
dictLiteral
   : '{' ((expression expression)|(splat) ','? )*  '}'
   ;

splat
  : '...' expression
  ;
```

A few example dicts:

```ruby
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

```ruby
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

The following type casts are supported:

##### Dict as boolean

The empty dict `{}` converts to `false`. Any other dict value converts to `true`.

##### Dict as list

Dicts are converted to lists as a sequence of key-value pairs. An empty dict gives an empty list. Keys and values appear in pairs, but the order of the pairs is not defined.

```ruby
> {} as list
[]

> {:a "foo" :b "bar"} as list
["a", "foo", "b", "bar"]

> {:a 1 :b 2}  as list
["a", 1, "b", 2]

> {:b 1 :a 2}  as list
["a", 2, "b", 1]
```

#### Function

The `function` type holds callable functions. There is only one data type for functions. It encompasses functions of all signatures. 

Function notation has two parts: function head, and body. The head holds the function signature: paramter list and return type. The body is either an expression that evaluates to the function's return value, or a structure specifying the Java class that is implementing the function.

Formally function syntax is as follows: 

```text
functionLiteral
  : functionHead (expression|viaDec)
  ;

functionHead
  : '(' paramsList ')' '->' dataType?
  ;

paramsList
  : (paramDef ','?) *
  ;

paramDef
  : dataType? identifier ('=' expression)?
  ;

viaDec
  : 'via' literal
  ;
```

A function head specifies a parameter list, and an optional return type. If the return type is omitted `any` is used. Parameter list items can be delimited by a comma, or just whitespace. Each parameter has a name, an optional data type, and an optional default value. If the data type is omitted, `any` is used, if the default value is omitted `nil` is used. When a function is invoked, all arguments are implicitly cast to the declared parameter types.

If a function body is an expression, it is evaluated to the return value when the function is called. The return value is implicitly cast to the declared return value of the function.

The following type casts are supported:

##### Function as boolean

All function values cast to boolean `true`.

Some examples:

```ruby
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
> f([1,2,3])
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

The signature of a function can be inspected calling [fun.signature](/tweakflow/modules/std.html#signature) from the standard library.

```ruby
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

##### Functions in Java

Instead of a body expression, tweakflow functions can specify a Java class that implements the function. The notation is the keyword `via` followed by a map literal containing the key `:class` which points to a Java class. The Java class must implement the tag interface [UserFunction](#link-to-user), as well as a exactly one of the following interfaces governing parameter passing. 

| Interface               | Purpose                                  |
| ----------------------- | ---------------------------------------- |
| [Arity0UserFunction](#) | Implements zero-parameter functions.     |
| [Arity1UserFunction](#) | Implements single-parameter functions.   |
| [Arity2UserFunction](#) | Implements functions with two parameters |
| [Arity3UserFunction](#) | Implements functions with three paramters. |
| [Arity4UserFunction](#) | Implements functions with four parameters |
| [ArityNUserFunction](#) | Implements functions with any number of parameters. Arguments are passed as an array of values. |

For example, the inner class [com.twineworks.tweakflow.std.Strings$concat](https://github.com/twineworks/tweakflow/blob/releases/0.0.1/src/main/java/com/twineworks/tweakflow/std/Strings.java#L43) implements the `strings.concat` function of the standard library. 

```ruby
> f: (list xs) -> string via {:class "com.twineworks.tweakflow.std.Strings$concat"}
function
> f(["Foo", "Bar", "Baz"])
"FooBarBaz"
```

See the standard library functions in [std](https://github.com/twineworks/tweakflow/tree/releases/0.0.1/src/main/java/com/twineworks/tweakflow/std) for examples of functions implemented in Java.

#### Void

The `void` type is a type that has `nil` as its only value. The `nil` value reports `void` as its type, even though it is a valid member of any type. 

The only `void` value `nil` casts successfully to any type, and remains `nil`.

#### Any

The `any` type is a type is not concrete type of its own, but it is used to indicate the possibility of any type being present in the given context. The `any` type is used as a default in type declarations for variables, parameters and return types. 

### Modules

Modules are the top level organizational unit in tweakflow. A module is typically a file, but host applications are free to supply modules as in-memory text as well. There is exactly one module per file. 

Module files must be encoded in UTF-8. The default file extension for modules is `.tf`. 

The role of a module is to provide a top level grouping of related functions and values. In this sense modules act like namespaces or packages. 

A module usually starts with the `module` keyword. If there are no [annotations](#annotations), the `module` keyword becomes optional, and the beginning of the file is treated as the beginning of the module. 

[Global modules](#global-modules) need to announce the fact that they are global, and provide a global identifier to store the module in. A global module starts with the keywords `global` `module` followed by an identifier. Any  [annotations](#annotations) go before that sequence.

Formally modules have the following syntax:

```
module
  : moduleHead moduleComponent*
  ;

moduleHead
  : nameDec? (importDef | aliasDef | exportDef) *
  ;
  
nameDec
  : metaDef 'module'
  | metaDef 'global' 'module' identifier
  ;

moduleComponent
  : library
  ;
```

#### Global modules

Modules can declare themselves available under a specific name in [global scope](#references). It is an error to load more than one module claiming the same name.

Global modules are designed to facilitate project-wide configuration and global libraries of which there must be exactly one in scope at all times. Individual modules remain in control of their functional dependencies through imports, but their global dependencies are controlled from the outside. When using tweakflow standalone, a global module would be loaded from the command line. When using tweakflow embedded, typically the host application would be loading global modules.

**Example use of global modules for configuration**

The following set of files constitute configuration variants, available globally as `env`. 

```ruby
# environments/local.tf
global module env

export library conf {
  string data_path: "/home/me/my_project/data/"
}
```

```ruby
# environments/production.tf
global module env

export library conf {
  string data_path: "/var/incoming/data/"
}
```

This file uses the environment configuration throught the global reference `$env`.

```ruby
# main.tf
library my_lib {
  file_path: (string prefix) -> $env.conf.data_path .. prefix .. "_data.csv"
}
```

#### Imports

Import statements bring names exported from other modules into the current module. The syntax allows importing individual names, or all exported names at once. If imported individually, names may be bound to local names that are different from the names in the source module.

It is an error to import a name that is not explicitly exported.

Imported names are placed in [module scope](#references). 

Imports have the following syntax:

```text
importDef 
  : 'import' importMember (','? importMember)* 'from' modulePath
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

The given module path is first appended the default module extension if not present. Then the module path is searched for on the load path. If the module path starts with a dot, tweakflow searches for the file relative to the module doing the import. The resulting path must still be on the load path. If the module path does not start with a dot, tweakflow searches all load path locations in their specified order. The order is typically specified on the command line when using [language tools](#language-tools) or by the host application when embedding. 

**Examples**

Module `"./util/strings.tf"` is imported as a whole below. Any exported name `x` is available as `utils.x` locally.

```javascript
import * as utils from "./util/strings.tf"
```

A specific library `conversion_lib` is imported from `"./util/strings.tf"` below. Its local name remains `conversion_lib`.

```javascript
import conversion_lib from "./util/strings.tf"
```

Specific entities are imported individually below. The import statement references two exported libraries from module `"./util/strings.tf"`, making them available under local names `str` and `conv`.

```javascript
import string_lib as str, conversion_lib as conv from "./util/strings.tf"
```

#### Aliases

Tweakflow allows local aliases to shorten or relabel names, making local code independent of name conventions in other modules. Aliases are placed in [module scope](#module-scope).

Aliases have the following syntax:

```text
aliasDef
  : 'alias' reference 'as' aliasName
  ;

aliasName
  : IDENTIFIER
  ;
```

The following module uses aliases.

```ruby
# file: aliases.tf
import * as std from "std"

# s can be used as shortcut to std.strings
alias std.strings as s
# map can be used as shortcut to std.data.map
alias std.data.map as map
# aliases can be aliased again
alias map as m

library my_util {
  # s alias used here
  greeting: s.concat(["Hello", " ", "World!"]) 	# "Hello World!"
  # m alias used here
  mapped: m([1,2,3], (x) -> x*x) 				# [1, 4, 9]
}
```

#### Exports 

A module defines its public interface using exports. Libraries can be exported inline when defined, or explicitly in an export statement. An export statement refers to a name and makes it available for other modules to import. You can optionally specify an export name, that is only visible to other modules when importing, but not within the exporting module itself. Exporting an imported or aliased name is allowed.

```text
exportDef
  : 'export' reference ('as' exportName)?
  ;

exportName
  : IDENTIFIER
  ;
```

Below example exports the strings standard library under the name `str`, and a local library `common` under the name `util` .

```text
# lib.tf
import * as std from "std"

export std.strings as str
export common as util

library common {
  ...
}
```

The following file imports both the `str` and the `util` export.

```ruby
# main.tf
import util, str from "./lib.tf"
```

### Libraries

A  library is a named collection of variables. The variables typically hold functions, but they can hold any data type. Libraries can be marked as exports as part of their definition, in which case they are exported from the enclosing module using their given name.

**Syntax**

```text
library
  : metaDef 'export'? 'library' identifier '{' varDef* '}'
  ;
```

Below is an exported library holding some functions.

```ruby
export library nums {
  function square: (x) 	-> x**2
  function root: (x) 	-> x**0.5
}
```

### Variables 
A variable is a named entity that holds a value. Variables are placed in [libraries](#libraries). Variables have a name, a  [type](#data-types), and a value. They can also be annotated by [docs and metadata](#annotations).

```
varDef
  : metaDef dataType? identifier ':' expression endOfStatement?
  | metaDef 'provided' dataType? identifier endOfStatement?
  ;
```

The type of variables guarantees that referencing a variable results in a value of the specified type. Variable values are cast implicitly if necessary. 

```ruby
> boolean bool_var: 1
true

> boolean bool_var: 0
false
```

If unspecified, the variable type is `any`, and no implicit casts take place.

```ruby
> some_var: 1
1

> any some_var: 1
1
```

Variable values are either given directly as [expressions](#expressions), or the variables are marked as `provided`. The host application is required to set the values of `provided` variables through the embedding API. Initially, all provided variables have the value `nil`. 

Tweakflow uses strict evaluation. All variables of a library are guaranteed to evaluate even if they are not referenced by other expressions.

### Annotations

Modules, libraries, and variables support documentation and metadata annotations. These are just literal values associated with the module, library or variable they annotate. They can be inspected in the REPL. Language processing tools like [tfdoc](#tfdoc) can extract them to generate project documentation. 

Both doc and meta annotations are optional. They can occur in any order before the definition of a module, library or variable. Doc annotations begin with the keyword `doc` followed by an expression. Meta annotations begin with the keyworkd `meta` followed by an expression. 

The doc and meta expressions must consist of value literals that evaluate to themselves. They cannot contain any form of computation like  operators or function calls. Function literals are also not permitted.

The following module contains a single library with a single function:

```ruby
module

library bar {
  function baz: (x) -> x*x
}
```

The same module with a full set of annotations at the module, library, and variable level:

```ruby
# module foo.tf
doc
​~~~
This is documentation at the module level.
​~~~
meta {
  :title       "foo"
  :description "Description of the module"
  :version     "4.2"
}
module

# library bar
doc
​~~~
This is documentation for library bar.
​~~~
meta {
  :author "John Doe et al."
  :since  "2.3"
}

library bar {

# function baz
doc
​~~~
This is documentation for function baz.
​~~~
  meta {
    :author "John Doe"
    :date 2017-03-12T
  }
  function baz: (x) -> x*x
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

### Scope

TODO

TODO: You can't replace things in scope, importing, aliasing etc. cannot alter existing impor

### Expressions

Tweakflow expressions evaluate to values. The most basic of which are literal values. All data types can be written as literals. Tweakflow also has function calls, conditionals, list comprehensions, pattern matching, type casts, and several operators for many common computations. 

#### Nil

The `nil` value is written as simply `nil`. Semantiaclly, a `nil` value indicates the absence of a value. The `nil` value is special because it is a valid member of all data types. It casts to any type successfully as `nil`. 

#### Value literals

All tweakflow data types have a literal notation outlined in their respective section under [data types](#data-types).

#### Type inspection

Tweakflow allows checking the type of a value using the `is` keyword.

```text
expression 'is' (boolean|string|long|double|datetime|list|dict|function|void|any)
```

The check is a boolean expression and it evaluates to `true` or `false` depending on whether the given expression evaluates to a  member of the given data type. 

As a special case, the `nil` value, even though a member of any type, only yields true when checked as being member of the `void` type. Therefore `expression is string` implies that expression evaluated to a non-nil string.

As a special case, if `any` is given as data type, the result is true only if the expression evaluates to a value other than `nil`,  making the `expression is any` equivalent to `expression != nil`.

```ruby
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

#### Type casts

The `as` keyword allows to explicitly cast a value expression to a given type.

```text
expression 'as' dataType
```

Type casts may throw errors if the types are incompatible or the specific value is not convertible. In general, type casts only succeed if there is either no information loss, or the amount of information loss is no greater than to be expected from the types involved. 

Supported type casts are listed for each type in their respective section of [data types](#data-types). Type casts to `any` always succeed, and leave the value unchanged. Type casts to `void` only succeed if the value was `nil`.

A few examples:

```ruby
> "1.4" as double # string to double
1.4

> ["a", "b", "c", "d"] as dict # list to dict
{
  :a "b",
  :c "d"
}

> 1234567890123 as datetime # long to datetime
2009-02-13T23:31:30.123Z@UTC

> 2017-07-23T23:12:32.298+02:00@`Europe/Berlin` as dict # datetime as dict
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

#### Container access

List and dict contents are accessed using square brackets. Tweakflow supports traversing through deep structure by giving several keys at a time. Splat keys allow traversing paths given by a list at runtime. The formal structure of container access expressions is as follows: 

```text
containerAccess
  : expression'['containerAccessKeySequence']'
  ;
	
containerAccessKeySequence
  : ((expression | splat) ','?)+
  ;

splat
  : '...' expression
  ;
```

##### List access

Indexes supplied for a list are automatically cast to long values.  If the given index does not exist in the list, the value of the access expression is `nil`. Accessing a `nil` list yields `nil`.

```ruby
> items: ["a" "b" "c"]
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

##### Dict access

Dicts are indexed with strings. Keys are automatically cast to strings. If a given key does not exist, the value of the access expression is `nil`. Accessing `nil` yields `nil`.

```ruby
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

##### Container traversal

Container access expressions can be chained to access nested data.

```ruby
> \e
story: {
  :name "A Study in Scarlet"
  :adaptations [
    {:year 1914 :media "silent film"}
    {:year 1968 :media "television series"}	
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

```ruby
> story[:adaptations][1][:media]
"television series"

> story[:adaptations 1 :media]
"television series"
```

It is worth noting that if the traversal yields `nil` at any intermediate point, the result is `nil`, which is consistent with `nil[x]` being `nil`.

```ruby
> story[:adaptations][4][:media] # there is no adaptation at index 4
nil

> story[:adaptations 4 :media]
nil
```

The list of keys in the traversal form can be interspersed with splat expressions. The splat expression must be a list containing the keys to access. Each splat expression is expanded, and concatenated with any existing items just as in [list literals](#list-literals).

```ruby
> path: [:adaptations 1 :media]
["adaptations", 1, "media"]

> story[...path]
"television series"

> story[:adaptations ...[0 :year]]
1914

> story[...[:adaptations] ...[1] ...[:year]]
1968
```

#### Function calls

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

```ruby
> ((x) -> x*x)(2) # call with argument x=2
4
```

You can reference the function and call it:

```ruby
> f: (x) -> x*x  # define a function and place it in f
function

> f(2) # call function f
4

> strings.length("foo") # call standard library function strings.length with one argument "foo"
3
```

 For the purposes of further discussion, let's define function `f` as:

```ruby
f: (long id = 0, string name = "n/a") -> string id .. "-" .. name
```

Above function has parameters `id` of type long and `name` of type string. Both have non-nil default values. 

When calling a function it is possible to specify arguments values using position, name, or a mix of both.

##### Positional arguments

Arguments given by position just list the values in parameter order and are seperated by comma. The following call passes `42` as `id` and `"test"` as `name`.

```ruby
> f(42, "test")
"42-test"
```

Passing more than the declared number of positional arguments is an error.

```ruby
> f(42, "test", "too much")
ERROR: {
  :message "cannot call function with 3 arguments",
  :code "UNEXPECTED_ARGUMENT",
  ...
}
```

Passing less than the declared number of positional arguments results in the missing arguments being supplied through default values of the missing parameters. All parameters of a function have the default value `nil` unless explicitly specified in the function definition.

```ruby
> f(12)
"12-n/a"

> f()
"0-n/a"

> g: (x) -> x # x's default value is nil
function

> g(1) 
1

> g() # x attains its default value nil
nil
```

##### Named arguments

When calling function, you can also pass arguments by name. Arguments given by name are listed in pairs of names and values separated by commas. The following call passes `42` as `id` and `"test"`  as `name` again, but uses named arguments this time. The order of named arguments does not matter.

```ruby
> f(id: 42, name: "test")
"42-test"

> f(name: "test", id: 42)
"42-test"
```

Omitted arguments are assigned their default parameter value. 

```ruby
> f(id: 42)
"42-n/a"

> f(name: "test")
"0-test"

> f()
"0-n/a"
```

It is an error to supply argument names not present in function parameters:

```ruby
> f(id: 42, name: "foo", country: "US")
ERROR: {
  :message "Function does not have parameter named: country",
  :code "UNEXPECTED_ARGUMENT",
}
```

##### Mixed positional and named arguments

Position and named arguments can be mixed in a single call. Positional arguments are listed first. Named arguments follow. 

The following call passes `42` as `id` and `"test"`  as `name`. It mixes positional and named arguments.

```ruby
> f(42, name: "test")
"42-test"
```

It is an error to supply any positional arguments after named arguments. 

```ruby
> f(id: 42, "test") # error, positional arguments cannot follow named arguments
ERROR: {
  :message "Positional argument cannot follow named arguments.",
  :code "UNEXPECTED_ARGUMENT",
  ...
}
```

It is possible to specify a parameter in both positional and named arguments. The rightmost specified value is used.

```ruby
> f(42, "test", id: 7)
"7-test"

> f(42, "test", id: 7, id: 8)
"8-test"
```

Mixed style arguments are a useful idiom when a function exposes a set of leading arguments that have intuitive order, but allows for a set of less common option-style parameters to configure details.

The function [add_period](/tweakflow/modules/std.html#add-period) from the standard library for example:

```ruby
> time.add_period(time.epoch, years: 1000)
2970-01-01T00:00:00Z@UTC

> time.add_period(time.epoch, days: 2)
1970-01-03T00:00:00Z@UTC
```

##### Splat arguments

Both positional arguments and named arguments can be supplied via a splat expression. This offers a notational convenience for cases where arguments have been collected into a list or dict. 

Whenever positional arguments are allowed, and a splat expression evaluates to a list, the items from the list are used as positional arguments in order. 

```ruby
> args: [42, "name"]
[42, "name"]

> f(...args) # splat positional arguments
"42-name"
```

Positional arguments can be interspersed with splats. The resulting arguments are concatenated left to right:

```ruby
> f(42, ...["name"]) 
"42-name"

> f(...[42], "name")
"42-name"

> f(...[42], ...["name"])
"42-name"
```

Whenever named arguments are allowed, and a splat expression evaluates to a dict, the items from the dict are used as named arguments.

Below example supplies the `id` and `name` named arguments. 

```ruby
> person: {:id 42, :name "test"}
{
  :name "test",
  :id 42
}
> f(...person) # splat named arguments
"42-test"
```

Named arguments can be interspersed with splats. The resulting arguments dict is then merged left to right, with rightmost keys taking precedence in case of duplicates. Below example again effectively passes `42` as `id` and `"test"` as `name`.  

```ruby
> person: {:id 0, :name "test"}
{
  :name "test",
  :id 0
}
> f(...person, id: 42)
"42-test"
```

Splats can be mixed as long as positional splats come first. 

```ruby
> f(...[42, "testing"], ...{:name "foo"})
"42-foo"

> f(...{:name "foo"}, ...[42, "testing"]) # no positional args allowed after named args
ERROR: {
  :message "List splat provides positional arguments and cannot follow named arguments.",
  :code "UNEXPECTED_ARGUMENT",
  ...
}
```

##### Argument casting

Arguments given in function calls are automatically cast to the declared parameter type:

```ruby
> f("3", 9837) # casts the first argument to long, and the second argument to string
"3-9837"

> f("abc", "def") # cannot cast first argument to long
ERROR: {
  :message "Cannot cast abc to long",
  :code "CAST_ERROR",
  ...
}
```

##### Return value casting

Every function declares a return type. It is `any` by default. Every value a function returns is cast to the declared return type implicitly. If the return type of a function is `any`, the cast is not performed.

```ruby
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

#### Local variables

Tweakflow allows defining local variables for temporary results. The `let` expression defines a set of variables that are bound in its scope, shadowing any existing local variables of the same name.

The formal syntax is as follows:

```text
let
  :'let' '{' varDef* '}' expression
  ;

varDef
  : dataType? identifier ':' expression endOfStatement?
  ;
```

TODO: limit provided and meta data for local variables. They are inaccessible, and should be rejected in analysis.

Examples:

```ruby
> let {a: 1; b: 2} a + b
3

> \e
  can_vote: (datetime born, datetime at) ->
    let {
      age: time.years_between(born, at)
      is_eighteen: age >= 18
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

#### Conditional evaluation

TODO

#### List comprehensions

TODO

#### Pattern Matching

TODO

#### Errors

TODO

##### Throwing errors

TODO

```text
'throw' expression
```

Example

```ruby
> \e
add: (long x=0, long y=0, throw_on_overflow=true) ->
  let {
    long sum: x + y
  }
  if throw_on_overflow
    if x > 0 and y > 0 and sum <= 0 then throw {:code "overflow", :message "binary overflow adding #{x} and #{y}"}
      if x < 0 and y < 0 and sum >= 0 then throw {:code "overflow", :message "binary underflow adding #{x} and #{y}"}
    sum
  else
    sum
\e
function

> add(1, 2)
3

> add(math.max_long, 1)
ERROR: {
  :code "CUSTOM_ERROR",
  :value {
    :message "binary overflow adding 9223372036854775807 and 1",
    :code "overflow"
  },
  ...
}        

> add(math.min_long, -1)
ERROR: {
  :code "CUSTOM_ERROR",
  :value {
    :message "binary underflow adding -9223372036854775808 and -1",
    :code "overflow"
  },
  ...
}
```

##### Catching errors

Errors can be caught if they thrown inside a `try/catch` expression. The error value and stacktrace can each be bound to an identifier in the `catch` block.

```text
tryCatch
  : 'try' expression 'catch' catchDeclaration expression
  ;

catchDeclaration
  :                               # catchAnonymous
  | identifier                    # catchError
  | identifier ',' identifier     # catchErrorAndTrace
  ;  
```

The whole try-catch block is an expression. It evaluates the expression in the try block, and if that does not throw, the result of that is the result of the try-catch block. If evaluation of the try block throws, then the error value and trace values are bound to the identifiers in order, if supplied, and the catch expression is evaluated. The result of that becomes the result of the try-catch block. If evaluation of the catch block throws, the error is propagated up.

```ruby
> \e 
# add two longs, revert to fallback_value if overflow or underflow happens
add_safe: (long x=0, long y=0, long fallback_value=0) -> long
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

> add_safe(math.max_long, 1, fallback_value: nil)
nil
```

#### References

Expressions can reference variables available in scope. There are four levels of scope in general: local, library, module, and global. If a variable is referenced via its name, it is searched in the current scope and if not found, the search propagates upwards the scope chain. Global scope contains named modules only. Global names are visible from everywhere, but references must be prefixed with `$` to explicitly reference a name in global scope.

##### Unscoped references

##### Library scope references

Libraries define a set of variables in library local scope. The module and global scopes are visible as well.

##### Module scope references

TODO

##### Global scope references

TODO

Global scope only holds module names. To resolve a reference in global scope, the reference must be prepended with `$`. This is unambiguously referencing the global symbol name.

##### Module names

TODO

To skip the search chain and start resolving a reference in module scope, the reference can be prepended with `::`. This helps unambiguously referencing a namel in module scope, regardless of whether a name in a closer scope is shadowing it.

#### Operators

TODO

Tweakflow features the following operators in precedence order.

All binary operators are left-associative.  Taking addition as an example `a + b + c` is evaluated as `(a + b) + c`.

Operators desugar to standard library calls. There are no special operator semantics, thus operator behavior is completely defined by the behaviour of standard library functions.

TODO: add parentheses for precedence grouping

| Operator  | Role                  | Semantics                                |
| :-------- | :-------------------- | :--------------------------------------- |
| `-a`      | Negation              | `$std.math.negate(a)`                    |
| `!a`      | Boolean not           | `$std.core.'not'(a)`                     |
| `not a`   | Boolean not           | `$std.core.'not'(a)`                     |
| `a ** b`  | Exponentiation        | `$std.math.power(a, b)`                  |
| `a * b`   | Multiplication        | `$std.math.product([a, b])`              |
| `a / b`   | Division              | `$std.math.divide(a, b)`                 |
| `a // b`  | Integer Division      | `$std.math.divide(a, b)`                 |
| `a % b`   | Modulo                | `$std.math.modulo(a, b)`                 |
| `a + b`   | Addition              | `$std.math.sum([a, b])`                  |
| `a - b`   | Subtraction           | `$std.math.subtract(a, b)`               |
| `a .. b`  | String concatenation  | `$std.strings.concat([a, b])`            |
| `a < b`   | Less than             | `$std.math.'<'([a, b])`                  |
| `a <= b`  | Less than or equal    | `$std.math.'<='([a, b])`                 |
| `a > b`   | Greater than          | `$std.math.'>'([a, b])`                  |
| `a >= b`  | Greater than or equal | `$std.math.'>='([a, b])`                 |
| `a == b`  | Equality              | `$std.core.'=='([a, b])`                 |
| `a != b`  | Negated equality      | `$std.core.'not'($std.core.'=='([a, b]))` |
| `a && b`  | Boolean and           | `$std.core.'and'([a, b])`                |
| `a and b` | Boolean and           | `$std.core.'and'([a, b])`                |
| `a ¦¦ b`  | Boolean or            | `$std.core.'or'([a, b])`                 |
| `a or b`  | Boolean or            | `$std.core.'or'([a, b])`                 |

#### Evaluation precedence

TODO: give order of constructs and operators 

## Language tools

### Interactive REPL

TODO: describe itf

### Runner

TODO: describe tf

### Metadata extraction

TODO: describe tfdoc

