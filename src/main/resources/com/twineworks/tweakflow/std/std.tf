# The MIT License (MIT)
#
# Copyright (c) 2019 Twineworks GmbH
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

doc
~~~
The `std` module contains libraries for general computing tasks.
~~~

module;

doc
~~~
The core library contains utility functions to process values at a basic level.
~~~

export library core {

doc
~~~
`(any x) -> any`

Identity function. Returns `x`.

```tweakflow
> core.id("foo")
"foo"

> core.id({:a "b"})
{
  :a "b"
}

> core.id(nil)
nil
```
~~~

  function id: (x) -> x;

doc
~~~
`(x) -> string`

Returns a string representation of `x`.
If `x` is a function the string `'function'` is returned.
Otherwise literal notation is used.
If `x` is not a function, and contains no functions as children `x == core.eval(core.inspect(x))` generally holds true.

```tweakflow
> core.inspect(1)
"1"

> core.inspect([1, 2, 3])
"[1, 2, 3]"

> core.inspect(nil)
"nil"

> core.inspect({:a 1, :b 2})
"{
  :a 1,
  :b 2
}"

> core.inspect("foo")
"\"foo\""
```

~~~
  function inspect: (x) -> string via {:class "com.twineworks.tweakflow.std.Core$inspect"};

doc
~~~
`(x) -> boolean`

Returns `false` if `x` is `nil`, returns `true` otherwise.

```tweakflow
> core.present?("foo")
true

> core.present?(nil)
false
```
~~~

  function present?: (x) -> boolean via {:class "com.twineworks.tweakflow.std.Core$present"};

doc
~~~
`(x) -> boolean`

Returns `true` if `x` is `nil`, returns `false` otherwise.

```tweakflow
> core.nil?("foo")
false

> core.nil?(nil)
true
```
~~~

  function nil?:    (x) -> boolean                  via {:class "com.twineworks.tweakflow.std.Core$isNil"};

doc
~~~
`(x) -> long`

Returns a hashcode of `x`. Values that compare as equal are guaranteed to have the same hashcode.

```tweakflow
> core.hash("foo")
101574

> core.hash(nil)
0

> core.hash([1,2,3])
66614367

> core.hash([1.0, 2.0, 3.0])
66614367
```
~~~
  function hash:    (x) -> long                     via {:class "com.twineworks.tweakflow.std.Core$hash"};

doc
~~~
`(string x) -> any`

Evaluates tweakflow source code `x` in empty scope.
If `x` represents a valid value, the value it is returned.
If `x` cannot be evaluated, an error is thrown.
`x` is evaluated in empty scope, so any references within `x` must be self-contained.
Functions can be declared and called within `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> core.eval("1")
1

> core.eval("'str'")
"str"

> core.eval(nil)
nil

> core.eval("(x) -> x+1")(1)
2

> core.eval("let {f: (x) -> x*x;} f(5)")
25

> core.eval("hello")
ERROR:
  code: UNRESOLVED_REFERENCE
  message: hello is undefined
  at: <eval>:1:1
  source: hello
```
~~~
  function eval:    (string x) -> via {:class "com.twineworks.tweakflow.std.Core$eval"};
}


doc
~~~
The strings library contains basic functions for text processing.
~~~

export library strings {

doc
~~~
`(list xs) -> string`

Returns a string consisting of all `xs` concatenated into a single string.
Each `x` in `xs` is cast to a string before concatenation.
If any `x` cannot be cast to a string, an error is thrown.
If any `x` is `nil`, it is concatenated into the output string as `"nil"`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> strings.concat(["foo", "bar"])
"foobar"

> strings.concat([1, 2, 3])
"123"

> strings.concat(["a", nil, "b"])
"anilb"

> strings.concat([])
""

> strings.concat(nil)
nil
```
~~~
  function concat: (list xs) -> string via {:class "com.twineworks.tweakflow.std.Strings$concat"};

doc
~~~
`(string x) -> long`

Returns the number of unicode codepoints in given string `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> strings.length("")
0

> strings.length("foo")
3

> strings.length("你好")
2
```
~~~
  function length: (string x) -> long via {:class "com.twineworks.tweakflow.std.Strings$length"};

doc
~~~
`(string x, long start=0, long end=nil) -> string`

Returns a substring of `x` beginning at index `start` (inclusive) up to index `end` (exclusive).
If `end` is `nil`, the substring extends to the end of `x`.

Returns an empty string if `end <= start`.

Returns `nil` if `x` is `nil`.

Throws an error if `start` is `nil` or `start < 0`.

```tweakflow
> strings.substring("hello world")
"hello world"

> strings.substring("hello world", 6)
"world"

> strings.substring("hello world", 6, 6)
""

> strings.substring("hello world", 6, 7)
"w"

> strings.substring("hello world", 6, 11)
"world"

> strings.substring("hello world", 6, -2)
""

> strings.substring(nil, 6)
nil

> strings.substring("你好", 0, 1)
"你"

> strings.substring("你好", 1, 2)
"好"

> strings.substring("hello world", nil)
ERROR:
  code: NIL_ERROR
  message: start must not be nil

> strings.substring("hello world", -4)
ERROR:
  code: INDEX_OUT_OF_BOUNDS
  message: start must not be negative: -4
```
~~~

  function substring: (string x, long start=0, long end=nil) -> string via {:class "com.twineworks.tweakflow.std.Strings$substring"};

doc
~~~
`(string x, string search, string replace) -> string`

Returns a string where each occurrence of `search` in `x` is replaced with `replace`.
The replacement happens from left to right,
so `strings.replace('ooo', 'oo', 'g')` results in `'go'` rather than `'og'`.

Returns `nil` if any argument is `nil`.

```tweakflow
> strings.replace('ooo', 'oo', 'g')
"go"

> strings.replace('foo', 'oo', 'ast')
"fast"

> strings.replace('hello', 'e', 'u')
"hullo"

> strings.replace('grab', 'gr', '')
"ab"

> strings.replace('hello world', 'not found', 'something')
"hello world"
```
~~~

  function replace: (string x, string search, string replace) -> string via {:class "com.twineworks.tweakflow.std.Strings$searchReplace"};

doc
~~~
`(list xs, string s="") -> string`

Concatenates a list of `xs` into a single string using `s` as the separator.
Each `x` in `xs` is cast to a string before concatenation.
If any `x` cannot be cast to a string, an error is thrown.
If any `x` is `nil`, it is concatenated into the output string as `"nil"`.

Returns `nil` if `xs` is `nil` or `s` is `nil`.

```tweakflow
> strings.join(["foo", "bar"])
"foobar"

> strings.join(["foo", "bar"], ", ")
"foo, bar"

> strings.join([1, 2, 3], "-")
"1-2-3"

> strings.join([1, nil, 3], "-")
"1-nil-3"

> strings.join([])
""

> strings.join(nil, ", ")
nil

> strings.join(["a", "b"], nil)
nil

```
~~~
  function join: (list xs, string s="") -> string via {:class "com.twineworks.tweakflow.std.Strings$join"};

doc
~~~
`(string x) -> string`

Returns `x` with leading and trailing whitespace characters removed.

Returns `nil` if `x` is `nil`.

```tweakflow
> strings.trim("  foo  ")
"foo"

> strings.trim("line\r\n")
"line"

> strings.trim(nil)
nil
```
~~~
  function trim: (string x) -> string via {:class "com.twineworks.tweakflow.std.Strings$trim"};


doc
~~~
`(string x, string lang='en-US') -> string`

Returns `x` with all characters converted to lower case as per conventions of the given language tag.

Returns `nil` if `x` is `nil` or `lang` is `nil`.

```tweakflow
> strings.lower_case("FOO")
"foo"

> strings.lower_case("TITLE")
"title"

> strings.lower_case("TITLE", "tr")
"tıtle" # note the dotless i in turkish language
```
~~~
  function lower_case: (string x, string lang="en-US") -> string via {:class "com.twineworks.tweakflow.std.Strings$lower_case"};

doc
~~~
`(string x, string lang='en-US') -> string`

Returns `x` with all characters converted to uper case as per conventions of the given language tag.

Returns `nil` if `x` is `nil` or `lang` is `nil`.

```tweakflow
> strings.upper_case("foo")
"FOO"

> strings.upper_case("straße")
"STRASSE" # note that some characters may expand to multiple characters

> strings.upper_case("title", "tr")
"TİTLE" # note the dotted upper case I in turkish language
```
~~~
  function upper_case: (string x, string lang="en-US") -> string via {:class "com.twineworks.tweakflow.std.Strings$upper_case"};

doc
~~~
```
(
  string lang='en-US',
  boolean case_sensitive=true
) -> function
```

Returns a function `f (string a, string b) -> long` that compares two strings `a` and `b` according to conventions appropriate for the given language tag.
If `case_sensitive` is `true`, lower case characters precede their upper case counterparts.
If `case_sensitive` is `false`, lower case characters are considered equal to their upper case conterparts.

`f` returns -1 if a < b.\
`f` returns 1 if a > b.\
`f` returns 0 if a == b.\

`f` considers `nil` less than any non-nil string.

Throws an error if any argument is `nil`.

```tweakflow
> f: strings.comparator("en-US", true)
function

> f("foo", "FOO")
-1

> f: strings.comparator("en-US", false)
function

> f("foo", "FOO")
0

> data.sort(["Foo", "Bar", "foo", "bar", nil], strings.comparator("en-US", true))
[nil, "bar", "Bar", "foo", "Foo"]

> data.sort(["Foo", "Bar", "foo", "bar", nil], strings.comparator("en-US", false))
[nil, "Bar", "bar", "Foo", "foo"]
```
~~~

  function comparator: (lang='en-US', case_sensitive=true) -> function via {:class "com.twineworks.tweakflow.std.Strings$comparator"};

doc
~~~
`(string x) -> list`

Returns a list of characters that make up given string `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> strings.chars("foo")
["f", "o", "o"]

> strings.chars("你好")
["你", "好"]

> strings.chars("")
[]

> strings.chars(nil)
nil
```
~~~
  function chars: (string x) -> list via {:class "com.twineworks.tweakflow.std.Strings$chars"};

doc
~~~
`(string x) -> list`

Returns a list of unicode code point numbers that make up given string `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> strings.code_points("foo")
[102, 111, 111]

> strings.code_points("你好")
[20320, 22909]

> strings.code_points("")
[]

> strings.code_points(nil)
nil
```
~~~
  function code_points: (string x) -> list via {:class "com.twineworks.tweakflow.std.Strings$codePoints"};


doc
~~~
`(list xs) -> string`

Returns string consisting of given `xs`. The `xs` are are longs representing unicode code point numbers.
Any non-long code point numbers in `xs` are cast to long.

Returns `nil` if `xs` is `nil`.

Throws an error if any of the `xs` cannot be cast to long, or the value does not represent a
valid code point.

```tweakflow
> strings.of_code_points([102, 111, 111])
"foo"

> strings.of_code_points([0x4F60, 0x597D, 0x01D11E])
"你好𝄞"

> strings.of_code_points([])
""

> strings.of_code_points(nil)
nil
```
~~~
  function of_code_points: (list xs) -> string via {:class "com.twineworks.tweakflow.std.Strings$ofCodePoints"};


doc
~~~
`(string x, string s=" ") -> list`

Returns a list of strings obtained by splitting `x` using separator `s`.
The separator `s` is treated like a regular string, it is not a regular expression.

Returns `nil` if `x` is `nil` or `s` is `nil`.

```tweakflow
> strings.split("hello world")
["hello", "world"]

> strings.split("a,b,c", ",")
["a", "b", "c"]

> strings.split("foo", ",")
["foo"]

> strings.split(",foo,,bar,", ",")
["", "foo", "", "bar", ""]
```
~~~

  function split: (string x, string s=" ") -> list via {:class "com.twineworks.tweakflow.std.Strings$split"};

doc
~~~
`(string x, string init) -> boolean`

Returns `true` if `x` starts with the substring `init`.\
Returns `false` otherwise.

Returns `true` for any non-nil `x` if `init` is the empty string.

Returns `nil` if `x` is `nil` or `init` is `nil`.

```tweakflow
> strings.starts_with?("yellow", "yell")
true

> strings.starts_with?("yellow", "blue")
false

> strings.starts_with?("yellow", "")
true

> strings.starts_with?(nil, "blue")
nil

> strings.starts_with?("yellow", nil)
nil
```
~~~

  function starts_with?: (string x, string init) -> boolean via {:class "com.twineworks.tweakflow.std.Strings$startsWith"};

doc
~~~
`(string x, string tail) -> boolean`

Returns `true` if `x` ends with the substring `tail`.\
Returns `false` otherwise.

Returns `true` for any non-nil `x` if `tail` is the empty string.

Returns `nil` if `x` is `nil` or `tail` is `nil`.

```tweakflow
> strings.ends_with?("yellow", "low")
true

> strings.ends_with?("yellow", "high")
false

> strings.ends_with?("yellow", "")
true

> strings.ends_with?(nil, "blue")
nil

> strings.ends_with?("yellow", nil)
nil
```
~~~

  function ends_with?: (string x, string tail) -> boolean via {:class "com.twineworks.tweakflow.std.Strings$endsWith"};

doc
~~~
`(string x, string sub, long start=0) -> long`

If `x` contains `sub` at or past index `start`, the function returns the index of the first occurrence of `sub`
at or past index `start`.

Returns `-1` otherwise.

Returns `nil` if any argument is `nil`.

```tweakflow
> strings.index_of("foobar", "foo")
0

> strings.index_of("foobar", "bar")
3

> strings.index_of("foobar", "hello")
-1

> strings.index_of("foobar", "bar", 3)
3

> strings.index_of("foobar", "bar", 4)
-1
```
~~~

  function index_of: (string x, string sub, long start=0) -> long via {:class "com.twineworks.tweakflow.std.Strings$indexOf"};

doc
~~~
`(string x, string sub, long end=nil) -> long`

If `x` contains `sub` at or before index `end` returns the index of the last such occurrence. Returns `-1` otherwise.
If `end` is `nil`, it is interpreted as the last index of `x`.

Returns `nil` if `x` is `nil` or `sub` is `nil`.

```tweakflow
> strings.last_index_of("elementary", "e")
4

> strings.last_index_of("elementary", "e", 100)
4

> strings.last_index_of("elementary", "e", 3)
2

> strings.last_index_of("elementary", "e", 1)
0

> strings.last_index_of("elementary", "e", -5)
-1

> strings.last_index_of("elementary", nil)
nil

> strings.last_index_of(nil, "e")
nil
```
~~~

  function last_index_of: (string x, string sub, long end=nil) -> long via {:class "com.twineworks.tweakflow.std.Strings$lastIndexOf"};

doc
~~~
`(string x, long i) -> string`

Returns `i`th character in string `x`.

Returns `nil` if `x` is `nil`, `i` is `nil`, or `i` is out of bounds: `i<0`, `i>=length(x)`.

```tweakflow
> strings.char_at("elementary", 0)
"e"

> strings.char_at("你好", 1)
"好"

> strings.char_at(nil, 0)
nil
```
~~~

  function char_at: (string x, long i) -> string via {:class "com.twineworks.tweakflow.std.Strings$charAt"};


doc
~~~
`(string x, long i) -> long`

Returns `i`th character code point in string `x`.

Returns `nil` if `x` is `nil`, `i` is `nil`, or `i` is out of bounds: `i<0`, `i>=length(x)`.

```tweakflow
> strings.code_point_at("abc", 0)
97 # "a"

> strings.code_point_at("你好", 0)
20320 # "你"

> strings.code_point_at(nil, 0)
nil
```
~~~

  function code_point_at: (string x, long i) -> long via {:class "com.twineworks.tweakflow.std.Strings$codePointAt"};

doc
~~~
`(string x, string charset="UTF-8") -> binary`

Returns binary representing the bytes of string `x` using given `charset`.

Returns `nil` if `x` is `nil` or `charset` is `nil`.

```tweakflow
> strings.to_bytes("abc")
0b616263

> strings.to_bytes("你好")
0bE4BDA0E5A5BD

> strings.to_bytes("bäcker")
0b62C3A4636B6572

> strings.to_bytes("bäcker", "ISO-8859-1")
0b62E4636B6572

> strings.to_bytes(nil)
nil
```
~~~

  function to_bytes: (string x, string charset="UTF-8") -> binary via {:class "com.twineworks.tweakflow.std.Strings$to_bytes"};

doc
~~~
`(binary x, string charset="UTF-8") -> string`

Returns string represented by the bytes of `x` using given `charset`.

Returns `nil` if `x` is `nil` or `charset` is `nil`.

```tweakflow
> strings.from_bytes(0b616263)
"abc"

> strings.from_bytes(0bE4BDA0E5A5BD)
"你好"

> strings.from_bytes(0b62C3A4636B6572)
"bäcker"

> strings.from_bytes(0b62E4636B6572, "ISO-8859-1")
"bäcker"

> strings.from_bytes(nil)
nil
```
~~~

  function from_bytes: (binary x, string charset="UTF-8") -> string via {:class "com.twineworks.tweakflow.std.Strings$from_bytes"};

doc
~~~
`() -> list`

Returns known charsets. Each item in the returned list is a charset name suitable for passing to
functions requiring a charset name, such as [to_bytes](#strings-to_bytes) and [from_bytes](#strings-from_bytes).
~~~

  function charsets: () -> list via {:class "com.twineworks.tweakflow.std.Strings$charsets"};

}

doc
~~~
The regex library provides functions to work with regular expressions.
Pattern syntax is that of the [Java regular expression language](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html).
~~~

export library regex {

doc
~~~
`(string pattern) -> function`

Returns a predicate function `f` accepting one string argument `x` that checks if the argument matches the given `pattern` completely.
Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.


```tweakflow
> digits?: regex.matching('\d+')
function

> digits?("12345")
true

> digits?("hello")
false

> digits?(nil)
nil
```
~~~

  function matching: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$matching"};

doc
~~~
`(string pattern) -> function`

Returns a function `f` accepting one string argument `x` and returning a list of captured groups in the pattern.

In case `pattern` matches the argument entirely, index `0` contains the input string, and subsequent indexes contain the matched groups. Group indexes correspond to the sequence of opening group parentheses of capturing groups in the pattern.

If a group is optional, and is not matched by the input string, its value in the list is `nil`.

If a capturing group is inside a quantifier, only the last matched string is captured.

In case `pattern` does not match the argument entirely, `f` returns an empty list.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.

```tweakflow
# match ISO date and optional hh:mm time, capturing year, month, day, time
> date_parts: regex.capturing('(\d{4})-(\d{2})-(\d{2})(?:T(\d{2}:\d{2}))?')
function

> date_parts("2017-05-24")
["2017-05-24", "2017", "05", "24", nil]

> date_parts("2017-05-24T08:45")
["2017-05-24T08:45", "2017", "05", "24", "08:45"]

> date_parts("hello")
[]

> date_parts(nil)
nil
```
~~~

  function capturing: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$capturing"};

doc
~~~
`(string pattern) -> function`

Returns a function `f` accepting one string argument `x` and returning a list of captured groups for each successive match of the pattern in `x`.

Each match generates a list with the following properties: Index `0` contains the matched input string, and subsequent indexes contain the matched groups. Group indexes correspond to the sequence of opening group parentheses of capturing groups in the pattern.
If a group is optional, and is not matched by the input string, its value in the list is `nil`.

In case `pattern` does not match `x`, `f` returns an empty list.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.

```tweakflow
# scan for hh:mm pattern, capturing hours and minutes separately for each match.
# AM/PM indicator is optional.
> clock_times: regex.scanning('(\d{1,2}):(\d{2})( AM| PM)?')
function

# 1 match
> clock_times("It was 9:30 AM.")
[["9:30 AM", "9", "30", " AM"]]

# 2 matches, optional AM/PM indicator is missing on second match
> clock_times("The hen jumped over the fence at 10:32 PM, the fox followed at 10:42.")
[["10:32 PM", "10", "32", " PM"], ["10:42", "10", "42", nil]]

# no matches
> clock_times("Hello world!")
[]

> clock_times(nil)
nil
```
~~~

  function scanning: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$scanning"};


doc
~~~
`(string pattern, long limit=nil) -> function`

Returns a function `f` accepting one string argument `x` and returning a list substrings of `x` split on boundaries given by `pattern`.

If `limit` is positive, the pattern is applied `limit-1` times, so at most `limit` items are in the resulting list.\
If `limit` is negative, the pattern is applied as often as possible and there is no limit on the size of the resulting list.\
If `limit` is `0` or `nil`, the pattern is applied as often as possible, and there is no limit on the size of the resulting list. Trailing
empty strings are removed from the list.

In case `pattern` is not found in `x`, `f` returns a list containing only `x`.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.\

```tweakflow
# split on space
> f: regex.splitting(' ')
function

> f("Hello World!")
["Hello", "World!"]

# split on word boundary
> f: regex.splitting('\b')
function

> f("Hello World!")
["Hello", " ", "World", "!"]

# split on comma potentially followed by space
> f: regex.splitting(',( )?')
function

> f("some, CSV,data, here")
["some", "CSV", "data", "here"]

# split out 2 initial sections between dashes
> f: regex.splitting('-', 3)
function

> f("abc-def-xyhaiod-28734-as2")
["abc", "def", "xyhaiod-28734-as2"]

> f(nil)
nil
```
~~~

  function splitting: (string pattern, long limit=nil) -> function via {:class "com.twineworks.tweakflow.std.Regex$splitting"};

doc
~~~
`(string pattern, string replace) -> function`

Returns a function `f` accepting one string argument and returning a string replacing all occurrences of `pattern` with `replace`.
References to capturing groups in `replace` are possible through `$n` syntax, where `n` is the number of the capturing group, as counted by
the occurrence of opening group parentheses. To replace a literal `$n`, escape the `$` sign as `\$`.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if any argument is `nil` or `pattern` is not a valid regular expression.

```tweakflow
# match date in yyyy/dd/mm format and convert to ISO date
> to_iso_date: regex.replacing('(\d{4})/(\d{2})/(\d{2})', '$1-$3-$2')
function

> to_iso_date("2017/24/05")
"2017-05-24"

> to_iso_date("hello")
"hello"

> to_iso_date(nil)
nil

> nr_dollar: regex.replacing('\b(a dollar)|(one dollar)\b', '\$1')
function

> nr_dollar("I need a dollar, just one dollar more.")
"I need $1, just $1 more."
```
~~~

  function replacing: (string pattern, string replace) -> function via {:class "com.twineworks.tweakflow.std.Regex$replacing"};

doc
~~~
`(string x) -> string`

Returns a string in which each character of `x` with special meaning in a pattern has been escaped.
The resulting string matches `x` literally as a pattern.

Returns `nil` if `x` is `nil`.

```tweakflow
# splits by word boundary
> f: regex.splitting('\b')
function

> f('foo\bbar\bbaz')
["foo", "\\", "bbar", "\\", "bbaz"]

# splits by literal sequence of characters '\b'
> f: regex.splitting(regex.quote('\b'))
function

> f('foo\bbar\bbaz')
["foo", "bar", "baz"]
```
~~~

  function quote: (string x) -> string via {:class "com.twineworks.tweakflow.std.Regex$quote"};

}


doc
~~~
The data library contains functions for manipulation of lists and dictionaries.
~~~

export library data {

doc
~~~
`(any xs) -> long`

Given a dict or list of `xs`, returns the collection's size.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`.

```tweakflow
> data.size([])
0

> data.size([1,2,3])
3

> data.size({})
0

> data.size({:a "foo", :b "bar"})
2

> data.size(nil)
nil

> data.size("foo")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: size is not defined for type string
```
~~~

  function size: (any xs) -> long                               via {:class "com.twineworks.tweakflow.std.Data$size"};

doc
~~~
`(any xs) -> boolean`

Given a dict or list of `xs`, returns true if the collection is empty, false otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`.

```tweakflow
> data.empty?([])
true

> data.empty?([1,2,3])
false

> data.empty?({})
true

> data.empty?({:a "foo", :b "bar"})
false

> data.empty?(nil)
nil

> data.empty?("foo")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: empty? is not defined for type string
```
~~~

  function empty?: (any xs) -> boolean                          via {:class "com.twineworks.tweakflow.std.Data$empty"};

doc
~~~
`(xs, key, not_found) ->`

Given a dict or list of `xs`, and a key, returns `xs[key]` if the key is present in `xs`.
Returns `not_found` if `key` is not present in `xs`.

Returns `nil` if `xs` is `nil`, or `key` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `xs` is a `list` and `key` cannot be cast to a `long`. \
Throws an error if `xs` is a `dict` and `key` cannot be cast to a `string`.

```tweakflow
> data.get(["a", "b", "c"], 0, "foo")
"a"

> data.get(["a", "b", "c"], 10, "foo")
"foo"

> data.get({:a "alpha", :b "beta"}, :a, "default")
"alpha"

> data.get({:a "alpha", :b "beta"}, :z, "default")
"default"

> data.get([], nil, "foo")
nil

> data.get(nil, :a, "foo")
nil
```
~~~

  function get: (xs, key, not_found) ->                     via {:class "com.twineworks.tweakflow.std.Data$get"};

doc
~~~
`(xs, key, value) ->`

Given a dict or list of `xs`, returns the same type of collection with `value` placed at `key`.
If `xs` is a list, any undefined indexes are filled with `nil`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `key` is `nil`. \
Throws an error if `xs` is a `list` and `key` cannot be cast to a `long` or `key` is negative. \
Throws an error if `xs` is a `dict` and `key` cannot be cast to a `string`.

```tweakflow
> data.put([], 0, "foo")
["foo"]

> data.put(["bar"], 0, "foo")
["foo"]

> data.put(["bar"], 1, "foo")
["bar", "foo"]

> data.put(["bar"], 2, "foo")
["bar", nil, "foo"]

> data.put(["bar"], 10, "foo")
["bar", nil, nil, nil, nil, nil, nil, nil, nil, nil, "foo"]

> data.put({:a "foo", :b "bar"}, :c, "baz")
{
  :a "foo",
  :b "bar",
  :c "baz"
}

> data.put({:a "foo", :b "bar"}, :a, "baz")
{
  :a "baz",
  :b "bar"
}

> data.put(nil, 0, "baz")
nil
```
~~~

  function put: (xs, key, value) ->                         via {:class "com.twineworks.tweakflow.std.Data$put"};


doc
~~~
`(any xs, list keys, any not_found) ->`

Accesses nested `list` and `dict` values in `xs` using the list of `keys`. Returns the found value.
Returns `not_found` if one of the keys is not present.

Returns `nil` if `xs` is `nil`, or `keys` is `nil`.
Returns `xs` if `keys` is empty.

Throws an error if `xs` or an accessed nested structure is neither a `dict` nor a `list`. \
Throws an error if `xs` or an accessed nested structure is a `list` and the key cannot be cast to a `long`. \
Throws an error if `xs` or an accessed nested structure is a `dict` and the key cannot be cast to a `string`.

```tweakflow
> data.get_in([["a", "b"], ["c", "d"], ["e", "f"]], [2, 0], "foo")
"e"

> data.get_in([["a", "b"], ["c", "d"], ["e", "f"]], [8, 0], "foo")
"foo"

> data.get_in([{:name "alpha"}, {:name "beta"}, {:name "gamma"}], [1, :name], "foo")
"beta"

> data.get_in({:a [1,2,3], :b [4,5,6]}, [:b 1], "default")
5

> data.get_in([1, 2, 3], [], "foo")
[1, 2, 3]

> data.get_in(nil, [0], "foo")
nil

> data.get_in([1,2,3], nil, "foo")
nil

> data.get_in({:a [1,2,3], :b [4,5,6]}, [:b :c], "default")
ERROR:
  code: CAST_ERROR
  message: Cannot cast c to long
```
~~~

  function get_in: (any xs, list keys, any not_found) ->    via {:class "com.twineworks.tweakflow.std.Data$getIn"};

doc
~~~
`(xs, list keys, value) ->`

Given a dict or list of `xs`, returns the same type of collection with `value` placed at the end of navigating
through the list of `keys`.

If any intermediate structures have to be created, they will be, depending on key type.
String keys create dicts. Long keys create lists.

If a value is placed in a list, any undefined indexes are filled with `nil`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `xs` is a `dict` or a `list` and `keys` is `nil`. \
Throws an error if navigating into a `list` and the key cannot be cast to a `long` or the key is negative. \
Throws an error if navigating into a `dict` and the key cannot be cast to a `string`.

```tweakflow

> \e
data.put_in(
  {
    "1" {:name "adam"},
    "2" {:name "jane"}
  },
  [2, :name],
  "eve"
)
\e
{
  :`1` {
    :name "adam"
  },
  :`2` {
    :name "eve"
  }
}

> data.put_in([], [0, :a], "foo")
[{
  :a "foo"
}]

> data.put_in([], [0, :a, 2], "foo")
[{
  :a [nil, nil, "foo"]
}]

> data.put_in({}, [:a, :b, :c], "foo")
{
  :a {
    :b {
      :c "foo"
    }
  }
}

> data.put_in(nil, [0], "baz")
nil
```
~~~

  function put_in: (xs, list keys, value) ->                via {:class "com.twineworks.tweakflow.std.Data$putIn"};

doc
~~~
`(xs, key, function f)`

Given a dict or list of `xs`, returns the same type of collection with `f(v)` placed at `key`, where `v` is the original value found at `key`.
If `xs` is a list, any undefined indexes are filled with `nil`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `key` is `nil`. \
Throws an error if `f` is `nil`. \
Throws an error if `xs` is a `list` and `key` cannot be cast to a `long` or `key` is negative. \
Throws an error if `xs` is a `dict` and `key` cannot be cast to a `string`.

```tweakflow
> data.update(["a", "b", "c"], 0, (x) -> x.."-updated")
["a-updated", "b", "c"]

> data.update(["a", "b", "c"], 5, (x) -> "original was: "..x)
["a", "b", "c", nil, nil, "original was: nil"]

> data.update({:a "foo", :b "bar"}, :a, (x) -> "Mr. "..x)
{
  :a "Mr. foo",
  :b "bar"
}

> data.update(nil, 0, (x) -> x)
nil
```
~~~

  function update: (xs, key, function f) ->                 via {:class "com.twineworks.tweakflow.std.Data$update"};

doc
~~~
`(xs, list keys, function f) ->`

Given a dict or list of `xs`, returns the same type of collection with `f(v)` placed at the end of navigating
through the list of `keys`, where `v` is the value originally found.

If any intermediate structures have to be created, they will be, depending on key type.
String keys create dicts. Long keys create lists.

If a value is placed in a list, any undefined indexes are filled with `nil`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `xs` is a `dict` or a `list` and `keys` is `nil`. \
Throws an error if `f` is `nil`. \
Throws an error if navigating into a `list` and the key cannot be cast to a `long` or the key is negative. \
Throws an error if navigating into a `dict` and the key cannot be cast to a `string`.

```tweakflow

> \e
data.update_in(
  {
    "1" {:name "adam"},
    "2" {:name "jane"}
  },
  [2, :name],
  (x) -> "miss "..x
)
\e
{
  :`1` {
    :name "adam"
  },
  :`2` {
    :name "miss jane"
  }
}

> data.update_in([], [0, :a], (x) -> "updated: "..x)
[{
  :a "updated: nil"
}]

> data.update_in({:even [0, 2, 4, 6], :odd [1, 3, 5, 7]}, [:even, 3], (x) -> x*2)
{
  :odd [1, 3, 5, 7],
  :even [0, 2, 4, 12]
}

> data.update_in(nil, [0], (x) -> x+1)
nil
```
~~~

  function update_in: (xs, list keys, function f) ->        via {:class "com.twineworks.tweakflow.std.Data$updateIn"};

doc
~~~
`(xs) -> list `

Given a `list` or `dict` of `xs`, returns a list of keys present in the structure. If `xs` is a list present indexes are returned in order. If `xs` is a dict, the order of returned keys is undefined.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```tweakflow
> data.keys([])
[]

> data.keys(["a", nil, "c"])
[0, 1, 2]

> data.keys({})
[]

> data.keys({:a "foo", :b "bar"})
["a", "b"]

> data.keys(nil)
nil

> data.keys("foo")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: keys is not defined for type string

```
~~~

  function keys: (xs) -> list                               via {:class "com.twineworks.tweakflow.std.Data$keys"};

doc
~~~
`(xs, key) -> boolean`

Returns `true` if `key` is a key in `xs`, false otherwise.
If `xs` is a `list`, `key` is cast to a `long`.
If `xs` is a `dict`, `key` is cast to a `string`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list`, nor a `dict`.

```tweakflow
> data.has?(["a", "b", "c"], 2)
true

> data.has?(["a", "b", "c"], 3)
false

> data.has?([nil, nil, nil], 1)
true

> data.has?({:a "foo", :b "bar"}, :a)
true

> data.has?({:a "foo", :b "bar"}, :c)
false

> data.has?({:a "foo", :b "bar", :c nil}, :c)
true

> data.has?("foo", 1)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: has? is not defined for type string

> data.has?("foo" as list, 1)
true
```
~~~

  function has?: (xs, key) -> boolean                       via {:class "com.twineworks.tweakflow.std.Data$has"};

doc
~~~
`(xs) -> list `

Given a `list` or `dict` of `xs`, returns a list of values present in the structure. Returns `xs` if `xs` is a list . If `xs` is a dict, the order of returned values is undefined.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```tweakflow
> data.values([])
[]

> data.values(["a", nil, "c"])
["a", nil, "c"]

> data.values({})
[]

> data.values({:a "foo", :b "bar"})
["foo", "bar"]

> data.values(nil)
nil

> data.values("foo")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: values is not defined for type string
```
~~~

  function values: (xs) -> list                             via {:class "com.twineworks.tweakflow.std.Data$values"};

doc
~~~
`(dict xs) -> list`

Returns a `list` where each element is a `dict`. For each key `k` in `xs` there is a correpsonding element `{:key k, :value xs[k]}` in the `list`. The order of items in the `list` is undefined.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.entries({})
[]

> data.entries({:foo "bar"})
[{
  :value "bar",
  :key "foo"
}]

> data.entries({:a 1, :b 2})
[{
  :value 1,
  :key "a"
}, {
  :value 2,
  :key "b"
}]

> data.entries(nil)
nil
```
~~~

  function entries: (dict xs) -> list via {:class "com.twineworks.tweakflow.std.Data$entries"};

doc
~~~
`(x, list xs) -> list`

Returns a list consisting of `x` followed by all elements of `xs`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.prepend("a", ["b", "c"])
["a", "b", "c"]

> data.prepend("a", [])
["a"]

> data.prepend("a", nil)
nil
```
~~~

  function prepend: (x, list xs) -> list                    via {:class "com.twineworks.tweakflow.std.Data$prepend"};

doc
~~~
`(list xs, x) -> list`

Returns a list consisting of all elements of `xs` followed by `x`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.append(["a", "b"], "c")
["a", "b", "c"]

> data.append([], "a")
["a"]

> data.append(nil, "a")
nil
```
~~~
  function append: (list xs, x) -> list                     via {:class "com.twineworks.tweakflow.std.Data$append"};

doc
~~~
`(list xs, function p) ->`

Tests each `x` in `xs` in order by passing them to predicate function `p`.

If `p` accepts a single argument, `p(x)` is evaluated.
If `p` accepts two arguments, `p(x, i)` is evaluated, where `i` is the index of `x` in `xs`.

Returns the first `x` for which `p` evaluates to a value that casts to `boolean` `true`.

Returns `nil` if there is no `x` in `xs` such that `p` evaluates to a value that casts to `boolean` `true`.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.find([1, 2, 3, 4, 5], (x) -> x >= 3)
3

# find first even number on an even index
> data.find([1, 2, 1, 7, 6, 5, 3], (x, i) -> (i % 2 == 0) && (x % 2 == 0))
6

> data.find([1, 2, 3, 4, 5], (x) -> x >= 10)
nil

> data.find(nil, (x) -> true)
nil

> data.find([], nil)
  code: NIL_ERROR
  message: predicate function cannot be nil
```
~~~
  function find: (list xs, function p) ->                   via {:class "com.twineworks.tweakflow.std.Data$find"};

doc
~~~
`(list xs, function p) -> long`

Tests each `x` in `xs` in order by passing them to predicate function `p`.

If `p` accepts a single argument, `p(x)` is evaluated.
If `p` accepts two arguments, `p(x, i)` is evaluated, where `i` is the index of `x` in `xs`.

Returns the first index for which `p` evaluates to a value that casts to `boolean` `true`.

Returns `nil` if there is no `x` in `xs` such that `p` evaluates to a value that casts to `boolean` `true`.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.find_index([1, 2, 3, 4, 5], (x) -> x >= 3)
2

# find index of first even number on an even index
> data.find_index([1, 2, 1, 7, 6, 5, 3], (x, i) -> (i % 2 == 0) && (x % 2 == 0))
4

> data.find_index([1, 2, 3, 4, 5], (x) -> x >= 10)
nil

> data.find_index(nil, (x) -> true)
nil

> data.find_index([], nil)
ERROR:
  code: NIL_ERROR
  message: predicate function cannot be nil
```
~~~
  function find_index: (list xs, function p) -> long        via {:class "com.twineworks.tweakflow.std.Data$findIndex"};


doc
~~~
`(list xs, long i, v) ->`

Returns a list consisting of all elements of `xs`, with elements starting at index `i` shifted to the right, and `v` inserted at position `i`.
If `i` is bigger than the largest index in `xs`, any intermediate indexes are created with value `nil`.

Returns `nil` if `xs` is nil.

Throws an error if index is negative or `nil`.

```tweakflow
> data.insert([], 0, "a")
["a"]

> data.insert(["a", "b", "d"], 2, "c")
["a", "b", "c", "d"]

> data.insert([], 3, "a")
[nil, nil, nil, "a"]

> data.insert(nil, 0, "a")
nil

> data.insert([], -2, "a")
ERROR:
  code: INDEX_OUT_OF_BOUNDS
  message: cannot insert at index -2

> data.insert([], nil, "a")
ERROR:
  code: NIL_ERROR
  message: cannot insert at nil index

```
~~~

  function insert: (list xs, long i, v) ->                  via {:class "com.twineworks.tweakflow.std.Data$insert"};

doc
~~~
`(xs, key) -> `

Given a `list` or `dict` `xs`, returns a collection of the same time consisting of all elements of `xs`, except for the element at `key`.
If `xs` is a list, any elements past the deleted index are shifted to the left.

If `xs` does not have the given `key`, `xs` is returned.

Returns `nil` if `xs` is nil.\
Returns `xs` if `key` is `nil`.

Throws an error if `xs` is a list and `key` cannot be cast to a `long`. \
Throws an error if `xs` is a dict and `key` cannot be cast to a `string`.

```tweakflow
> data.delete(["a", "b", "c"], 0)
["b", "c"]

> data.delete(["a", "b", "c"], 1)
["a", "c"]

> data.delete(["a", "b", "c"], 10)
["a", "b", "c"]

> data.delete({:a "1", :b "2"}, :a)
{
  :b "2"
}

> data.delete(nil, 0)
nil

> data.delete([0, 1], nil)
[0, 1]

> data.delete([], -2)
ERROR:
  code: INDEX_OUT_OF_BOUNDS
  message: cannot delete index -2
```
~~~

  function delete: (xs, key) ->                             via {:class "com.twineworks.tweakflow.std.Data$delete"};

doc
~~~
`(xs, list keys, not_found) ->`

Given a `list` or `dict` `xs`, returns a collection of the same type, with elements at all `keys` from `xs`.
If any `key` is not present in `xs`, `not_found` is included in the result instead.

Returns `nil` if `xs` is `nil` or `keys` is `nil`.

Throws an error if `xs` is a `list` and a `key` cannot be cast to `long`.\
Throws an error if `xs` is a `dict` and a `key` cannot be cast to `string`.

```tweakflow
> data.select(["a", "b", "c"], [0, 2])
["a", "c"]

> data.select(["a", "b", "c"], [0, 2, 10])
["a", "c", nil]

> data.select(["a", "b", "c"], [0, 2, 10], "z")
["a", "c", "z"]

> data.select({:a "1", :b "2", :c "3"}, [:a, :c, :d], "0")
{
  :a "1",
  :c "3",
  :d "0"
}

> data.select(nil, [0, 1])
nil

> data.select([0, 1], nil)
nil
```
~~~

  function select: (xs, list keys, not_found) ->            via {:class "com.twineworks.tweakflow.std.Data$select"};

doc
~~~
`(dict xs, list keys) ->`

Returns a copy of `xs` in which all keys given in `keys` are omitted.

Returns `nil` if `xs` is `nil` or `keys` is `nil`.

Throws an error if any key in `keys` cannot be cast to `string`.

```tweakflow
> data.omit({:a 1, :b 2, :c 3}, ["a", "c"])
{
  :b 2
}

> data.omit({:a 1, :b 2, :c 3}, ["a", "foo"])
{
  :b 2,
  :c 3
}

> data.omit(nil, ["a"])
nil

> data.omit({:a 1, :b 2}, nil)
nil
```
~~~

  function omit: (dict xs, list keys) ->            via {:class "com.twineworks.tweakflow.std.Data$omit"};

doc
~~~
`(xs, function p) ->`

Given a `list` or `dict` `xs`, returns a collection of the same type,
with all elements for which predicate function `p` returns a value that casts to `boolean` `true`.

If `p` accepts one argument, `x` is passed. If `p` accepts two arguments, `x` and the key or index of `x` in `xs` is passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.\
Throws an error if `p` is `nil`.

```tweakflow
> data.filter([0,1,2,3,4,5,6,7,8], (x) -> x % 2 == 0)
[0, 2, 4, 6, 8]

> data.filter(["a", "b", "c", "d", "e", "f", "g"], (x, i) -> i % 2 == 0)
["a", "c", "e", "g"]

> data.filter({:a 1, :b 2, :c 3, :d 4, :e 5, :f 6}, (x) -> x % 3 == 0)
{
  :c 3,
  :f 6
}

> data.filter({:a 1, :b 2, :c 3, :d 4, :e 5, :f 6}, (x, k) -> k == "d" || x % 3 == 0)
{
  :c 3,
  :d 4,
  :f 6
}

> data.filter(nil, (x) -> true)
nil

> data.filter([], nil)
ERROR:
  code: NIL_ERROR
  message: predicate function cannot be nil

> data.filter(1, (x) -> true)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: filter is not defined for type long
```
~~~

  function filter: (xs, function p) ->                      via {:class "com.twineworks.tweakflow.std.Data$filter"};

doc
~~~
`(list xs, seed) -> list`

Returns a list with all elements of `xs` in random order. The reordering of items is based on `seed`.
The same seed will shuffle lists of equal length in the same way.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.shuffle([1, 2, 3, 4, 5, 6], "hello")
[5, 2, 6, 4, 1, 3]

> data.shuffle(["a", "b", "c", "d", "e", "f"], "hello")
["e", "b", "f", "d", "a", "c"]

> data.shuffle([1, 2, 3, 4, 5, 6], "foo")
[1, 5, 2, 4, 3, 6]

> data.shuffle(["a", "b", "c", "d", "e", "f"], "foo")
["a", "e", "b", "d", "c", "f"]

> data.shuffle(nil, "seed")
nil
```
~~~

  function shuffle: (list xs, seed) -> list                 via {:class "com.twineworks.tweakflow.std.Data$shuffle"};

doc
~~~
`(list xs) -> list `

Returns a list with all duplicates in `xs` removed. Values are considered duplicates when they compare as equal
using the `===` operator.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.unique([1, 1, 2, 3, 3])
[1, 2, 3]

> data.unique([1.0, 1, 2.0, 2, 3.0, 3])
[1.0, 1, 2.0, 2, 3.0, 3]

> data.unique(["foo", "bar", "foo"])
["foo", "bar"]

> data.unique([{:a "a", :b "b"}, {:b "b", :a "a"}])
[{
  :a "a",
  :b "b"
}]

> data.unique(nil)
nil
```
~~~

  function unique: (list xs) -> list                        via {:class "com.twineworks.tweakflow.std.Data$unique"};

doc
~~~
`(long start=0, long end=0) -> list`

Returns a list with long values starting at `start`, and ending at `end`.

Returns an empty list if `start > end`.

Returns `nil` if `start` is `nil` or `end` is `nil`.

```tweakflow
> data.range(0, 2)
[0, 1, 2]

> data.range(-3, 3)
[-3, -2, -1, 0, 1, 2, 3]

> data.range(1, 1)
[1]

> data.range(10, 1)
[]

> data.range(nil, 0)
nil

> data.range(0, nil)
nil
```
~~~

  function range: (long start=0, long end=0) -> list          via {:class "com.twineworks.tweakflow.std.Data$range"};

doc
~~~
`(list xs, function p) -> boolean`

Tests items `x` in `xs` with predicate function `p`. If `p` accepts one argument, `x` is passed. If `p` accepts
two arguments, `x` and the index of `x` in `xs` are passed.

Returns true if `p` returns a value that casts to boolean `true` for any `x` in `xs`.
Returns false otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.any?([1,2,3,4,5], (x) -> x % 2 == 0)
true

> data.any?([1,3,5], (x) -> x % 2 == 0)
false

> data.any?([1,3,5], (x, i) -> x == i)
false

> data.any?([1,0,2], (x, i) -> x == i)
true

> data.any?([], (x) -> true)
false

> data.any?(nil, (x) -> true)
nil
```
~~~

  function any?: (list xs, function p) -> boolean           via {:class "com.twineworks.tweakflow.std.Data$any"};

doc
~~~
`(list xs, function p) -> boolean`

Tests items `x` in `xs` with predicate function `p`. If `p` accepts one argument, `x` is passed. If `p` accepts
two arguments, `x` and the index of `x` in `xs` are passed.

Returns false if `p` returns a value that casts to boolean `true` for any `x` in `xs`.
Returns true otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.none?([1,2,3,4,5], (x) -> x % 2 == 0)
false

> data.none?([1,3,5], (x) -> x % 2 == 0)
true

> data.none?([1,3,5], (x, i) -> x == i)
true

> data.none?([1,0,2], (x, i) -> x == i)
false

> data.none?([], (x) -> true)
true

> data.none?(nil, (x) -> true)
nil
```
~~~

  function none?: (list xs, function p) -> boolean          via {:class "com.twineworks.tweakflow.std.Data$none"};

doc
~~~
`(list xs, function p) -> boolean`

Tests items `x` in `xs` with predicate function `p`. If `p` accepts one argument, `x` is passed. If `p` accepts
two arguments, `x` and the index of `x` in `xs` are passed.

Returns true if `p` returns a value that casts to boolean `true` for all `x` in `xs`.
Returns true if `xs` is empty.

Returns false otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.all?([1,2,3,4,5], (x) -> x % 2 == 0)
false

> data.all?([2,4,6], (x) -> x % 2 == 0)
true

> data.all?([1,3,5], (x, i) -> x == i)
false

> data.all?([0,1,2], (x, i) -> x == i)
true

> data.all?([], (x) -> false)
true

> data.all?(nil, (x) -> false)
nil
```
~~~

  function all?: (list xs, function p) -> boolean           via {:class "com.twineworks.tweakflow.std.Data$all"};

doc
~~~
`(list xs) -> list`

Returns a list containing all elements of `xs` with the exception of the last one.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```tweakflow
> data.init([1,2,3])
[1, 2]

> data.init(nil)
nil

> data.init([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
```
~~~

  function init: (list xs) -> list                          via {:class "com.twineworks.tweakflow.std.Data$init"};

doc
~~~
`(list xs) -> list`

Returns a list containing all elements of `xs` with the exception of the first one.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```tweakflow
> data.tail([1,2,3])
[2, 3]

> data.tail(nil)
nil

> data.tail([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
```
~~~

  function tail: (list xs) -> list                          via {:class "com.twineworks.tweakflow.std.Data$tail"};

doc
~~~
`(list xs) -> any`

Returns the first element in `xs`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```tweakflow
> data.head([1,2,3])
1

> data.head(nil)
nil

> data.head([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
```
~~~

  function head: (list xs) -> any                           via {:class "com.twineworks.tweakflow.std.Data$head"};

doc
~~~
`(list xs) -> any`

Returns the last element in `xs`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```tweakflow
> data.last([1,2,3])
3

> data.last(nil)
nil

> data.last([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
```
~~~

  function last: (list xs) -> any                           via {:class "com.twineworks.tweakflow.std.Data$last"};

doc
~~~
`(list xs, long start=0, long end=nil) -> list`

Returns a sublist of `xs` starting at index `start` inclusively, and extending to index `end` exclusively.
If `end` is `nil`, the sublist extends to the end of `xs`.

Returns an empty list if `start` is greater than the last index in `xs`.
Returns an empty list if `start >= end`.

Returns `nil` if `xs` is `nil`.

Throws an error if `start` is `nil` or `start < 0`.

```tweakflow
> data.slice([1,2,3,4,5], 3)
[4, 5]

> data.slice(["a", "b", "c", "d", "e"], 1, 4)
["b", "c", "d"]

> data.slice(["a", "b"], 10, 20)
[]

> data.slice([1,2,3], 1, 1)
[]

> data.slice(nil, 0, 1)
nil

```
~~~

  function slice: (list xs, long start=0, long end=nil) -> list via {:class "com.twineworks.tweakflow.std.Data$slice"};

doc
~~~
`(list xs, long s=1) -> list`

Partitions `xs` into slices. Returns a list of slices, each of size `s`. The last slice may contain less than `s` items.

Returns an empty list if `xs` is empty.

Returns `nil` if any argument is `nil`.

Throws an error if `s <= 0`.


```tweakflow
> data.slices([1,2,3,4,5,6,7,8,9,0], 3)
[[1, 2, 3], [4, 5, 6], [7, 8, 9], [0]]
> data.slices(nil)
nil
> data.slices([])
[]
> data.slices([], 4)
[]
> data.slices([1,2,3], 0)
ERROR:
  code: CUSTOM_ERROR
  message: CUSTOM_ERROR
  at: std.tf:2109:20
  source: throw {:message "s must be positive, was #{s}" :code "ILLEGAL_ARGUMENT"}
  value: {
  :message "s must be positive, was 0",
  :code "ILLEGAL_ARGUMENT"
}
```
~~~

  function slices: (list xs, long s=1) -> list
    if (xs == nil) || (s == nil) then nil else
    if s <= 0 then throw {:message "s must be positive, was #{s}", :code "ILLEGAL_ARGUMENT"} else
    if empty?(xs) then [] else
    let {
      slice_count: math.ceil(size(xs)/s);
      init: {:xs xs, :slices []};
      iteration: (a) -> {
                  :xs drop(s, a[:xs]),
                  :slices [...(a[:slices]), take(s, a[:xs])]
                 };
    }
    (fun.times(slice_count, init, iteration)[:slices]);

doc
~~~
`(list xs) -> list`

Returns a list that contains all elements of `xs` in reverse order.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.reverse([1, 2, 3])
[3, 2, 1]

> data.reverse(nil)
nil
```
~~~

  function reverse: (list xs) -> list                       via {:class "com.twineworks.tweakflow.std.Data$reverse"};

doc
~~~
`(list xs, function f) -> list`

Returns a list sorted according to comparator function f. The sorting algorithm is guaranteed to be stable.

The comparator function accepts two arguments: a and b. \
If a < b, f returns a negative number.\
If a > b, f returns a positive number.\
If a == b, f returns 0.

Returns `nil` if any argument is `nil`.

```tweakflow
> data.sort([1, 4, 5, 3, 6, 7], math.compare)
[1, 3, 4, 5, 6, 7]

# sorts numbers by numeric value
> data.sort([110, 221, 12, 121, 222, 33], math.compare)
[12, 33, 110, 121, 221, 222]

# sorts numbers as strings
> data.sort([12, 33, 110, 121, 221, 222], strings.comparator())
[110, 12, 121, 221, 222, 33]

# sorts records by id
> data.sort([{:id 872, :name "foo"}, {:id 261, :name "bar"}], (a, b) -> math.compare(a[:id], b[:id]))
[{
  :name "bar",
  :id 261
}, {
  :name "foo",
  :id 872
}]
```
~~~

  function sort: (list xs, function f) -> list              via {:class "com.twineworks.tweakflow.std.Data$sort"};

doc
~~~
`(long n, x) -> list`

Returns a list of length `n` that contains `x` in every position.

Returns `nil` if `n` is `nil`.

Throws an error if `n` is negative.

```tweakflow
> data.repeat(3, "foo")
["foo", "foo", "foo"]

> data.repeat(0, "foo")
[]

> data.repeat(nil, "foo")
nil

> data.repeat(-2, "foo")
ERROR:
  code: INDEX_OUT_OF_BOUNDS
  message: Cannot repeat -2 times
```
~~~

  function repeat: (long n, x) -> list                    via {:class "com.twineworks.tweakflow.std.Data$repeat"};

doc
~~~
`(list lists) -> list`

Returns a list that contains all elements of all `lists`. Lists are concatenated in given order.

Returns `nil` if `lists` is `nil`. \
Returns `nil` if any element of `lists` is `nil`.

Throws an error if any element of `lists` is not a `list`.

```tweakflow
> data.concat([[1,2,3], [4,5,6], [7,8,9]])
[1, 2, 3, 4, 5, 6, 7, 8, 9]

> data.concat([[1,nil,3], [nil,5,6], [7,nil,nil]])
[1, nil, 3, nil, 5, 6, 7, nil, nil]

> data.concat([[1,2,3], nil, [7,8,9]])
nil

> data.concat(nil)
nil

> data.concat(["foo", "bar"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot concat type string
```
~~~

  function concat: (list lists) -> list                     via {:class "com.twineworks.tweakflow.std.Data$concat"};

doc
~~~
`(list dicts) -> dict`

Returns a `dict` that contains all entries from all given `dicts`. Dicts are merged left to right.
In case of duplicate keys across `dicts`, the rightmost occurrence takes precedence.

Returns `nil` if `dicts` is `nil`. \
Returns `nil` if any element of `dicts` is `nil`.

Throws an error if any element of `dicts` is not a `dict`.

```tweakflow
> data.merge([{:a 1}, {:b 2}, {:c 3}])
{
  :a 1,
  :b 2,
  :c 3
}

> data.merge([{:a 1}, {:b 2}, {:a 99}])
{
  :a 99,
  :b 2
}

> data.merge([{:a 1}, nil])
nil

> data.merge(nil)
nil

> data.merge(["foo", "bar"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot merge type string
```
~~~

  function merge: (list dicts) -> dict                      via {:class "com.twineworks.tweakflow.std.Data$merge"};

doc
~~~
`(long n, list xs) -> list`

Returns a list containing the first `n` elements of `xs`.
Returns `xs` if `n` is greater than the size of `xs`.
Returns an empty list if `n` is negative.

Returns `nil` if `n` is `nil` or `xs` is `nil`.

```tweakflow
> data.take(2, ["a", "b", "c", "d"])
["a", "b"]

> data.take(10, ["a", "b"])
["a", "b"]

> data.take(0, ["a", "b", "c"])
[]

> data.take(-1, ["a", "b", "c"])
[]

> data.take(1, nil)
nil

> data.take(nil, ["a", "b", "c"])
nil
```
~~~

  function take: (long n, list xs) -> list                  via {:class "com.twineworks.tweakflow.std.Data$take"};

doc
~~~
`(function p, list xs) -> list`

Returns a list containing leading elements `x` of `xs` for which the predicate function `p` returns a value that casts to boolean `true`.
If `p` accepts one argument, `x` is passed.  If `p` accepts two arguments, `x` and the index of `x` are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow

> data.take_while((x) -> x < 5, [2, 3, 3, 2, 5, 1, 2])
[2, 3, 3, 2]

> data.take_while((x) -> x % 2 == 0, [0, 2, 4, 6, 7, 8, 9, 10])
[0, 2, 4, 6]

> data.take_while((x) -> x % 2 == 0, [1, 2, 3, 4])
[]

> data.take_while((x) -> true, nil)
nil
```
~~~
  function take_while: (function p, list xs) -> list
    via {:class "com.twineworks.tweakflow.std.Data$take_while"};

doc
~~~
`(function p, list xs) -> list`

Returns a list containing leading elements `x` of `xs` for which the predicate function `p` returns a value that does not cast to boolean `true`.
If `p` accepts one argument, `x` is passed.  If `p` accepts two arguments, `x` and the index of `x` are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow

> data.take_until((x) -> x >= 5, [2, 3, 3, 2, 5, 1, 2])
[2, 3, 3, 2]

> data.take_until((x) -> x % 2 != 0, [0, 2, 4, 6, 7, 8, 9, 10])
[0, 2, 4, 6]

> data.take_until((x) -> x % 2 != 0, [1, 2, 3, 4])
[]

> data.take_until((x) -> x > 10, [1, 2, 3, 4])
[1, 2, 3, 4]

> data.take_until((x) -> false, nil)
nil
```
~~~
  function take_until: (function p, list xs) -> list
    via {:class "com.twineworks.tweakflow.std.Data$take_until"};

doc
~~~
`(long n, list xs) -> list`

Returns a list of elements in `xs` skipping the first `n` elements.
Returns an empty list if `n` is greater than the size of `xs`.
Returns `xs` if `n` is negative.

Returns `nil` if `n` is `nil` or `xs` is `nil`.

```tweakflow
> data.drop(2, ["a", "b", "c", "d"])
["c", "d"]

> data.drop(10, ["a", "b"])
[]

> data.drop(0, ["a", "b", "c"])
["a", "b", "c"]

> data.drop(-3, ["a", "b", "c"])
["a", "b", "c"]

> data.drop(1, nil)
nil

> data.drop(nil, ["a", "b", "c"])
nil
```
~~~
  function drop: (long n, list xs) -> list                  via {:class "com.twineworks.tweakflow.std.Data$drop"};

doc
~~~
`(function p, list xs) -> list`

Returns a sublist of elements of `xs` skipping leading elements `x` for which the predicate function `p` returns a value that casts to boolean `true`.
If `p` accepts one argument, `x` is passed.  If `p` accepts two arguments, `x` and the index of `x` are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.drop_while((x) -> x <= 2, [1, 2, 3, 4])
[3, 4]

> data.drop_while((x) -> x > 10, [1, 2, 3])
[1, 2, 3]

> data.drop_while((x) -> false, nil)
nil

```
~~~

  function drop_while: (function p, list xs) -> list
    via {:class "com.twineworks.tweakflow.std.Data$drop_while"};

doc
~~~
`(function p, list xs) -> list`

Returns a sublist of elements of `xs` skipping leading elements `x` for which the predicate function `p` returns a value that does not cast to boolean `true`.
If `p` accepts one argument, `x` is passed.  If `p` accepts two arguments, `x` and the index of `x` are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```tweakflow
> data.drop_until((x) -> x > 2, [1, 2, 3, 4])
[3, 4]

> data.drop_until((x) -> x <= 2, [1, 2, 3, 4])
[1, 2, 3, 4]

> data.drop_until((x) -> x > 10, [1, 2, 3, 4])
[]

> data.drop_until((x) -> false, nil)
nil

```
~~~

  function drop_until: (function p, list xs) -> list
    via {:class "com.twineworks.tweakflow.std.Data$drop_until"};

doc
~~~
`(xs, x) -> boolean`

If `xs` is a `list`, returns `true` if for any element `e` of `xs` `x === e` evaluates to `true`, `false` otherwise.

If `xs` is a `dict`, returns `true` if for any value `v` in `xs` `v === e` evaluates to `true`, `false` otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```tweakflow
> data.contains?([1,2,3], 2)
true

> data.contains?([1,2,3], 4)
false

> data.contains?({:a 1, :b 2, :c 3}, 2)
true

> data.contains?({:a 1, :b 2, :c 3}, 4)
false

# NaN never compares as equal
> data.contains?([NaN], NaN)
false

# functions never compare as equal
> let {f: () -> true;} data.contains?([f], f)
false

> data.contains?(nil, 0)
nil

> data.contains?("foo", "bar")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: contains? is not defined for type string
  ...
```
~~~

  function contains?: (xs, x) -> boolean                       via {:class "com.twineworks.tweakflow.std.Data$contains"};

doc
~~~
`(list xs, x, long start=0) -> long`

If `xs` contains `x` at or after index `start` returns the index of the earliest such occurrence. Returns `-1` if `x` is not an element of `xs`. Each element of `xs` is compared to `x` using the `===` operator.

Returns `nil` if `xs` is `nil`.\
Returns `nil` if `start` is `nil`.

```tweakflow
> data.index_of([1,2,3], 2)
1

# 2 !== 2.0
> data.index_of([1,2,3], 2.0)
-1

> data.index_of([1,2,3], 4)
-1

> data.index_of([1,2,3], 2, 2)
-1

> data.index_of([1,2,3,1,2,3], 3, 4)
5

# NaN === NaN is false
> data.index_of([NaN], NaN)
-1

# f === f is false for functions
> let {f: () -> true;} data.index_of([f], f)
-1

> data.index_of(nil, 2)
nil

> data.index_of([1,2,3], 3, nil)
nil
```
~~~

  function index_of: (list xs, x, long start=0) -> long         via {:class "com.twineworks.tweakflow.std.Data$indexOf"};

doc
~~~
`(list xs, x, long end=nil) -> long`

If `xs` contains `x` at or before index `end` returns the index of the last such occurrence. Returns `-1` if `x` is not an element of `xs`. Each element of `xs` is compared to `x` using the `===` operator.

If `end` is `nil`, it is interpreted as the last index of `xs`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.last_index_of([1,2,3], 2)
1

> data.last_index_of([1,2,3,1,2,3], 2)
4

> data.last_index_of([1,2,3,1,2,3], 2, 4)
4

> data.last_index_of([1,2,3,1,2,3], 2, 3)
1

> data.last_index_of([1,2,3], 4)
-1

> data.last_index_of([1,2,3,1,2,3], 3, 1)
-1

# 2 !== 2.0
> data.last_index_of([1,2,3], 2.0)
-1

# NaN === NaN is false
> data.last_index_of([NaN], NaN)
-1

# f === f is false for functions
> let {f: () -> true;} data.last_index_of([f], f)
-1

> data.last_index_of(nil, 2)
nil
```
~~~

  function last_index_of: (list xs, x, long end=nil) -> long   via {:class "com.twineworks.tweakflow.std.Data$lastIndexOf"};

doc
~~~
`(dict xs, x) -> string`

If `xs` contains an entry with value `x`, returns the key of that entry.
If there are multiple such entries, it is undefined which of the keys is returned.
Each value in `xs` is compared to `x` using the `===` operator.

Returns `nil` if `xs` does not contain an entry with value `x`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.key_of({:a 1, :b 2}, 1)
"a"

> data.key_of({:a 1, :b 2}, 3)
nil

> data.key_of({:foo 1, :bar 1}, 1)
"foo"

> data.key_of({:foo 1, :doo 1}, 1)
"doo"

# NaN === NaN is false
> data.key_of({:foo NaN}, NaN)
nil

# f === f is false, functions do not compare as equal
> let {f: () -> true;} data.key_of({:f f}, f)
nil

> data.key_of(nil, 1)
nil
```
~~~

  function key_of: (dict xs, x) -> string                   via {:class "com.twineworks.tweakflow.std.Data$keyOf"};

doc
~~~
`(list xs) -> list`

Builds a result list iterating over all elements `x` in `xs`. If `x` is a list, it is concatenated to the result list.
Otherwise the element is appended to the list. Returns the result list.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.flatten([[1, 2, 3], 4, 5, [6, 7, 8]])
[1, 2, 3, 4, 5, 6, 7, 8]

> data.flatten([1, 2, 3])
[1, 2, 3]

> data.flatten([1, nil, [], "foo"])
[1, nil, "foo"]

> data.flatten([[[1]], [[2]]])
[[1], [2]]

> data.flatten(nil)
nil
```
~~~

  function flatten: (list xs) -> list
    (reduce(xs, [], (a, x) -> if x is list then [...a, ...x] else [...a, x]));

doc
~~~
`(xs, function f) -> any`

If `xs` is a `list`, returns a `list` of all `x` in `xs` mapped through `f`.
If `f` accepts one argument, `x` is passed.
If `f` accepts two arguments, `x` and its index are passed.

If `xs` is a `dict`, returns a `dict` of all entries mapped through `f`.
If `f` accepts one argument, each entry's value is passed.
If `f` accepts two arguments, each entry's value and key are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `f` is `nil`;

```tweakflow
> data.map([1,2,3], (x) -> x*x)
[1, 4, 9]

> data.map(["a", "b", "c"], (x, i) -> i.." -> "..x)
["0 -> a", "1 -> b", "2 -> c"]

> data.map({:a 1, :b 2, :c 3}, (x) -> x*x)
{
  :a 1,
  :b 4,
  :c 9
}

> data.map({:a 1, :b 2, :c 3}, (x, k) -> k.." -> "..x)
{
  :a "a -> 1",
  :b "b -> 2",
  :c "c -> 3"
}

> data.map(nil, (x) -> x)
nil

> data.map([1, 2, 3], nil)
nil

> data.map("foo", (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: map is not defined for type string
```
~~~

  function map: (xs, function f) -> via {:class "com.twineworks.tweakflow.std.Data$map"};

doc
~~~
`(xs, function f) -> list`

Expects `xs` to be a `list` or a `dict`. Builds a result list by mapping each `x` of `xs`
through `f`. If a mapped value is a list, it is concatenated at the end of the result list.
Non-lists are appended to the result list.

If `f` accepts a single argument, `x` is passed.
If `f` accepts two arguments, `x` and its index or key are passed.

Returns the result list.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `f` is `nil`.

```tweakflow
> data.flatmap([1, 2, 3], (x) -> data.repeat(x, x))
[1, 2, 2, 3, 3, 3]

> data.flatmap([1, 2, 1], (x) -> if x == 1 then ["a", "b", "c"] else "-")
["a", "b", "c", "-", "a", "b", "c"]

> data.flatmap(nil, (x) -> x)
nil

> data.flatmap("foo", (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: map is not defined for type string
  ...
```
~~~

  function flatmap: (xs, function f) -> list
    (reduce(map(xs, f), [], (a, x) -> if x is list then [...a, ...x] else [...a, x]));

doc
~~~
`(xs, function f) -> list`

Expects `xs` to be a `list` or a `dict`. Builds a result list by mapping each `x` of `xs`
through `f`. If a mapped value is a list, it is concatenated at the end of the result list.
Non-lists are discarded.

If `f` accepts a single argument, `x` is passed.
If `f` accepts two arguments, `x` and its index or key are passed.

Returns the result list.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `f` is `nil`.

```tweakflow
> data.mapcat([1, 2, 3], (x) -> data.repeat(x, x))
[1, 2, 2, 3, 3, 3]

> data.mapcat([1, 2, 1], (x) -> if x == 1 then ["a", "b", "c"] else "nothing")
["a", "b", "c", "a", "b", "c"]

> data.mapcat(nil, (x) -> x)
nil

> data.mapcat("foo", (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: map is not defined for type string
  ...
```
~~~
  function mapcat: (xs, function f) -> list
    (reduce(map(xs, f), [], (list a, x) -> if x is list then [...a, ...x] else a));

doc
~~~
`(list xs, list ys) -> list`

Returns a list of same length as `xs`. Each index `i` contains the value `[xs[i], ys[i]]`.

Returns `nil` if `xs` or `ys` are `nil`.

```tweakflow
> data.zip(["a", "b", "c"], [1, 2, 3])
[["a", 1], ["b", 2], ["c", 3]]

> data.zip([1, 2], [1, 2, 3])
[[1, 1], [2, 2]]

> data.zip([1, 2, 3], [1, 2])
[[1, 1], [2, 2], [3, nil]]

> data.zip([1, 2], nil)
nil

> data.zip(nil, [1, 2])
nil
```
~~~

  function zip: (list xs, list ys) -> list
    if xs == nil then nil else
    if ys == nil then nil else
    (reduce(xs, [], (z, a, i) -> [...z, [a, ys[i]]]));


doc
~~~
`(list keys, list values) -> dict`

Returns a `dict` with all keys from `keys`. Each key contains the value `values[i]`, where `i`
is the index of the key in `keys`.

In case there are duplicate keys in `keys`, the last index of a key provides the corresponding value.

Returns `nil` if `keys` or `values` are `nil`.

Throws an error if any key cannot be cast to string.

```tweakflow
> data.zip_dict(["a", "b", "c"], [1, 2, 3])
{
  :a 1,
  :b 2,
  :c 3
}

> data.zip_dict(["a", "b"], [1, 2, 3])
{
  :a 1,
  :b 2
}


> data.zip_dict(["a", "b", "c"], [1, 2])
{
  :a 1,
  :b 2,
  :c nil
}

> data.zip_dict(["a", "b", "a", "b"], [1, 2, 3, 4])
{
  :a 3,
  :b 4
}

> data.zip_dict(["a", "b"], nil)
nil

> data.zip_dict(nil, [1, 2])
nil
```
~~~

  function zip_dict: (list keys, list values) -> dict
    if keys == nil then nil else
    if values == nil then nil else
    (reduce(keys, {}, (a, k, i) -> (put(a, k, values[i]))));

doc
~~~
`(xs, function f) -> dict`

Returns a `dict` that contains all values from `xs`. Each `x` in `xs` is indexed by key `f(x)`.

If `xs` is a list:\
If `f` accepts one argument, `x` is passed.\
If `f` accepts two arguments, `x` and its index are passed.

If `xs` is a dict:\
If `f` accepts one argument, each entry's value is passed.\
If `f` accepts two arguments, each entry's value and key are passed.

The indexing function `f` must return a string, or a value that can be cast to string.

In case `f(x)` returns the same key for multiple `x`, only one of such `x` will be indexed in the returned dict.
In such cases it is undefined which `x` is preserved in the returned dict.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `f` is `nil`.

```tweakflow
> data.index_by([{:id 'id_1', :name "Sherlock Holmes"}, {:id 'id_2', :name "Dr. Watson"}], (x) -> x[:id])
{
  :id_1 {
    :name "Sherlock Holmes",
    :id "id_1"
  },
  :id_2 {
    :name "Dr. Watson",
    :id "id_2"
  }
}

> data.index_by({:a1 1, :a2 2, :a3 3}, (x) -> "b"..x)
{
  :b1 1,
  :b2 2,
  :b3 3
}

> data.index_by({:a1 1, :a2 2, :a3 3}, (x, k) -> "_" .. k)
{
  :_a1 1,
  :_a2 2,
  :_a3 3
}
```

~~~

  function index_by: (xs, function f) -> any                via {:class "com.twineworks.tweakflow.std.Data$index_by"};

doc
~~~
`(list xs, s) -> list`

Returns a `list` with all elements from `xs` separated by `s`.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.interpose([1, 2, 3, 4], 0)
[1, 0, 2, 0, 3, 0, 4]

> data.interpose([1], "foo")
[1]

> data.interpose([], "foo")
[]

> data.interpose(nil, "foo")
nil
```
~~~

  function interpose: (list xs, s) -> list
    if xs == nil then nil else
    (reduce(xs, [],          # build a new list
      (a, x, i) ->
        if i == 0 then
          [x]
        else               # first item remains as is
          [...a, s, x]      # follow-up items are preceded with seperator
    ));

doc
~~~
`(xs, init, function f) -> any`

Expects `xs` to be a `list` or a `dict`.

Sets the initial accumulator value to `init`, iterates through all `xs`, and calls `f` for each `x`.

If `f` accepts two arguments, the current accumulator value and `x` are passed.
If `f` accepts three arguments, the current accumulator value, `x`, and the index or key of `x` are passed.

After each call the return value of `f` becomes the new accumulator value.

Returns final accumulator value after iteration over `xs` is completed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `f` is `nil`.

```tweakflow
> data.reduce([1,2,3], 0, (a, x) -> a+x)
6

> data.reduce([], 0, (a, x) -> a+x)
0

> data.reduce(["a", "b", "c"], {}, (a, x, i) -> data.put(a, x, i))
{
  :a 0,
  :b 1,
  :c 2
}

> data.reduce({:a 1, :b 2, :c 3}, 0, (a, x) -> a+x)
6

> data.reduce({}, 0, (a, x) -> a+x)
0

> data.reduce({:a "alpha", :b "beta", :c "gamma"}, {}, (a, x, k) -> data.put(a, x, k))
{
  :gamma "c",
  :beta "b",
  :alpha "a"
}

> data.reduce(nil, 0, (a, x) -> x)
nil

> data.reduce("foo", 0, (a, x) -> a)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce is not defined for type string
  ...
```
~~~

  function reduce: (xs, init, function f) -> any                via {:class "com.twineworks.tweakflow.std.Data$reduce"};

doc
~~~
`(xs, init, function p, function f) -> any`

Expects `xs` to be a `list` or a `dict`.

Sets the initial accumulator value to `init`, and iterates through all `xs`.

For each `x`, calls predicate function `p` passing in the current accumulator value.
If the return value of `p` casts to boolean `true`, aborts iteration and returns the current
accumulator value immediately. Otherwise continues the reduction process by calling `f`.

If `f` accepts two arguments, the current accumulator value and `x` are passed.
If `f` accepts three arguments, the current accumulator value, `x`, and the index or key of `x` are passed.

After each call the return value of `f` becomes the new accumulator value.

Returns final accumulator value after iteration over `xs` is completed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `p` is `nil` or `f` is `nil`.

```tweakflow

# sum up integers, until sum exceeds 10
> data.reduce_until([1,2,3,4,5,6,7,8], 0, (a) -> a > 10, (a, x) -> a+x)
15

# initial value is checked
> data.reduce_until([1, 2, 3], 0, (a) -> true, (a, x) -> a+x)
0

> data.reduce_until({:a "alpha", :b "beta", :c "gamma"}, {}, (a) -> data.size(a) == 2, (a, x, k) -> data.put(a, x, k))
{
  :beta "b",
  :alpha "a"
}

> data.reduce_until(nil, 0, (a) -> true, (a, x) -> x)
nil

> data.reduce_until("foo", 0, (a) -> true, (a, x) -> a)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce_until is not defined for type string
```
~~~

  function reduce_until: (xs, init, function p, function f) ->  via {:class "com.twineworks.tweakflow.std.Data$reduce_until"};

doc
~~~
`(xs, init, function p, function f) -> any`

Expects `xs` to be a `list` or a `dict`.

Sets the initial accumulator value to `init`, and iterates through all `xs`.

For each `x`, calls predicate function `p` passing in the current accumulator value.
If the return value of `p` casts to boolean `true`, continues the reduction process by calling `f`.
Otherwise aborts iteration and returns the current accumulator value immediately.

If `f` accepts two arguments, the current accumulator value and `x` are passed.
If `f` accepts three arguments, the current accumulator value, `x`, and the index or key of `x` are passed.

After each call the return value of `f` becomes the new accumulator value.

Returns final accumulator value after iteration over `xs` is completed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

Throws an error if `p` is `nil` or `f` is `nil`.

```tweakflow

# sum up integers, until sum exceeds 10
> data.reduce_while([1,2,3,4,5,6,7,8], 0, (a) -> a <= 10, (a, x) -> a+x)
15

# initial value is checked
> data.reduce_while([1, 2, 3], 0, (a) -> false, (a, x) -> a+x)
0

> data.reduce_while({:a "alpha", :b "beta", :c "gamma"}, {}, (a) -> data.size(a) < 2, (a, x, k) -> data.put(a, x, k))
{
  :beta "b",
  :alpha "a"
}

> data.reduce_while(nil, 0, (a) -> true, (a, x) -> x)
nil

> data.reduce_while("foo", 0, (a) -> true, (a, x) -> a)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce_while is not defined for type string
```
~~~

  function reduce_while: (xs, init, function p, function f) ->  via {:class "com.twineworks.tweakflow.std.Data$reduce_while"};

}


doc
~~~
The time library provides functions for processing datetime values.
~~~
export library time {

doc
~~~
`datetime`

The instant of time at `1970-01-01T00:00:00Z`
~~~
  datetime epoch: 1970-01-01T00:00:00Z;

doc
~~~

```tweakflow
(
  long year=1970,
  long month=1,
  long day_of_month=1,
  long hour=0,
  long minute=0,
  long second=0,
  long nano_of_second=0,
  string tz="UTC"
) -> datetime
```

Returns a datetime with components set to the given arguments.

If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.
If resulting datetime is in a DST overlap, the earlier valid offset is used.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.of()
1970-01-01T00:00:00Z@UTC

> time.of(2019, 04, 10, tz: "Europe/Berlin")
2019-04-10T00:00:00+02:00@Europe/Berlin

> time.of(2019, 04, 10, 16, 23, 11, 999000000, "America/New_York")
2019-04-10T16:23:11.999-04:00@America/New_York

# DST gap
> time.of(2019, 03, 31, 2, 30, tz: "Europe/Berlin")
2019-03-31T03:30:00+02:00@Europe/Berlin

> time.of(nil)
nil
```
~~~
  function of: (long year=1970, long month=1, long day_of_month=1, long hour=0, long minute=0, long second=0, long nano_of_second=0, string tz="UTC") -> datetime via {:class "com.twineworks.tweakflow.std.Time$of"};

doc
~~~

```tweakflow
(
  datetime base,
  long hour=0,
  long minute=0,
  long second=0,
  long nano_of_second=0
) -> datetime
```

Returns datetime base with time components set to the given arguments.

If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.
If resulting datetime is in a DST overlap, the earlier valid offset is used.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.at(time.epoch, 23, 12, 10)
1970-01-01T23:12:10Z@UTC

> time.at(2019-04-10T00:00:00+02:00@Europe/Berlin, 6, 30)
2019-04-10T06:30:00+02:00@Europe/Berlin

# DST gap
> time.at(2019-03-31T00:00:00+01:00@Europe/Berlin, 2, 30)
2019-03-31T03:30:00+02:00@Europe/Berlin

> time.at(nil)
nil
```
~~~
  function at: (datetime base, long hour=0, long minute=0, long second=0, long nano_of_second=0) -> datetime
    ->> (base)
        (x) -> with_hour(x, hour),
        (x) -> with_minute(x, minute),
        (x) -> with_second(x, second),
        (x) -> with_nano_of_second(x, nano_of_second);

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full seconds between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.seconds_between(time.epoch, time.epoch)
0

> time.seconds_between(1970-01-01T00:00:00, 1970-01-01T00:00:01)
1

> time.seconds_between(1970-01-01T00:00:01, 1970-01-01T00:00:00)
-1

> time.seconds_between(time.epoch, 2018-01-01T00:00:00)
1514764800

> time.seconds_between(nil, nil)
nil
```
~~~
  function seconds_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$secondsBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full minutes between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.minutes_between(time.epoch, time.epoch)
0

> time.minutes_between(1970-01-01T00:00:00, 1970-01-01T00:01:00)
1

> time.minutes_between(1970-01-01T00:01:00, 1970-01-01T00:00:00)
-1

> time.minutes_between(time.epoch, 2018-01-01T00:00:00)
25246080

> time.minutes_between(nil, nil)
nil
```
~~~

  function minutes_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$minutesBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full hours between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.hours_between(time.epoch, time.epoch)
0

> time.hours_between(1970-01-01T00:00:00, 1970-01-01T01:00:00)
1

> time.hours_between(1970-01-01T01:00:00, 1970-01-01T00:00:00)
-1

> time.hours_between(time.epoch, 2018-01-01T00:00:00)
420768

> time.hours_between(nil, nil)
nil
```
~~~
  function hours_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$hoursBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full days between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.days_between(time.epoch, time.epoch)
0

> time.days_between(1970-01-01T, 1970-01-02T)
1

> time.days_between(1970-01-02T, 1970-01-01T)
-1

> time.days_between(time.epoch, 2018-01-01T)
17532

> time.days_between(nil, nil)
nil
```
~~~

  function days_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$daysBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full months between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.months_between(time.epoch, time.epoch)
0

> time.months_between(1970-01-01T, 1970-02-01T)
1

> time.months_between(1970-02-01T, 1970-01-01T)
-1

> time.months_between(time.epoch, 2018-01-01T)
576

> time.months_between(nil, nil)
nil
```
~~~

  function months_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$monthsBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> long
```

Returns the number of full years between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.years_between(time.epoch, time.epoch)
0

> time.years_between(1970-01-01T, 1971-01-01T)
1

> time.years_between(1971-01-01T, 1970-01-01T)
-1

> time.years_between(time.epoch, 2018-01-01T)
48

> time.years_between(nil, nil)
nil
```
~~~
  function years_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$yearsBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> dict
```

Returns a dict with long values on keys `:years`, `:months`, and `:days`, representing a calendar period between two datetimes.

The values are negative if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.period_between(time.epoch, time.epoch)
{
  :months 0,
  :years 0,
  :days 0
}

> time.period_between(1970-01-01T00:00:00, 1973-08-02T12:23:13)
{
  :months 7,
  :years 3,
  :days 1
}

> time.period_between(1973-08-02T12:23:13, 1970-01-01T00:00:00)
{
  :months -7,
  :years -3,
  :days -1
}


> time.period_between(nil, nil)
nil
```
~~~

  function period_between: (datetime start_inclusive, datetime end_exclusive) -> dict via {:class "com.twineworks.tweakflow.std.Time$periodBetween"};

doc
~~~
```
(
  datetime start_inclusive,
  datetime end_exclusive
) -> dict
```

Returns a dict with long values on keys `:seconds`, and `:nano_seconds`, representing a duration between two datetimes.

`:seconds` are negative if start datetime is after end datetime.
`:nano_seconds` is never negative and within the range of `0 - 999,999,999`.
A duration of `-1` nanosecond is represented as `-1` `:seconds` and `999,999,999` `:nanoseconds`.

Returns `nil` if any argument is `nil`.

```tweakflow
> time.duration_between(time.epoch, time.epoch)
{
  :nano_seconds 0,
  :seconds 0
}

> time.duration_between(1970-01-01T00:00:00, 1970-01-01T00:00:00.000000001)
{
  :nano_seconds 1,
  :seconds 0
}

> time.duration_between(1970-01-01T00:00:00.000000001, 1970-01-01T00:00:00)
{
  :nano_seconds 999999999,
  :seconds -1
}

> time.duration_between(1970-01-01T00:00:00.00000233, 1973-08-02T12:23:13.928736)
{
  :nano_seconds 928733670,
  :seconds 113142193
}

> time.duration_between(nil, nil)
nil
```
~~~
  function duration_between: (datetime start_inclusive, datetime end_exclusive) -> dict via {:class "com.twineworks.tweakflow.std.Time$durationBetween"};

doc
~~~
```
(
  datetime start,
  long years=0,
  long months=0,
  long days=0
) -> datetime
```

Adds a calendar period to the given start datetime and returns the result.

Supply negative period numbers to effectively subtract a period.

Returns `nil` if any argument is `nil`.

```tweakflow

> time.add_period(time.epoch, 0, 0, 1)
1970-01-02T00:00:00Z@UTC

> time.add_period(time.epoch, 0, 0, -1)
1969-12-31T00:00:00Z@UTC

> time.add_period(2017-05-24T, 1, 2, 3)
2018-07-27T00:00:00Z@UTC

> time.add_period(time.epoch, 2, 23, -98)
1973-08-25T00:00:00Z@UTC

> time.add_period(nil, nil, nil, nil)
nil
```
~~~

  function add_period: (datetime start, long years=0, long months=0, long days=0) -> datetime via {:class "com.twineworks.tweakflow.std.Time$addPeriod"};

doc
~~~
```
(
  datetime start,
  long seconds=0,
  long nano_of_second=0
) -> datetime
```

Adds a time duration to the given start datetime and returns the result.

Supply negative numbers to effectively subtract a duration.

Returns `nil` if any argument is `nil`.

```tweakflow

> time.add_duration(time.epoch, 0, 1)
1970-01-01T00:00:00.000000001Z@UTC

> time.add_duration(time.epoch, 0, -1)
1969-12-31T23:59:59.999999999Z@UTC

> time.add_duration(2017-05-24T00:00:00Z, 3600)
2017-05-24T01:00:00Z@UTC

> time.add_duration(2017-05-24T00:00:00Z, -3600)
2017-05-23T23:00:00Z@UTC

> time.add_duration(nil, nil, nil)
nil
```
~~~

  function add_duration: (datetime start, long seconds=0, long nano_of_second=0) -> datetime via {:class "com.twineworks.tweakflow.std.Time$addDuration"};

doc
~~~
`(datetime x) -> long`

Returns the year component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.year(time.epoch)
1970

> time.year(2017-02-21T)
2017

> time.year(nil)
nil
```
~~~

  function year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$year"};

doc
~~~
`(datetime x) -> long`

Returns the month component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.month(time.epoch)
1

> time.month(2017-02-21T)
2

> time.month(nil)
nil
```
~~~

  function month: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$month"};

doc
~~~
`(datetime x) -> long`

Returns the day of month component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.day_of_month(time.epoch)
1

> time.day_of_month(2017-02-21T)
21

> time.day_of_month(nil)
nil
```
~~~

  function day_of_month: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfMonth"};

doc
~~~
`(datetime x) -> long`

Returns the day of year of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.day_of_year(time.epoch)
1

> time.day_of_year(2017-02-21T)
52

> time.day_of_year(nil)
nil
```
~~~

  function day_of_year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfYear"};

doc
~~~
`(datetime x) -> long`

Returns the day of week of given datetime `x`.

The returned number represents a day of week as per the following mapping.

| Nr  | Day of week   |
|:--- |:------------- |
| 1   | Monday        |
| 2   | Tuesday       |
| 3   | Wednesday     |
| 4   | Thursday      |
| 5   | Friday        |
| 6   | Saturday      |
| 7   | Sunday        |

Returns `nil` if `x` is `nil`.

```tweakflow
> time.day_of_week(time.epoch)
4

> time.day_of_week(2017-05-24T)
3

> time.day_of_week(nil)
nil
```
~~~
  function day_of_week: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfWeek"};

doc
~~~
`(datetime x) -> long`

Returns the hour of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.hour(time.epoch)
0

> time.hour(2017-02-21T23:00:00)
23

> time.hour(nil)
nil
```
~~~
  function hour: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$hour"};

doc
~~~
`(datetime x) -> long`

Returns the minute of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.minute(time.epoch)
0

> time.minute(2017-02-21T00:59:00)
59

> time.minute(nil)
nil
```
~~~

  function minute: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$minute"};

doc
~~~
`(datetime x) -> long`

Returns the second of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.second(time.epoch)
0

> time.second(2017-02-21T00:00:12)
12

> time.second(nil)
nil
```
~~~

  function second: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$second"};

doc
~~~
`(datetime x) -> long`

Returns the nanoseconds of given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.nano_of_second(time.epoch)
0

> time.nano_of_second(2017-02-21T00:00:00.123456789)
123456789

> time.nano_of_second(2017-02-21T00:00:00.01)
10000000

> time.nano_of_second(nil)
nil
```
~~~

  function nano_of_second: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$nanoOfSecond"};

doc
~~~
`(datetime x) -> long`

Returns the week of year for given datetime `x`. The ISO-8601 definition of weeks is used, where a week starts on Monday and the first week of a year has a minimum of 4 days.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.week_of_year(time.epoch)
1

> time.week_of_year(2010-01-01T)
53

> time.week_of_year(2011-01-01T)
52

> time.week_of_year(2012-01-01T)
52

> time.week_of_year(2013-01-01T)
1

> time.week_of_year(2017-02-21T)
8

> time.week_of_year(nil)
nil
```
~~~

  function week_of_year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$weekOfYear"};

doc
~~~
`(datetime x) -> long`

Returns the number of seconds in the offset from UTC for given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.offset_seconds(time.epoch)
0

> time.offset_seconds(2010-01-01T00:00:00.00+02:00)
7200

> time.offset_seconds(2011-01-01T00:00:00.00-03:00)
-10800

> time.offset_seconds(nil)
nil
```
~~~
  function offset_seconds: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$offsetSeconds"};

doc
~~~
`(datetime x) -> long`

Returns the time zone for given datetime `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> time.zone(time.epoch)
"UTC"

> time.zone(2010-01-01T00:00:00.00+02:00)
"UTC+02:00"

> time.zone(2010-01-01T00:00:00.00+01:00@Europe/Berlin)
"Europe/Berlin"

> time.zone(nil)
nil
```
~~~
  function zone: (datetime x) -> string via {:class "com.twineworks.tweakflow.std.Time$zone"};

doc
~~~
`(datetime x, long year) -> datetime`

Returns the datetime `x` with the year field set to `year`. If the day-of-month is invalid for the
resulting datetime, it will be changed to the last valid day of the month.

Returns `nil` if any argument is `nil`.

Throws an error if year is out of bounds of -999999999 - 999999999.

```tweakflow
> time.with_year(time.epoch, 2007)
2007-01-01T00:00:00Z@UTC

> time.with_year(2016-02-29T, 2019)
2019-02-28T00:00:00Z@UTC

> time.with_year(time.epoch, nil)
nil

> time.with_year(nil, 2017)
nil

> time.with_year(time.epoch, 1000000000)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for Year (valid values -999999999 - 999999999): 1000000000
```
~~~

  function with_year: (datetime x, long year) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withYear"};

doc
~~~
`(datetime x, long month) -> datetime`

Returns the datetime `x` with the month field set to `month`.
If the day-of-month is invalid for the resulting datetime, it will be changed to the last valid day of the month.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given month.

```tweakflow
> time.with_month(time.epoch, 6)
1970-06-01T00:00:00Z@UTC

> time.with_month(2016-03-31T, 2)
2016-02-29T00:00:00Z@UTC

> time.with_month(time.epoch, nil)
nil

> time.with_month(nil, 6)
nil

> time.with_month(time.epoch, 13)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for MonthOfYear (valid values 1 - 12): 13
```
~~~

  function with_month: (datetime x, long month) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withMonth"};


doc
~~~
`(datetime x, long day_of_month) -> datetime`

Returns the datetime `x` with the day_of_month field set to `day_of_month`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `day_of_month`.

```tweakflow
> time.with_day_of_month(time.epoch, 23)
1970-01-23T00:00:00Z@UTC

> time.with_day_of_month(2016-02-01T, 29)
2016-02-29T00:00:00Z@UTC

> time.with_day_of_month(2010-02-01T, 29)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid date 'February 29' as '2010' is not a leap year
  ...

> time.with_day_of_month(time.epoch, nil)
nil

> time.with_day_of_month(nil, 23)
nil

> time.with_day_of_month(time.epoch, 33)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for DayOfMonth (valid values 1 - 28/31): 33
```
~~~

  function with_day_of_month: (datetime x, long day_of_month) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withDayOfMonth"};

doc
~~~
`(datetime x, long hour) -> datetime`

Returns the datetime `x` with the hour field set to `hour`.
If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `hour`.

```tweakflow
> time.with_hour(time.epoch, 4)
1970-01-01T04:00:00Z@UTC

# 2:30 is in a 1h DST gap, it technically did not exist
> time.with_hour(2019-03-31T01:30:00+01:00@Europe/Berlin, 2)
2019-03-31T03:30:00+02:00@Europe/Berlin

> time.with_hour(time.epoch, nil)
nil

> time.with_hour(nil, 4)
nil

> time.with_hour(time.epoch, 25)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for HourOfDay (valid values 0 - 23): 25
```
~~~

  function with_hour: (datetime x, long hour) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withHour"};

doc
~~~
`(datetime x, long minute) -> datetime`

Returns the datetime `x` with the minute field set to `minute`.
If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `minute`.

```tweakflow
> time.with_minute(time.epoch, 42)
1970-01-01T00:42:00Z@UTC

> time.with_minute(time.epoch, nil)
nil

> time.with_minute(nil, 42)
nil

> time.with_minute(time.epoch, 78)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for MinuteOfHour (valid values 0 - 59): 78
```
~~~

  function with_minute: (datetime x, long hour) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withMinute"};

doc
~~~
`(datetime x, long second) -> datetime`

Returns the datetime `x` with the second field set to `second`.
If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `second`.

```tweakflow
> time.with_second(time.epoch, 42)
1970-01-01T00:00:42Z@UTC

> time.with_second(time.epoch, nil)
nil

> time.with_second(nil, 42)
nil

> time.with_second(time.epoch, 78)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for SecondOfMinute (valid values 0 - 59): 78
```
~~~

  function with_second: (datetime x, long second) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withSecond"};

doc
~~~
`(datetime x, long nano_of_second) -> datetime`

Returns the datetime `x` with the nano_of_second field set to `nano_of_second`.
If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `nano_of_second`.

```tweakflow
> time.with_nano_of_second(time.epoch, 123456789)
1970-01-01T00:00:00.123456789Z@UTC

> time.with_nano_of_second(time.epoch, nil)
nil

> time.with_nano_of_second(nil, 123456789)
nil

> time.with_nano_of_second(time.epoch, 1000000000)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for NanoOfSecond (valid values 0 - 999999999): 1000000000
```
~~~

  function with_nano_of_second: (datetime x, long nano_of_second) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withNanoOfSecond"};

doc
~~~
`(datetime x, string tz) -> datetime`

Returns the datetime `x` with the time zone field set to `tz`.
If resulting datetime is in a DST gap, the datetime is adjusted forward by the length of the gap.
If resulting datetime is in a DST overlap, the earlier valid offset is used.

Returns `nil` if any argument is `nil`.
Throws an error if no datetime can be constructed with given `tz`.

```tweakflow
> time.with_zone(time.epoch, "Europe/Berlin")
1970-01-01T00:00:00+01:00@Europe/Berlin

> time.with_zone(time.epoch, "GMT+08:00")
1970-01-01T00:00:00+08:00@`GMT+08:00`

> time.with_zone(time.epoch, 'America/New_York')
  1970-01-01T00:00:00-05:00@America/New_York

> time.with_zone(time.epoch, nil)
nil

> time.with_zone(nil, "Europe/Berlin")
nil

> time.with_zone(time.epoch, "---")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: unknown time zone id: ---
```
~~~

  function with_zone: (datetime x, string tz) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withTz"};


doc
~~~
`(datetime x, string tz) -> datetime`

Returns a datetime representing the same instant as `x` in time zone `tz`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `tz`.

```tweakflow
> time.same_instant_at_zone(time.epoch, "Europe/Berlin")
1970-01-01T01:00:00+01:00@Europe/Berlin

> time.same_instant_at_zone(time.epoch, "GMT+08:00")
1970-01-01T08:00:00+08:00@`GMT+08:00`

# the instant in time is the same
> time.compare(1970-01-01T01:00:00+01:00@Europe/Berlin, 1970-01-01T08:00:00+08:00@`GMT+08:00`)
0

> time.same_instant_at_zone(time.epoch, nil)
nil

> time.same_instant_at_zone(nil, "Europe/Berlin")
nil

> time.same_instant_at_zone(time.epoch, "---")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: unknown time zone id: ---
```
~~~

  function same_instant_at_zone: (datetime x, string tz) -> datetime via {:class "com.twineworks.tweakflow.std.Time$sameInstantAtZone"};

doc
~~~
`(datetime x) -> long`

Returns a long representing the number of seconds since epoch for the given `x`.

Returns `nil` if `x` is `nil`.

```tweakflow

> time.unix_timestamp(time.epoch)
0

> time.unix_timestamp(2017-06-08T14:59:55Z@UTC)
1496933995

> time.unix_timestamp(1922-07-26T09:00:05Z@UTC)
-1496933995
```
~~~

  function unix_timestamp: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$unixTimestamp"};

doc
~~~
`(datetime x) -> long`

Returns a long representing the number of milliseconds since epoch for the given `x`.

Returns `nil` if `x` is `nil`.

```tweakflow

> time.unix_timestamp_ms(time.epoch)
0

> time.unix_timestamp_ms(2017-06-08T14:59:55.123Z@UTC)
1496933995123

> time.unix_timestamp_ms(1922-07-26T09:00:04.877Z@UTC)
-1496933995123
```
~~~

  function unix_timestamp_ms: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$unixTimestampMs"};

doc
~~~
`(long s) -> datetime`

Returns a datetime representing epoch + `s` seconds in time zone `UTC`.

Returns `nil` if `s` is `nil`.

```tweakflow

> time.of_unix_timestamp(0)
1970-01-01T00:00:00Z@UTC

> time.of_unix_timestamp(1496933995)
2017-06-08T14:59:55Z@UTC

> time.of_unix_timestamp(-1496933995)
1922-07-26T09:00:05Z@UTC
```
~~~

  function of_unix_timestamp: (long s) ->
    (add_duration(epoch, s));


doc
~~~
`(long ms) -> datetime`

Returns a datetime representing epoch + `ms` milliseconds in time zone `UTC`.

Returns `nil` if `ms` is `nil`.

```tweakflow

> time.of_unix_timestamp_ms(0)
1970-01-01T00:00:00Z@UTC

> time.of_unix_timestamp_ms(1496933995763)
2017-06-08T14:59:55.763Z@UTC

> time.of_unix_timestamp_ms(-1496933995763)
1922-07-26T09:00:04.237Z@UTC
```
~~~

  function of_unix_timestamp_ms: (long ms) ->
    (add_duration(epoch, ms // 1000, (ms % 1000) * 1000000));

doc
~~~
`(datetime a, datetime b) -> long`

Compares two datetimes `a` and `b`.

Returns -1 if `a` precedes `b`.\
Returns 1 if `b` precedes `a`.\
Returns 0 if `a` and `b` describe the same instant in time.\
Assumes `nil` to precede any non-nil datetime.

```tweakflow
> time.compare(time.epoch, time.epoch)
0

> time.compare(time.epoch, 2017-05-24T)
-1

> time.compare(2017-05-24T, time.epoch)
1

> time.compare(2017-05-24T00:00:00, 2017-05-24T02:00:00+02:00)
0

> data.sort([2017-05-24T00:00:00, 2017-05-25T00:00:00, nil, 2017-05-24T00:01:00, 2017-05-24T02:00:00], time.compare)
[nil, 2017-05-24T00:00:00Z@UTC, 2017-05-24T00:01:00Z@UTC, 2017-05-24T02:00:00Z@UTC, 2017-05-25T00:00:00Z@UTC]
```
~~~

  function compare: (datetime a, datetime b) -> long
    if a == nil
      if b == nil then 0 else -1
    if b == nil then 1
    let {
      d: duration_between(a, b);
    }
    if d[:seconds] < 0 then 1
    if (d[:seconds] > 0) || ((d[:seconds] == 0) && (d[:nano_seconds] > 0)) then -1
    else 0;


doc
~~~
```
(
  string pattern="uuuu-MM-dd'T'HH:mm:ssZZZZZ'@`'VV'`'",
  string lang="en-US"
) -> function
```

Returns a function `f` that accepts a single datetime parameter `x`, and returns a string representation of `x` using the
supplied [pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) and language tag.

`f` returns `nil` if `nil` is passed as an argument.

Throws an error if any argument is `nil`.\
Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) for the DateTimeFormatter of the Java language.

```tweakflow
> f: time.formatter("d MMM uuuu")
function

> f(time.epoch)
"1 Jan 1970"

> f: time.formatter("d MMM uuuu", "fr")
function

> f(time.epoch)
"1 janv. 1970"

> f: time.formatter("cccc, d MMMM uuuu")
function

> f(time.epoch)
"Thursday, 1 January 1970"

> f: time.formatter("cccc, d MMMM uuuu", "de")
function

> f(time.epoch)
"Donnerstag, 1 Januar 1970"
```
~~~

  function formatter: (string pattern="uuuu-MM-dd'T'HH:mm:ssZZZZZ'@`'VV'`'", string lang="en-US") -> function via {:class "com.twineworks.tweakflow.std.Time$formatter"};


doc
~~~
```
(
  string pattern="uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']",
  string lang="en-US",
  string default_tz="UTC",
  boolean lenient=false
) -> function
```
Returns a function `f` that accepts a single string paramter `x` and returns a parsed datetime value as specified by the supplied
[pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) and language tag.

`f` returns `nil` when `nil` is passed as an argument.

If `lenient` is false, `f` throws an error on strings that parse to invalid dates.\
If `lenient` is true, the parser will attempt to correct invalid datetime values. A date of January 32nd will parse as February 1st, for example.

The default time zone is used unless the pattern parses both a time component and a time zone in which case the parsed values are used.

Throws an error if any argument is `nil`.\
Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) for the DateTimeFormatter of the Java language.

```tweakflow
> f: time.parser("uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']")
function

> f(nil)
nil

> f("2017-04-23T21:43:11")
2017-04-23T21:43:11Z@UTC

> f("2017-04-23T21:43:11@`Europe/Berlin`")
2017-04-23T21:43:11+02:00@Europe/Berlin

# patterns specifies mandatory @ sign and backticks before timezone
> f("2017-04-23T21:43:11 Europe/Berlin")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Text '2017-04-23T21:43:11 Europe/Berlin' could not be parsed, unparsed text found at index 19

# strict parser
> f: time.parser('uuuu-MM-dd')
function

# there is no march 32nd
> f("2015-03-32")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Text '2015-03-32' could not be parsed: Invalid value for DayOfMonth (valid values 1 - 28/31): 32

# lenient parser
> f: time.parser('uuuu-MM-dd', lenient: true)
function

# lenient parser interprets march 32nd as april 1st
> f("2015-03-32")
2015-04-01T00:00:00Z@UTC

# parses local times setting the default time zone
> f: time.parser('uuuu-MM-dd[ HH:mm:ss]', default_tz: 'America/Chicago')
function

> f("2017-06-22 12:34:11")
2017-06-22T12:34:11-05:00@America/Chicago

> f("2017-06-22")
2017-06-22T00:00:00-05:00@America/Chicago

```
~~~

  function parser: (string pattern="uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']", string lang="en-US", string default_tz="UTC", boolean lenient=false) -> function via {:class "com.twineworks.tweakflow.std.Time$parser"};

doc
~~~
`() -> list`

Returns a list of all known time zone ids.

```tweakflow
> time.zones()
["Africa/Abidjan", "Africa/Accra", "Africa/Addis_Ababa", ...]

# get all Europe/x time zones
> data.filter(time.zones(), (z) -> strings.split(z, "/")[0] == "Europe")
["Europe/Amsterdam", "Europe/Andorra", "Europe/Astrakhan", "Europe/Athens", ...]

# get all US/x time zones
> data.filter(time.zones(), (z) -> strings.split(z, "/")[0] == "US")
["US/Alaska", "US/Aleutian", "US/Arizona", "US/Central", "US/East-Indiana", "US/Eastern", ...]
```
~~~

  function zones: () -> list via {:class "com.twineworks.tweakflow.std.Time$zones"};
}

doc
~~~
The math library contains basic mathematical functions.
~~~

export library math {

doc
~~~
`(x) -> `

If `x` is a `double`, returns the absolute value of `x` as a double. \
If `x` is `NaN`, the result is `NaN`. \
If `x` is `-Infinity` the result is `Infinity`.

If `x` is a `long`, returns the absolute value of `x` as a long. \
If `x` is a long, and is equal to `math.min_long`, the absolute value cannot be represented as a long, and an error is thrown.

If `x` is a `decimal`, returns the absolute value of `x` as a decimal.

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not numeric and not `nil`.

```tweakflow
> math.abs(100)
100

> math.abs(-100)
100

> math.abs(-2.0)
2.0

> math.abs(-1.000d)
1.000d

> math.abs(-Infinity)
Infinity

> math.abs(NaN)
NaN

> math.abs(math.min_long)
ERROR:
  code: NUMBER_OUT_OF_BOUNDS
  message: cannot represent magnitude as long

> math.abs(math.min_long as double)
9.223372036854776E18

> math.abs(nil)
nil

> math.abs("hello")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot determine magnitude of type string
```
~~~

  function abs: (x) ->                                      via {:class "com.twineworks.tweakflow.std.Math$abs"};

doc
~~~
`(any seed) -> double`

Returns a pseudo-random double between `0.0` inclusive and `1.0` exclusive, based on given `seed`.
This function is pure and deterministically returns the same number for the same `seed`.

To generate a sequence of pseudo-random numbers, you can use a previously generated number as the next `seed`.

`nil` is a valid `seed` value.

```tweakflow
> dice_roll: (any seed) -> (math.rand(seed) *6 +1) as long
function

> dice_roll("hello")
3

> dice_roll("hi")
6

> \e
  dice_rolls: (long count, any seed) ->
    fun.iterate(
      1, count,                   # loop 1-count times
      {:seed [seed], :nums []},   # initial state
      (state, i) ->
        # accumulate seed list and number list and return new state
        let {
          r: math.rand(state[:seed]);
          n: (r*6+1) as long;
        }
        {
          :seed [...state[:seed], r*i],
          :nums [...state[:nums], n]
        }
    )[:nums]
\e
function

> dice_rolls(3, "foo")
[5, 1, 5]

> dice_rolls(10, "foo")
[5, 1, 5, 3, 3, 5, 6, 6, 5, 3]

> dice_rolls(3, "bar")
[6, 1, 1]

> dice_rolls(10, "bar")
[6, 1, 1, 3, 6, 4, 3, 5, 6, 5]

> math.rand(nil)
0.730967787376657

```
~~~
  function rand: (any seed) -> double                           via {:class "com.twineworks.tweakflow.std.Math$rand"};

doc
~~~
`(any x) -> any`

Increments `x` by one.

If `x` is a double, returns `x+1.0` \
If `x` is a long, returns `x+1` \
If `x` is a decimal, returns `x+1d` \

Does not guard against overflow of long values.

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not numeric.

```tweakflow
> math.inc(2.0)
3.0

> math.inc(-2.0)
-1.0

> math.inc(1)
2

> math.inc(4.1234d)
5.1234d

> math.inc(math.max_long)
-9223372036854775808 # overflow to math.min_long

> math.inc(nil)
nil

> math.inc([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot increment type list
```
~~~

  function inc: (any x) -> any                              via {:class "com.twineworks.tweakflow.std.Math$inc"};

doc
~~~
`(any x) -> any`

Decrements `x` by one.

If `x` is a double, returns `x-1.0` \
If `x` is a long, returns `x-1` \
If `x` is a decimal, returns `x-1d` \

Does not guard against underflow of long values.

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not numeric.

```tweakflow
> math.dec(2.0)
1.0

> math.dec(-2.0)
-3.0

> math.dec(-2.1d)
-3.1d

> math.dec(1)
0

> math.dec(math.min_long)
9223372036854775807 # overflow to math.max_long

> math.dec(nil)
nil

> math.dec([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot decrement type list
```
~~~

  function dec: (x) -> via {:class "com.twineworks.tweakflow.std.Math$dec"};

doc
~~~
`(a, b) -> long`

Compares numbers a and b according to their numeric order.

Returns -1 if a < b.\
Returns 1 if a > b.\
Returns 0 if a == b.

The order reflected by this function sorts these values in order: `nil`, `NaN`, `-Infinity`, finite numeric values, and `Infinity`.

Throws an error if `a` or `b` are not `nil`, and not numeric.

```tweakflow
> math.compare(1, 1)
0

> math.compare(nil, 7)
-1

> math.compare(7.0, 2)
1

> data.sort([4, 3, 2.5d, 1, 0.2, nil, NaN, -Infinity, Infinity], math.compare)
[nil, NaN, -Infinity, 0.2, 1, 2.5d, 3, 4, Infinity]
```
~~~
  function compare: (a, b) -> long via {:class "com.twineworks.tweakflow.std.Math$compare"};

doc
~~~
`(list xs) -> any`

Given a list of numeric `xs`, returns the smallest `x`.\

Returns `nil` if `xs` is `nil`, `xs` is empty, or any `x` in `xs` is `nil` or `NaN`.

Throws an error if any `x` is anything other than a numeric value or `nil`.

```tweakflow
> math.min([1,2,3])
1

> math.min([1.0, 2d, -3.0])
-3.0

> math.min([1, nil])
nil

> math.min(nil)
nil

> math.min(["foo"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot compare type string
```
~~~

  function min: (list xs) -> any                            via {:class "com.twineworks.tweakflow.std.Math$min"};

doc
~~~
`(list xs) -> any`

Given a list of numeric `xs`, returns the largest `x`.

Returns `nil` if `xs` is `nil`, `xs` is empty, or any `x` in `xs` is `nil` or `NaN`.

Throws an error if any `x` is anything other than a numeric value or `nil`.

```tweakflow
> math.max([1,2,3])
3

> math.max([1.0, 2.0, -3d])
2.0

> math.max([1, nil])
nil

> math.max(nil)
nil

> math.max(["foo"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot compare type string
```
~~~

  function max: (list xs) -> any                            via {:class "com.twineworks.tweakflow.std.Math$max"};

doc
~~~
`(double x) -> long`

Given a double `x`, returns `x` rounded to the closest long value. Ties are rounded up.

Returns `0` if `x` is `NaN`.\
Returns `math.min_long` if `x` is `-Infinity` or less than or equal to `math.min_long`.\
Returns `math.max_long` if `x` is `Infinity` or greater than or equal to `math.max_long`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.round(2.3)
2

> math.round(2.5)
3

> math.round(-2.3)
-2

> math.round(-2.5)
-2

> math.round(nil)
nil
```
~~~

  function round: (double x) -> long                      via {:class "com.twineworks.tweakflow.std.Math$round"};

doc
~~~
`(double x) -> double`

Given a double `x`, returns the smallest double value greater than or equal to `x` that is a mathematical integer.

Returns `-Infinity` if `x` is `-Infinity`.\
Returns `Infinity` if `x` is `Infinity`.\
Returns `NaN` if `x` is `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.ceil(2.3)
3.0

> math.ceil(-2.3)
-2.0

> math.ceil(nil)
nil
```
~~~

  function ceil:  (double x) -> double                      via {:class "com.twineworks.tweakflow.std.Math$ceil"};

doc
~~~
`(double x) -> double`

Given a double `x`, returns the largest double value smaller than or equal to `x` that is a mathematical integer.

Returns `-Infinity` if `x` is `-Infinity`.\
Returns `Infinity` if `x` is `Infinity`.\
Returns `NaN` if `x` is `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.floor(2.3)
2.0

> math.floor(-2.3)
-3.0

> math.floor(nil)
nil
```
~~~

  function floor: (double x) -> double                      via {:class "com.twineworks.tweakflow.std.Math$floor"};

doc
~~~
`(any x) -> boolean`

Given a value `x`, returns `true` if `x` is NaN, returns `false` otherwise.

```tweakflow
> math.NaN?(2.3)
false

> math.NaN?(NaN)
true

> math.NaN?(nil)
false
```
~~~

  function NaN?:  (any x) -> boolean                     via {:class "com.twineworks.tweakflow.std.Math$nan"};

doc
~~~
`(any x) -> boolean`

Returns `false` if `x` is `nil`, `NaN`, `-Infinity`, or `Infinity`.

Returns `true` if `x` is a finite numeric value.

Throws an error if `x` is not of type `long`, `double`, or `decimal`.

```tweakflow
> math.finite?(2.3)
true

> math.finite?(NaN)
false

> math.finite?(Infinity)
false
```
~~~

  function finite?:  (any x) -> boolean                     via {:class "com.twineworks.tweakflow.std.Math$finite"};

doc
~~~
`(double x) -> double`

Given a double `x`, returns the square root of `x`.

Returns `NaN` if `x` is negative.\
Returns `Infinity` if `x` is `Infinity`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.sqrt(9.0)
3.0

> math.sqrt(-9.0)
NaN

> math.sqrt(nil)
nil
```
~~~

  function sqrt:  (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$sqrt"};

doc
~~~
`(double x) -> double`

Returns the trigonometric sine of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.sin(0)
0.0

> math.sin(math.pi/2)
1.0

> math.sin(1)
0.8414709848078965
```
~~~

  function sin: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$sin"};

doc
~~~
`(double x) -> double`

Returns the trigonometric cosine of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.cos(0)
1.0

> math.cos(math.pi)
-1.0

> math.cos(math.pi*2)
1.0

> math.cos(1)
0.5403023058681398
```
~~~

  function cos: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$cos"};


doc
~~~
`(double x) -> double`

Returns the trigonometric tangent of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.tan(0)
0.0

> math.tan(1)
1.5574077246549023

> math.tan(math.pi/4)
0.9999999999999999
```
~~~

  function tan: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$tan"};

doc
~~~
`(double x) -> double`

Returns the arc sine of `x` in the range of -pi/2 throuh pi/2. \
Returns `NaN` if `x` is `NaN` or its absolute value is greater than 1.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.asin(0)
0.0

> math.asin(0.5)
0.5235987755982989

> math.asin(1)
1.5707963267948966
```
~~~

  function asin: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$asin"};

doc
~~~
`(double x) -> double`

Returns the arc cosine of `x` in the range of 0.0 throuh pi. \
Returns `NaN` if `x` is `NaN` or its absolute value is greater than 1.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.acos(0)
1.5707963267948966

> math.acos(0.5)
1.0471975511965979

> math.acos(1)
0.0

> math.acos(-1)
3.141592653589793
```
~~~

  function acos: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$acos"};

doc
~~~
`(double x) -> double`

Returns the arc tangent of `x` in the range of -pi/2 through pi/2. \
Returns `NaN` if `x` is `NaN`.\
Returns `nil` if `x` is `nil`.

```tweakflow
> math.atan(0)
0.0

> math.atan(-1)
-0.7853981633974483

> math.atan(1)
0.7853981633974483
```
~~~

  function atan: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$atan"};


doc
~~~
`(double x) -> double`

Returns the natural logarithm (base e) of `x`.

Returns `NaN` if `x` is `NaN` or less than `0`.\
Returns `-Infinity` if `x` is `0` \
Returns `Infinity` if `x` is `Infinity` \
Returns `nil` if `x` is `nil`.

```tweakflow
> math.log(0)
-Infinity

> math.log(1)
0.0

> math.log(math.e**2)
2.0
```
~~~

  function log: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$log"};


doc
~~~
`(double x) -> double`

Returns the base 10 logarithm of `x`.

Returns `NaN` if `x` is `NaN` or less than `0`.\
Returns `-Infinity` if `x` is `0` \
Returns `Infinity` if `x` is `Infinity` \
Returns `nil` if `x` is `nil`.

```tweakflow
> math.log10(0)
-Infinity

> math.log10(1)
0.0

> math.log10(100)
2.0
```
~~~

  function log10: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$log10"};


doc
~~~
`(long x) -> long`

Given a long `x`, returns the number of bits set to 1 in the binary two's complement representation of `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> math.bit_count(1) # 1
1

> math.bit_count(2) # 10
1

> math.bit_count(3) # 11
2

> math.bit_count(nil)
nil
```
~~~
  function bit_count: (long x) -> long                      via {:class "com.twineworks.tweakflow.std.Math$bitCount"};


doc
~~~
```
(
  string pattern='0.##',
  dict decimal_symbols=nil,
  string rounding_mode="half_even",
  boolean always_show_decimal_separator=false
) -> function
```

Returns a function `f` that accepts a single numeric parameter `x`, and returns a string representation of `x` using the
supplied [pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html),
[decimal_symbols](#locale-decimal_symbols),
[rounding mode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html), and
[always_show_decimal_separator](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html#setDecimalSeparatorAlwaysShown-boolean-) flag.

If `decimal_symbols` is `nil` (the default), 'en-US' decimal symbols are used.\
`rounding_mode` is provided as a case-insensitive name of one of the [rounding modes](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html) defined by the
Java language.

`f` returns `nil` if passed `nil` as an argument.
`f` throws an error if `x` is not a numeric value.

Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html) for the DecimalFormat of the Java language.\
Throws an error if `decimal_symbols` is invalid.\
Throws an error if `rounding_mode` is `nil`.\
Throws an error if `always_show_decimal_separator` is `nil`.

```tweakflow
> f: math.formatter()
function

> f(100)
"100"

> f(100.233)
"100.23"

> f: math.formatter('0.00', locale.decimal_symbols('fr'), 'half_even')
function

> f(100)
"100,00"

> f(100.233)
"100,23"

> f(20332.039)
"20332,04"

> f: math.formatter('#,##0.##', locale.decimal_symbols('hi-IN'))
function

std.tf> f(648722)
"६४८,७२२"
```
~~~
  function formatter: (string pattern='0.##', dict decimal_symbols=nil, string rounding_mode="half_even", boolean always_show_decimal_separator=false) -> function via {:class "com.twineworks.tweakflow.std.Math$formatter"};


doc
~~~
```
(
  string pattern='0.##',
  dict decimal_symbols=nil,
  boolean lenient=false,
  boolean parse_decimal=false
) -> function
```

Returns a function `f` that accepts a single string parameter `x` and returns a parsed numeric value as specified by the supplied
[pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html) and [decimal_symbols](#locale-decimal_symbols).

Uses `en-US` decimal symbols if `decimal_symbols` is `nil`.

If `lenient` is `false` then `x` must parse as a number in its entirety. Partial matches throw an error.\
If `lenient` is `true` then partial matches of `x` return the number that results from parsing the partial match.

If `parse_decimal` is false, `f` returns a `long` or `double` value.\
If `parse_decimal` is true, `f` returns a `decimal` value.

`f` returns `nil` if `x` is `nil`.

Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html) for the DecimalFormat of the Java language.\
Throws an error if `decimal_symbols` is invalid.\
Throws an error if `lenient` is `nil`. \
Throws an error if `parse_decimal` is `nil`.

```tweakflow
> f: math.parser()

> f("203.23")
203.23

> f("203.23kg")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Partial match not allowed. Parsing ended at index: 6

# lenient parser
> f: math.parser('0.##', lenient: true)
function

> f("203.23kg")
203.23

# localized parser
> f: math.parser('#,##0.##', locale.decimal_symbols('hi-IN'))
function

> f("१०३")
103

# decimal parser
> f: math.parser('0.##', parse_decimal: true)
function

> f("203.23")
203.23d
```
~~~

  function parser: (string pattern='0.##', dict decimal_symbols=nil, boolean lenient=false, boolean parse_decimal=false) -> function via {:class "com.twineworks.tweakflow.std.Math$parser"};

doc
~~~
The double value that is closer than any other to `e`, the base of the natural logarithms.
~~~
  double e:   2.7182818284590452354;

doc
~~~
The double value that is closer than any other to `pi`, the ratio of the circumference of a circle to its diameter.
~~~

  double pi:  3.14159265358979323846;

doc
~~~
The smallest representable long value: `-9223372036854775808`.
~~~

  long min_long: 0x8000000000000000;

doc
~~~
The largest representable long value: `9223372036854775807`.
~~~

  long max_long: 0x7FFFFFFFFFFFFFFF;
}


doc
~~~
The decimals library contains utility functions for working with decimal numbers.
~~~

export library decimals {

doc
~~~
`(decimal x) -> decimal`

Returns the unit in the last place, of given decimal `x`.

An ulp of a decimal is the positive distance between this value and the decimal next larger in magnitude with the same scale.

Returns `nil` if `x` is `nil`.

```tweakflow
> decimals.ulp(0.5d)
0.1d

> decimals.ulp(1.000d)
0.001d

> decimals.ulp(-1000d)
1d

> decimals.ulp(nil)
nil
```
~~~

  function ulp: (decimal x) -> decimal  via {:class "com.twineworks.tweakflow.std.Decimals$ulp"};

doc
~~~
`(decimal x) -> long`

Returns the scale of given decimal `x`.

A decimal number is internally represented as a mathematical integer x 10^(-scale).

A positive scale therefore indicates the number of fractional digits.

A negative scale represents a decimal that is stored in terms of 10, 100, 1000, etc.
times some integer value, effectively dropping precision of trailing digits.

Returns `nil` if `x` is `nil`.

```tweakflow
# no fractional digits
> decimals.scale(0d)
0

# two fractional digits
> decimals.scale(0.00d)
2

# two fractional digits
> decimals.scale(0.75d)
2

# value 100 with -2 scale
> decimals.scale(1e+2d)
-2
```
~~~
  function scale: (decimal x) -> long via {:class "com.twineworks.tweakflow.std.Decimals$scale"};

doc
~~~
```tweakflow
(
  decimal x,
  long scale,
  string rounding_mode='half_up'
) -> decimal
```

Returns a decimal of the same value as `x` with given `scale`.

The given [rounding mode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html) is used when digits must be dropped to adapt the given scale.

Returns `nil` if `x` is `nil`.

Throws an error if `scale` or `rounding_mode` are `nil`.

```tweakflow
> decimals.with_scale(0d, 3)
0.000d

> decimals.with_scale(5d, 2)
5.00d

> decimals.with_scale(1.29d, 1)
1.3d

> decimals.with_scale(1.29d, 1, 'down')
1.2d

# set negative scale -2, effectively storing the number at multiples of 100
> decimals.with_scale(1299d, -2)
1.3E+3d

> decimals.with_scale(1299d, -2, 'down')
1.2E+3d
```
~~~
  function with_scale: (decimal x, long scale, string rounding_mode='half_up') -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$with_scale"};

doc
~~~
```tweakflow
(
  decimal x,
  long digits,
  string rounding_mode='half_up'
) -> decimal
```

Returns a decimal of `x` rounded to the given number of non-zero digits using the given [rounding mode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html).

This function is useful to get a number with a relevant number of significant digits when dealing with numbers which contain many digits.

If you wish to round to a fixed number of decimal places or digits, use [with_scale](#decimals-with_scale).

Returns `nil` if `x` is `nil`.\
If `digits` is `0`, `x` is returned.\
Throws an error if `digits` or `rounding_mode` are `nil`.\
Throws an error if `digits` is negative.

```tweakflow
> decimals.round(0.0001234, 2)
0.00012d

> decimals.round(0.01234d, 2)
0.012d

> decimals.round(1.01234d, 2)
1.0d

> decimals.round(1999d, 2)
2.0E+3d

> decimals.round(1999, 3, 'down')
1.99E+3d
```
~~~

  function round: (decimal x, long digits, string rounding_mode='half_up') -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$round"};

doc
~~~
`(decimal x) -> string`

Returns a plain string representation of the decimal, not using exponent notation.

Returns `nil` if `x` is `nil`.

```tweakflow
> decimals.plain(1d)
"1"

> decimals.plain(1.00d)
"1.00"

> decimals.plain(1e+6d)
"1000000"

> decimals.plain(1e-5d)
"0.00001"
```
~~~
  function plain: (decimal x) -> string via {:class "com.twineworks.tweakflow.std.Decimals$plain"};

doc
~~~
`(decimal x) -> decimal`

A decimal number is internally represented as a mathematical integer x 10^(-scale).

Returns a decimal numerically equal to `x` in which scale and the integer part has
been adjusted to not store any trailing zeros in the integer part.

Returns `nil` if `x` is `nil`.

```tweakflow
> decimals.strip_trailing_zeros(1.00d)
1d

> decimals.strip_trailing_zeros(1.10d)
1.1d

> decimals.strip_trailing_zeros(100d)
1E+2d

> decimals.strip_trailing_zeros(110d)
1.1E+2d
```
~~~
  function strip_trailing_zeros: (decimal x) -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$strip_trailing_zeros"};

doc
~~~
```tweakflow
(
  decimal x,
  decimal y,
  long scale,
  string rounding_mode='half_up'
) -> decimal
```

Returns the result of `x` divided by `y` with given `scale` and [rounding mode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html).

If `scale` is `nil`, the scale of `x` is used.

Returns `nil` if `x` or `y` are `nil`.\
Throws an error if `rounding_mode` is `nil`.
Throws an error if `y` is zero.

```tweakflow
> decimals.divide(1d, 3d, 2)
0.33d

> decimals.divide(1d, 3d, 10)
0.3333333333d

> decimals.divide(1d, 3d, 2, 'up')
0.34d

# scale=nil means result uses scale of x
> decimals.divide(1.00000d, 3d)
0.33333d

# negative scale division
> decimals.divide(10_000d, 3d, -2)
3.3E+3d

# negative scale division rounded up
> decimals.divide(10_000d, 3d, -2, 'up')
3.4E+3d
```
~~~
  function divide: (decimal x, decimal y, long scale, string rounding_mode='half_up') -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$divide"};

doc
~~~
`(decimal x, decimal y) -> decimal`

Returns the integer part of `x` divided by `y`. Any fractional digits are not included in the result.

The preferred scale of the result is `scale(x) - scale(y)`, but the scale will be expanded to accommodate
additional digits, if necessary.

Returns `nil` if `x` or `y` are `nil`.\
Throws an error if `y` is zero.

```tweakflow
> decimals.divide_integral(1d, 3d)
0d

> decimals.divide_integral(100d, 3d)
33d

> decimals.divide_integral(100d, -3d)
-33d

# scale of result is scale(x) - scale(y)
> decimals.divide_integral(10.54321d, 0.5d)
21.0000d

# scale of result is scale(x) - scale(y)
> decimals.divide_integral(10.54321d, 1E+1d)
1.000000d
```
~~~
  function divide_integral: (decimal x, decimal y) -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$divide_integral"};


doc
~~~
`(double x) -> decimal`

Floating point doubles encode approximations of fractional numbers.

This function returns the exact mathematical value of the given double as a decimal.

Returns `nil` if `x` is `nil`.

```tweakflow
> decimals.from_double_exact(1.0)
1d

> decimals.from_double_exact(2.6)
2.600000000000000088817841970012523233890533447265625d

> decimals.from_double_exact(0.1)
0.1000000000000000055511151231257827021181583404541015625d

> decimals.from_double_exact(nil)
nil
```
~~~
  function from_double_exact: (double x) -> decimal via {:class "com.twineworks.tweakflow.std.Decimals$from_double_exact"};

}

doc
~~~
The function library contains utility functions to call functions using certain patterns or conditions. Functions in this library
provide functionality similar to control-flow features in other languages.
~~~

export library fun {

doc
~~~
`(long n, any x, function f) -> any`

Calls `f` `n` times with one argument. The first call of `f` receives `x` as argument.
Subsequent calls to `f` receive as argument the result of the previous call.

Returns the result of the last call to `f`, or `x` if `n` is `0`.

Returns `nil` if `n` is `nil`.\
Throws an error if `n` is negative or `f` is `nil`.

```tweakflow
> double_up: (x) -> 2*x
function

> fun.times(10, 1, double_up)
1024
```

Example: computing [Fibonacci numbers](https://en.wikipedia.org/wiki/Fibonacci_number)

```tweakflow
# helper: given fibonacci number n and n+1, compute fibonacci number n+1 and n+2
> next_fib_pair: (p) -> if p == [0, 0] then [0, 1] else [p[1], p[0]+p[1]]
function

# compute nth fibonacci number
> fib: (n) -> fun.times(n, [0, 0], next_fib_pair)[1]
function

> fib(10)
55

> data.map([1,2,3,4,5,6,7,8,9,10], fib)
[1, 1, 2, 3, 5, 8, 13, 21, 34, 55]
```
~~~

  function times: (long n, any x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$times"};

doc
~~~
`(function p, any x, function f) -> any`

Calls `f` repeatedly with one argument. The first call of `f` receives `x` as argument.
Subsequent calls to `f` receive as argument the result of the previous call.

Before each call to `f`, the argument is tested by predicate function `p`.
If `p` returns a value that casts to boolean `true`, execution completes and the tested argument is returned.

Returns the first argument for which `p` returns a value that casts to boolean `true`.

Throws an error if `p` is `nil` or `f` is `nil`.

```tweakflow
# keep doubling up 1 until the result exceeds 60,000
> fun.until((x) -> x>60000, 1, (x) -> x*2)
65536

# Generate natural numbers, until their sum exceeds 100.
# The data structure is a dict containing the list of
# numbers at key :nums and their sum at key :sum.

> \e
  next: (x) ->
    let {
      n: data.size(x[:nums])+1;
    }
    {
      :nums [...x[:nums], n],
      :sum x[:sum]+n
    }
\e
function

> fun.until((x) -> x[:sum] > 100, {:nums [], :sum 0}, next)
{
  :sum 105,
  :nums [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
}

```
~~~
  function until: (function p, any x, function f) ->            via {:class "com.twineworks.tweakflow.std.Fun$until"};

doc
~~~
`(function p, any x, function f) -> any`

Calls `f` repeatedly with one argument. The first call of `f` receives `x` as argument.
Subsequent calls to `f` receive as argument the result of the previous call.

Before each call to `f`, the argument is tested by predicate function `p`.
If `p` returns a value that does not cast to boolean `true`, execution completes and the tested argument is returned.

Returns the first argument for which `p` returns a value that does not cast to boolean `true`.

Throws an error if `p` is `nil` or `f` is `nil`.

```tweakflow
# keep doubling up 1 while the result is smaller than 60,000
> fun.while((x) -> x<60000, 1, (x) -> x*2)
65536

# Generate natural numbers, while their sum is smaller than 100.
# The data structure is a dict containing the list of
# numbers at key :nums and their sum at key :sum.

> \e
  next: (x) ->
    let {
      n: data.size(x[:nums])+1;
    }
    {
      :nums [...x[:nums], n],
      :sum x[:sum]+n
    }
\e
function

> fun.while((x) -> x[:sum] < 100, {:nums [], :sum 0}, next)
{
  :sum 105,
  :nums [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
}

```
~~~

  function while: (function p, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$doWhile"};

doc
~~~
`(long start, long end, any x, function f) -> any`

Calls `f` repeatedly with two arguments. The first call of `f` receives `x` as first argument.
Subsequent calls to `f` receive as first argument the result of the previous call.

The second argument is an index starting at `start` and ending inclusively at `end`.

Returns the result of the last call to `f`.\
Returns `x` if `end` < `start`.

Returns `nil` if `start` is `nil` or `end` is `nil`.

Throws an error if `f` is `nil`.

```tweakflow
# add natural numbers 1 to 10
> fun.iterate(1, 10, 0, (sum, i) -> sum+i)
55
```
~~~

  function iterate: (long start, long end, x, function f) ->      via {:class "com.twineworks.tweakflow.std.Fun$iterate"};

doc
~~~
`(any state, list fs) -> any`

Calls functions from given `fs` in sequence, passing `state` as the argument to the first function,
and the result of the previous call to subsequent calls. Returns result of final call.

Returns `state` if `fs` is empty.

Throws an error if `fs` is nil.

```tweakflow
> \e
  rev_str: (str) -> fun.thread(
    str,
    [
      (x) -> strings.chars(x), # split to chars
      (x) -> data.reverse(x),  # reverse order
      (x) -> strings.join(x)   # and re-join
    ]
  )
\e
function

> rev_str("dlrow olleh")
"hello world"
```
~~~

  function thread: (any state, list fs) ->
    if fs == nil
      throw {:code "NIL_ERROR", :message "fs cannot be nil"}
    else
      (data.reduce(fs, state, (a, f) -> (f(a))));

  # function thread_splat: (state, list fs) ->
  #   data.reduce(fs, state, (a, f) -> f(...a))

  # function thread_until: (list state, function p, list fs) ->
  #   data.reduce_until(fs, state, p, (a, f) -> f(a))

  # function thread_splat_until: (list state, function p, list fs) ->
  #   data.reduce_until(fs, state, p, (a, f) -> f(...a))

doc
~~~
`(list fs) -> function`

Given a list of functions `fs`, returns a composite function that calls all functions in `fs` in order.

`fun.chain([f, g])(x) == g(f(x))`

Returns `core.id` identity function if `fs` is empty.\
Throws an error if `fs` is `nil`.

```tweakflow
> f: fun.chain([(x) -> x+10, (x) -> x*2]) # add 10, then double result
function

> f(1)  # (1+10)*2
22
```
~~~

  function chain: (list fs) -> function
    if fs == nil then throw {:code "NIL_ERROR", :message "fs cannot be nil"}
    if data.size(fs) == 0 then core.id
    (data.reduce(fs, nil, (a, f) -> if a == nil then f else (x) -> (f(a(x)))));

doc
~~~
`(list fs) -> function`

Given a list of functions `fs`, returns a composite function that calls all functions in `fs` in reverse order, consistent
with conventional mathematical notation.

`fun.compose([f, g])(x) == f(g(x))`

Returns `core.id` identity function if `fs` is empty.\
Throws an error if `fs` is `nil`.

```tweakflow
> f: fun.compose([(x) -> x+10, (x) -> x*2]) # double result, then add 10
function

> f(1)  # (1*2)+10
12
```
~~~

  function compose: (list fs) -> function
    if fs == nil then throw {:code "NIL_ERROR", :message "fs cannot be nil"}
    if data.size(fs) == 0 then core.id
    (data.reduce(data.reverse(fs), nil, (a, f) -> if a == nil then f else (x) -> (f(a(x)))));

doc
~~~
`(function f) -> dict`

Returns a `dict` describing the signature of `f`.

The following keys are present:

| key            | value                                      |
|:---            |:-------------                              |
| return_type    | string indicating the declared return type |
| parameters     | list indicating the declared parameters    |

Each parameter in the parameters list is a dict of the form:

| key            | value                                      |
|:---            |:-------------                              |
| index          | long indicating the index of the parameter |
| declared_type  | string indicating the declared type        |
| name           | string indicating the name of the parameter|
| default_value  | the default value of the parameter         |

Returns `nil` if `f` is `nil`.

```tweakflow
> fun.signature(strings.replace)
{
  :return_type "string",
  :parameters [{
    :name "x",
    :index 0,
    :default_value nil,
    :declared_type "string"
  }, {
    :name "search",
    :index 1,
    :default_value nil,
    :declared_type "string"
  }, {
    :name "replace",
    :index 2,
    :default_value nil,
    :declared_type "string"
  }]
}

> fun.signature(fun.signature)
{
  :return_type "dict",
  :parameters [{
    :name "f",
    :index 0,
    :default_value nil,
    :declared_type "function"
  }]
}

```
~~~

  function signature: (function f) -> dict via {:class "com.twineworks.tweakflow.std.Fun$signature"};

}

doc
~~~
The locale library provides information about available localization conventions. Localization conventions
are relevant in the context of number and date formatting as well as string sorting.
~~~

export library locale {

doc
~~~
`(lang='en-US') -> dict`

Returns a dict of language tags (BCP 47) to human readable names of all available languages.
The human readable names are given in the language tag passed as the locale parameter, if available.


Returns `en-US` display names if `lang` is `nil` or unknown.

```tweakflow
# list available languages with french display names
> locale.languages('fr')
{
  :`en-IE` "anglais (Irlande)",
  :`ar-AE` "arabe (Emirats Arabes Unis)",
  :`ar-JO` "arabe (Jordanie)",
  :de "allemand",
  :hi "hindi",
  :no "norvégien",
  :`be-BY` "biélorusse (Biélo-Russie)",
  :`es-PR` "espagnol (Porto Rico)",
  :`zh-HK` "chinois (Hong-Kong)",
  :`en-CA` "anglais (Canada)",
  :`zh-TW` "chinois (Taiwan)",
  ...
}
```
~~~
  function languages: (lang='en-US') -> dict via {:class "com.twineworks.tweakflow.std.Locale$languages"};

doc
~~~
`(lang='en-US') -> dict`

Returns a dict containing the decimal symbols used for number representation in given language.


The dict contains the following keys:

| Key                 | Value                                                  |
|:------------------- |:------------------------------------------------------ |
| decimal_separator   | The character representing a decimal separator.        |
| grouping_separator  | The character representing a grouping separator.       |
| exponent_separator  | The string used to separate the mantissa from the exponent in scientific notation. |
| zero_digit          | The character representing the zero digit. The subsequent 9 character values are treated as digits 1-9. |
| minus_sign          | The character representing the minus sign.             |
| infinity            | The string representing infinity.                      |
| NaN                 | The string representing NaN (Not a Number) values.     |

Returns `en-US` decimal symbols if `lang` is `nil`.

```tweakflow
> locale.decimal_symbols()
{
  :infinity "∞",
  :grouping_separator ",",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "0",
  :decimal_separator ".",
  :NaN "�"
}

> locale.decimal_symbols('fr')
{
  :infinity "∞",
  :grouping_separator " ",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "0",
  :decimal_separator ",",
  :NaN "�"
}

> locale.decimal_symbols('hi-IN')
{
  :infinity "∞",
  :grouping_separator ",",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "०",
  :decimal_separator ".",
  :NaN "�"
}

```
~~~

  function decimal_symbols: (lang='en-US') -> dict via {:class "com.twineworks.tweakflow.std.Locale$decimalSymbols"};

}


doc
~~~
The bin library provides functions that operate on binary data.
~~~

export library bin {

doc
~~~
`(list xs) -> binary`

Expects a list of binary values. Returns a binary that contains all bytes of all binaries in given order.

Returns `nil` if `xs` is `nil`.\
Returns `nil` if any element of `xs` is `nil`.

Throws an error if any element of `xs` is not a binary.

```tweakflow
> bin.concat([0b00, 0b01])
0b0001

> bin.concat([nil, 0b01])
nil
```
~~~
  function concat: (list xs) -> binary via {:class "com.twineworks.tweakflow.std.Bin$concat"};

doc
~~~
`(binary x) -> long`

Returns the number of bytes in `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> bin.size(0b)
0

> bin.size(0b01020304)
4

> bin.size(nil)
nil
```
~~~

  function size: (binary x) -> long via {:class "com.twineworks.tweakflow.std.Bin$size"};

doc
~~~
`(binary x, long i) -> long`

Returns the byte at byte offset `i` in binary `x` as a long between 0 and 255.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i>=size(x)`.

```tweakflow
> bin.byte_at(0b01020304, 3)
4

> bin.byte_at(0bFF, 0)
255

> bin.byte_at(0b, 10)
nil
```
~~~

  function byte_at: (binary x, long i) -> long via {:class "com.twineworks.tweakflow.std.Bin$byte_at"};

doc
~~~
`(binary x, long i, boolean big_endian=false) -> long`

Returns the word (16-bit integer value) at byte offset `i` in binary `x` as a long between 0 and 65535.

Interprets bytes as most significant last. If `big_endian` is `true`, interprets bytes as most significant first.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i+1>=size(x)`.

```tweakflow
> bin.word_at(0b0100, 0)
1

> bin.word_at(0b0100, 0, true)
256

> bin.word_at(0b000F, 0)
3840

> bin.word_at(0b000F, 0, true)
15

> bin.word_at(0bFF, 0)
nil
```
~~~

  function word_at: (binary x, long i, boolean big_endian=false) -> long via {:class "com.twineworks.tweakflow.std.Bin$word_at"};

doc
~~~
`(binary x, long i, boolean big_endian=false) -> long`

Returns the double-word (32-bit integer value) at byte offset `i` in binary `x` as a long between 0 and 4294967295.

Interprets bytes as most significant last. If `big_endian` is `true`, interprets bytes as most significant first.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i+3>=size(x)`.

```tweakflow
> bin.dword_at(0b01000000, 0)
1

> bin.dword_at(0b01000000, 0, true)
16777216

> bin.dword_at(0bFF, 0)
nil
```
~~~

  function dword_at: (binary x, long i, boolean big_endian=false) -> long via {:class "com.twineworks.tweakflow.std.Bin$dword_at"};

doc
~~~
`(binary x, long i, boolean big_endian=false) -> long`

Returns the long (64-bit integer value) at byte offset `i` in binary `x` as a signed long.

Interprets bytes as most significant last. If `big_endian` is `true`, interprets bytes as most significant first.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i+7>=size(x)`.

```tweakflow
> bin.long_at(0b0100000000000000, 0)
1

> bin.long_at(0b0100000000000000, 0, true)
72057594037927936

> bin.long_at(0bFFFFFFFFFFFFFFFF, 0)
-1

> bin.long_at(0bFF, 0)
nil
```
~~~

  function long_at: (binary x, long i, boolean big_endian=false) -> long via {:class "com.twineworks.tweakflow.std.Bin$long_at"};

doc
~~~
`(long x, boolean signed=false) -> binary`

Returns the binary representation of the given byte.

If signed is `true`, `x` must be a value between `-128` and `127`.\
If signed is `false`, `x` must be a value between `0` and `255`.

Returns `nil` if `x` is `nil`.

Throws an error if `signed` is `nil`.

```tweakflow
> bin.of_byte(64)
0b40

> bin.of_byte(192)
0bc0

> bin.of_byte(255)
0bff

> bin.of_byte(-1, true)
0bff

> bin.of_byte(-1, false)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: unsigned byte value out of range: -1
```
~~~
  function of_byte: (long x, boolean signed=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_byte"};

doc
~~~
`(long x, boolean signed=false, boolean big_endian=false) -> binary`

Returns the binary representation of the given 2-byte word.

If `signed` is `true`, `x` must be a value between `−32768` and `32767`.\
If `signed` is `false`, `x` must be a value between `0` and `65535`.

If `big_endian` is `true`, the returned binary starts with the most significant byte.\
If `big_endian` is `false`, the returned binary starts with the least significant byte.

Returns `nil` if `x` is `nil`.

Throws an error if `signed` is `nil`.\
Throws an error if `big_endian` is `nil`.

```tweakflow
> bin.of_word(192)
0bc000

> bin.of_word(192, big_endian: true)
0b00c0

> bin.of_word(65535)
0bffff

> bin.of_word(-32768, signed: true)
0b0080

> bin.of_word(-32768, signed: true, big_endian: true)
0b8000

> bin.of_word(65535, signed: true)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: signed word value out of range: 65535
```
~~~
  function of_word: (long x, boolean signed=false, boolean big_endian=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_word"};

doc
~~~
`(long x, boolean signed=false, boolean big_endian=false) -> binary`

Returns the binary representation of the given 4-byte dword.

If `signed` is `true`, `x` must be a value between `−2147483648` and `2147483647`.\
If `signed` is `false`, `x` must be a value between `0` and `4294967295`.

If `big_endian` is `true`, the returned binary starts with the most significant byte.\
If `big_endian` is `false`, the returned binary starts with the least significant byte.

Returns `nil` if `x` is `nil`.

Throws an error if `signed` is `nil`.\
Throws an error if `big_endian` is `nil`.

```tweakflow
> bin.of_dword(255)
0bff000000

> bin.of_dword(255, big_endian: true)
0b000000ff

> bin.of_dword(-128, signed: true)
0b80ffffff

> bin.of_dword(-128, signed: true, big_endian: true)
0bffffff80

> bin.of_dword(4294967295)
0bffffffff

> bin.of_dword(4294967295, signed: true)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: signed dword value out of range: 4294967295
```
~~~
  function of_dword: (long x, boolean signed=false, boolean big_endian=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_dword"};

doc
~~~
`(long x, boolean big_endian=false) -> binary`

Returns the 64-bit binary representation of the given long value.

If `big_endian` is `true`, the returned binary starts with the most significant byte.\
If `big_endian` is `false`, the returned binary starts with the least significant byte.

Returns `nil` if `x` is `nil`.

Throws an error if `big_endian` is `nil`.

```tweakflow
> bin.of_long(255)
0bff00000000000000

> bin.of_long(255, big_endian: true)
0b00000000000000ff

> bin.of_long(9223372036854775807)
0bffffffffffffff7f

> bin.of_long(-1)
0bffffffffffffffff
```
~~~
  function of_long: (long x, boolean big_endian=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_long"};

doc
~~~
`(double x, boolean big_endian=false) -> binary`

Returns the 32-bit binary representation of the float closest to the given double value.

If `big_endian` is `true`, the returned binary starts with the most significant byte.\
If `big_endian` is `false`, the returned binary starts with the least significant byte.

Returns `nil` if `x` is `nil`.

Throws an error if `big_endian` is `nil`.

```tweakflow
> bin.of_float(0)
0b00000000

> bin.of_float(1)
0b0000803f

> bin.of_float(1, big_endian: true)
0b3f800000

> bin.of_float(NaN)
0b0000c07f

> bin.of_float(math.pi)
0bdb0f4940
```
~~~
  function of_float: (double x, boolean big_endian=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_float"};

doc
~~~
`(double x, boolean big_endian=false) -> binary`

Returns the 64-bit binary representation of the given double value.

If `big_endian` is `true`, the returned binary starts with the most significant byte.\
If `big_endian` is `false`, the returned binary starts with the least significant byte.

Returns `nil` if `x` is `nil`.

Throws an error if `big_endian` is `nil`.

```tweakflow
> bin.of_double(0.0)
0b0000000000000000

> bin.of_double(1.0)
0b000000000000f03f

> bin.of_double(1.0, big_endian: true)
0b3ff0000000000000

> bin.of_double(NaN)
0b000000000000f87f

> bin.of_double(math.pi)
0b182d4454fb210940
```
~~~
  function of_double: (double x, boolean big_endian=false) -> binary via {:class "com.twineworks.tweakflow.std.Bin$of_double"};

doc
~~~
`(binary x, long i, boolean big_endian=false) -> double`

Returns the float (32-bit floating point value) at byte offset `i` in binary `x` as a double.

Interprets bytes as most significant last. If `big_endian` is `true`, interprets bytes as most significant first.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i+3>=size(x)`.

```tweakflow
> bin.float_at(0b00000000, 0)
0.0

> bin.float_at(0b0000803f, 0)
1.0

> bin.float_at(0b0000803f, 0, true)
4.600602988224807E-41

> bin.float_at(0b40490fdb, 0, true)
3.1415927410125732

> bin.float_at(0b0000807f, 0)
Infinity

> bin.float_at(0b0100C0ff, 0)
NaN

> bin.float_at(0bFF, 0)
nil
```
~~~

  function float_at: (binary x, long i, boolean big_endian=false) -> double via {:class "com.twineworks.tweakflow.std.Bin$float_at"};

doc
~~~
`(binary x, long i, boolean big_endian=false) -> double`

Returns the double (64-bit floating point value) at byte offset `i` in binary `x` as a double.

Interprets bytes as most significant last. If `big_endian` is `true`, interprets bytes as most significant first.

Returns `nil` if any argument is `nil`.\
Returns `nil` if `i<0` or `i+7>=size(x)`.

```tweakflow
> bin.double_at(0b0000000000000000, 0)
0.0

> bin.double_at(0b000000000000f03f, 0)
1.0

> bin.double_at(0bc000000000000000, 0, true)
-2.0

> bin.double_at(0b000000000000f07f, 0)
Infinity

> bin.double_at(0b000000000000f0ff, 0)
-Infinity

> bin.double_at(0b010000000000f07f, 0)
NaN

> bin.double_at(0b400921fb54442d18, 0, true)
3.141592653589793

> bin.double_at(0bFF, 0)
nil
```
~~~

  function double_at: (binary x, long i, boolean big_endian=false) -> double via {:class "com.twineworks.tweakflow.std.Bin$double_at"};

doc
~~~
`(binary x, long start=0, long end=nil) -> list`

Returns a range of bytes from `x` starting at byte offset `start` inclusively, and extending to byte offset `end` exclusively.
If `end` is `nil`, the range extends to the end of `x`.

Returns a zero length binary if `start` is greater than the size of `x`.
Returns a zero length binary if `start >= end`.

Returns `nil` if `x` is `nil`.

Throws an error if `start` is `nil` or `start < 0`.

```tweakflow
> bin.slice(0b00010203, 2)
0b0203

> bin.slice(0b00010203040506, 1, 4)
0b010203

> bin.slice(0b, 10, 20)
0b

> bin.slice(0b0001020304, 1, 1)
0b

> bin.slice(nil)
nil
```
~~~

  function slice: (binary x, long start=0, long end=nil) -> binary via {:class "com.twineworks.tweakflow.std.Bin$slice"};

doc
~~~
`(binary x) -> string`

Returns a hexadecimal string representing the binary value `x`.

Returns `nil` if `x` is `nil`.

```tweakflow
> bin.to_hex(0b010A0B04)
"010a0b04"

> bin.to_hex(0b)
""

> bin.to_hex(nil)
nil
```
~~~

  function to_hex: (binary x) -> string via {:class "com.twineworks.tweakflow.std.Bin$to_hex"};

doc
~~~
`(string x) -> binary`

Returns the binary value represented by given hexadecimal string `x`.

Returns `nil` if `x` is `nil`.

Throws an error if `x` contains non-hexadecimal characters.

```tweakflow
> bin.from_hex("010a0b04")
0b010A0B04

> bin.from_hex("")
0b

> bin.from_hex(nil)
nil
```
~~~

  function from_hex: (string x) -> binary via {:class "com.twineworks.tweakflow.std.Bin$from_hex"};

doc
~~~
`(binary x, string variant='basic') -> string`

Returns the base64 string representation of given binary data `x`.

If `variant` is equal to `'basic'`, the standard base64 alphabet of RFC 2045 and RFC 4648 is used.\
If `variant` is equal to `'url'`, the "URL and Filename safe" base64 alphabet of RFC 4648 is used.\
If `variant` is equal to `'mime'`, the standard base64 alphabet of RFC 2045 is used.
In addition the encoded output is represented in lines of no more than 76 characters each
and uses a carriage return `\r` followed immediately by a linefeed `\n` as the line separator.

Returns `nil` if `x` is `nil` or `variant` is `nil`.

Throws an error if `variant` is not `nil` and not equal to any supported variant.

```tweakflow

> bin.base64_encode(0b00010203)
"AAECAw=="

> bin.base64_encode(strings.to_bytes("Hello World"))
"SGVsbG8gV29ybGQ="

> bin.base64_encode(nil)
nil
```
~~~

  function base64_encode: (binary x, string variant='basic') -> string via {:class "com.twineworks.tweakflow.std.Bin$base64_encode"};

doc
~~~
`(string x, string variant='basic') -> binary`

Returns the bytes encoded by given base64 string `x`.

If `variant` is equal to `'basic'`, the standard base64 alphabet of RFC 2045 and RFC 4648 is used.\
If `variant` is equal to `'url'`, the "URL and Filename safe" base64 alphabet of RFC 4648 is used.\
If `variant` is equal to `'mime'`, the standard base64 alphabet of RFC 2045 is used. Any characters
not found in the standard alphabet table are ignored.

Returns `nil` if `x` is `nil` or `variant` is `nil`.

Throws an error if `variant` is not `nil` and not equal to any supported variant.\
Throws an error if `variant` is `basic` or `url` and any non-alphabet characters are encountered.

```tweakflow
> bin.base64_decode("AA==")
0b00

> bin.base64_decode("SGVsbG8gV29ybGQh")
0b48656c6c6f20576f726c6421

> strings.from_bytes(bin.base64_decode("SGVsbG8gV29ybGQh"))
"Hello World!"

> bin.base64_decode(nil)
nil
```
~~~

  function base64_decode: (string x, string variant='basic') -> binary via {:class "com.twineworks.tweakflow.std.Bin$base64_decode"};


}