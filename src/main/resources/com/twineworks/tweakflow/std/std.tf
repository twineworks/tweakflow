# The MIT License (MIT)
#
# Copyright (c) 2017 Twineworks GmbH
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
The standard module contains basic functions for general computing tasks.
~~~

# meta
# {
#   :date "2017-05-12"
#   :since "1.0.0"
# }

module


doc
~~~
The core library contains utility functions to process values at a basic level.
~~~

export library core {

doc
~~~
`(any x) -> any`

Identity function. Returns `x`.

```ruby
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

  function id: (x) -> x

doc
~~~
`(x) -> string`

Returns a string representation of `x`.
If `x` is a function the string `'function'` is returned.
Otherwise literal notation is used.
If `x` is not a function, and contains no functions as children `x == core.eval(core.inspect(x))` generally holds true.

```ruby
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
  function inspect: (x) -> string via {:class "com.twineworks.tweakflow.std.Core$inspect"}

doc
~~~
`(x) -> boolean`

Returns `false` if `x` is `nil`, returns `true` otherwise.

```ruby
> core.present?("foo")
true

> core.present?(nil)
false
```
~~~

  function present?:(x) -> boolean via {:class "com.twineworks.tweakflow.std.Core$present"}

doc
~~~
`(x) -> boolean`

Returns `true` if `x` is `nil`, returns `false` otherwise.

```ruby
> core.nil?("foo")
false

> core.nil?(nil)
true
```
~~~

  function nil?:    (x) -> boolean                  via {:class "com.twineworks.tweakflow.std.Core$isNil"}

doc
~~~
`(x) -> long`

Returns a hashcode of `x`. Values that compare as equal are guaranteed to have the same hashcode.

```ruby
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
  function hash:    (x) -> long                     via {:class "com.twineworks.tweakflow.std.Core$hash"}

doc
~~~
`(string x) -> any`

Evaluates tweakflow source code `x` in empty scope.
If `x` represents a valid value, the value it is returned.
If `x` cannot be evaluated, an error is thrown.
`x` is evaluated in empty scope, so any references within `x` must be self-contained.
Functions can be declared and called within `x`.

Returns `nil` if `x` is `nil`.

```ruby
> core.eval("1")
1

> core.eval("'str'")
"str"

> core.eval(nil)
nil

> core.eval("(x) -> x+1")(1)
2

> core.eval("let {f: (x) -> x*x} f(5)")
25

> core.eval("hello")
ERROR:
  code: UNRESOLVED_REFERENCE
  message: hello is undefined
  at: <eval>:1:1
  source: hello
```
~~~
  function eval:    (string x) -> via {:class "com.twineworks.tweakflow.std.Core$eval"}
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

```ruby
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
  function concat: (list xs) -> string via {:class "com.twineworks.tweakflow.std.Strings$concat"}

doc
~~~
`(string x) -> long`

Returns the number of unicode codepoints in given string `x`.

Returns `nil` if `x` is `nil`.

```ruby
> strings.length("")
0

> strings.length("foo")
3

> strings.length("你好")
2
```
~~~
  function length: (string x) -> long via {:class "com.twineworks.tweakflow.std.Strings$length"}

doc
~~~
`(string x, long start=0, long end=nil) -> string`

Returns a substring of `x` beginning at index `start` (inclusive) up to index `end` (exclusive).
If `end` is `nil`, the substring extends to the end of `x`.

Returns an empty string if `end <= start`.

Returns `nil` if `x` is `nil`.

Throws an error if `start` is `nil` or `start < 0`.

```ruby
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
  at: [interactive]:14:10
  source: strings.substring("hello world", nil)

> strings.substring("hello world", -4)
ERROR:
  code: INDEX_OUT_OF_BOUNDS
  message: start must not be negative: -4
  at: [interactive]:14:10
  source: strings.substring("hello world", -4)
```
~~~

  function substring: (string x, long start=0, long end=nil) -> string via {:class "com.twineworks.tweakflow.std.Strings$substring"}

doc
~~~
`(string x, string search, string replace) -> string`

Returns a string where each occurrence of `search` in `x` is replaced with `replace`.
The replacement happens from left to right,
so `search.replace('ooo', 'oo', 'g')` results in `'go'` rather than `'og'`.

Returns `nil` if any argument is `nil`.

```ruby
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

  function replace: (string x, string search, string replace) -> string via {:class "com.twineworks.tweakflow.std.Strings$searchReplace"}

doc
~~~
`(list xs, string s="") -> string`

Concatenates a list of `xs` into a single string using `s` as the separator.
Each `x` in `xs` is cast to a string before concatenation.
If any `x` cannot be cast to a string, an error is thrown.
If any `x` is `nil`, it is concatenated into the output string as `"nil"`.

Returns `nil` if `xs` is `nil` or `s` is `nil`.

```ruby
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
  function join: (list xs, string s="") -> string via {:class "com.twineworks.tweakflow.std.Strings$join"}

#  function lower_case: (string x) -> string         via {:class "com.twineworks.tweakflow.std.Strings$lowerCase"}
#  function upper_case: (string x) -> string         via {:class "com.twineworks.tweakflow.std.Strings$upperCase"}

doc
~~~
`(string x) -> string`

Returns `x` with leading and trailing whitespace characters removed.

Returns `nil` if `x` is `nil`.

```ruby
> strings.trim("  foo  ")
"foo"

> strings.trim("line\r\n")
"line"

> strings.trim(nil)
nil
```
~~~
  function trim: (string x) -> string via {:class "com.twineworks.tweakflow.std.Strings$trim"}


doc
~~~
`(string x, string lang='en-US') -> string`

Returns `x` with all characters converted to lower case as per conventions of the given language tag.

Returns `nil` if `x` is `nil` or `lang` is `nil`.

```ruby
> strings.lower_case("FOO")
"foo"

> strings.lower_case("TITLE")
"title"

> strings.lower_case("TITLE", "tr")
"tıtle" # note the dotless i in turkish language
```
~~~
  function lower_case: (string x, string lang="en-US") -> string via {:class "com.twineworks.tweakflow.std.Strings$lower_case"}

doc
~~~
`(string x, string lang='en-US') -> string`

Returns `x` with all characters converted to uper case as per conventions of the given language tag.

Returns `nil` if `x` is `nil` or `lang` is `nil`.

```ruby
> strings.upper_case("foo")
"FOO"

> strings.upper_case("straße")
"STRASSE" # note that some characters may expand to multiple characters

> strings.upper_case("title", "tr")
"TİTLE" # note the dotted upper case I in turkish language
```
~~~
  function upper_case: (string x, string lang="en-US") -> string via {:class "com.twineworks.tweakflow.std.Strings$upper_case"}

doc
~~~
`(string lang='en-US', boolean case_sensitive=true) -> function`

Returns a function `f (string a, string b) -> long` that compares two strings `a` and `b` according to conventions appropriate for the given language tag.
If `case_sensitive` is `true`, lower case characters precede their upper case counterparts.
If `case_sensitive` is `false`, lower case characters are considered equal to their upper case conterparts.

`f` returns -1 if a < b.\
`f` returns 1 if a > b.\
`f` returns 0 if a == b.\

`f` considers `nil` less than any non-nil string.

Throws an error if any argument is `nil`.

```ruby
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

  function comparator: (lang='en-US', case_sensitive=true) -> function via {:class "com.twineworks.tweakflow.std.Strings$comparator"}

doc
~~~
`(string x) -> list`

Returns a list of unicode codepoints that make up given string `x`.

Returns `nil` if `x` is `nil`.

```ruby
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
  function chars: (string x) -> list via {:class "com.twineworks.tweakflow.std.Strings$chars"}

doc
~~~
`(string x, string s=" ") -> list`

Returns a list of strings obtained by splitting `x` using separator `s`.

Returns `nil` if `x` is `nil` or `s` is `nil`.

```ruby
> strings.split("hello world")
["hello", "world"]

> strings.split("a,b,c", ",")
["a", "b", "c"]
```
~~~

  function split: (string x, string s=" ") -> list via {:class "com.twineworks.tweakflow.std.Strings$split"}

doc
~~~
`(string x, string init) -> boolean`

Returns `true` if `x` starts with the substring `init`.\
Returns `false` otherwise.

Returns `nil` if `x` is `nil` or `init` is `nil`.

```ruby
> strings.starts_with?("yellow", "yell")
true

> strings.starts_with?("yellow", "blue")
false

> strings.starts_with?(nil, "blue")
nil

> strings.starts_with?("yellow", nil)
nil
```
~~~

  function starts_with?: (string x, string init) -> boolean via {:class "com.twineworks.tweakflow.std.Strings$startsWith"}

doc
~~~
`(string x, string tail) -> boolean`

Returns `true` if `x` ends with the substring `tail`.\
Returns `false` otherwise.

Returns `nil` if `x` is `nil` or `tail` is `nil`.

```ruby
> strings.ends_with?("yellow", "low")
true

> strings.ends_with?("yellow", "high")
false

> strings.ends_with?(nil, "blue")
nil

> strings.ends_with?("yellow", nil)
nil
```
~~~

  function ends_with?: (string x, string tail) -> boolean via {:class "com.twineworks.tweakflow.std.Strings$endsWith"}

doc
~~~
`(string x, string sub, long start=0) -> long`

If `x` contains `sub` at or past index `start`, the function returns the index of the first occurrence of `sub`
at or past index `start`.

Returns `-1` otherwise.

Returns `nil` if any argument is `nil`.

```ruby
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

  function index_of: (string x, string sub, long start=0) -> long via {:class "com.twineworks.tweakflow.std.Strings$indexOf"}

doc
~~~
`(string x, string sub, long end=nil) -> long`

If `x` contains `sub` at or before index `end` returns the index of the last such occurrence. Returns `-1` otherwise.
If `end` is `nil`, it is interpreted as the last index of `x`.

Returns `nil` if `x` is `nil` or `sub` is `nil`.

```ruby
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

  function last_index_of: (string x, string sub, long end=nil) -> long via {:class "com.twineworks.tweakflow.std.Strings$lastIndexOf"}

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

Returns a predicate function `f` accepting one string argument, that checks if the argument matches the given `pattern` completely.
Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.


```ruby
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

  function matching: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$matching"}

doc
~~~
`(string pattern) -> function`

Returns a function `f` accepting one string argument and returning a list of captured groups in the pattern.

In case `pattern` matches the argument entirely, index `0` contains the input string, and subsequent indexes contain
the matched groups. Group indexes correspond to the sequence of opening group parentheses of capturing groups in the pattern.
If a group is optional, and is not matched by the input string, its value in the list is `nil`.

In case `pattern` does not match the argument entirely, `f` returns an empty list.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.

```ruby
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

  function capturing: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$capturing"}

doc
~~~
`(string pattern) -> function`

Returns a function `f` accepting one string argument `x` and returning a list of captured groups for each successive match
of the pattern in `x`.

Each match generates a list with the following properties: Index `0` contains the matched input string, and subsequent indexes contain
the matched groups. Group indexes correspond to the sequence of opening group parentheses of capturing groups in the pattern.
If a group is optional, and is not matched by the input string, its value in the list is `nil`.

In case `pattern` does not match `x`, `f` returns an empty list.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if `pattern` is `nil` or not a valid regular expression.

```ruby
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

  function scanning: (string pattern) -> function via {:class "com.twineworks.tweakflow.std.Regex$scanning"}


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

```ruby
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

  function splitting: (string pattern, long limit=nil) -> function via {:class "com.twineworks.tweakflow.std.Regex$splitting"}

doc
~~~
`(string pattern, string replace) -> function`

Returns a function `f` accepting one string argument and returning a string replacing all occurrences of `pattern` with `replace`.
References to capturing groups in `replace` are possible through `$n` syntax, where `n` is the number of the capturing group, as counted by
the occurrence of opening group parentheses. To replace a literal `$n`, escape the `$` sign as `\$`.

Calling `f` with a `nil` argument returns `nil`.

Throws an error if any argument is `nil` or `pattern` is not a valid regular expression.

```ruby
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

  function replacing: (string pattern, string replace) -> function via {:class "com.twineworks.tweakflow.std.Regex$replacing"}

doc
~~~
`(string x) -> string`

Returns a string in which each character of `x` with special meaning in a pattern has been escaped.
The resulting string matches `x` literally as a pattern.

Returns `nil` if `x` is `nil`.

```ruby
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

  function quote: (string x) -> string via {:class "com.twineworks.tweakflow.std.Regex$quote"}

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

```ruby
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
  at: [interactive]:14:10
  source: data.size("foo")
```
~~~

  function size: (any xs) -> long                               via {:class "com.twineworks.tweakflow.std.Data$size"}

doc
~~~
`(any xs) -> boolean`

Given a dict or list of `xs`, returns true if the collection is empty, false otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`.

```ruby
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
  at: [interactive]:14:10
  source: data.empty?("foo")
```
~~~

  function empty?: (any xs) -> boolean                          via {:class "com.twineworks.tweakflow.std.Data$empty"}

doc
~~~
`(xs, key, not_found) ->`

Given a dict or list of `xs`, and a key, returns `xs[key]` if the key is present in `xs`.
Returns `not_found` if `key` is not present in `xs`.

Returns `nil` if `xs` is `nil`, or `key` is `nil`.

Throws an error if `xs` is neither a `dict` nor a `list`. \
Throws an error if `xs` is a `list` and `key` cannot be cast to a `long`. \
Throws an error if `xs` is a `dict` and `key` cannot be cast to a `string`.

```ruby
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

  function get: (xs, key, not_found) ->                     via {:class "com.twineworks.tweakflow.std.Data$get"}

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

```ruby
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

  function put: (xs, key, value) ->                         via {:class "com.twineworks.tweakflow.std.Data$put"}


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

```ruby
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
  at: [interactive]:14:10
  source: data.get_in({:a [1,2,3], :b [4,5,6]}, [:b :c], "default")
```
~~~

  function get_in: (any xs, list keys, any not_found) ->    via {:class "com.twineworks.tweakflow.std.Data$getIn"}

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

```ruby

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

  function put_in: (xs, list keys, value) ->                via {:class "com.twineworks.tweakflow.std.Data$putIn"}

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

```ruby
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

  function update: (xs, key, function f) ->                 via {:class "com.twineworks.tweakflow.std.Data$update"}

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

```ruby

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

  function update_in: (xs, list keys, function f) ->        via {:class "com.twineworks.tweakflow.std.Data$updateIn"}

doc
~~~
`(xs) -> list `

Given a `list` or `dict` of `xs`, returns a list of keys present in the structure.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
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
  at: [interactive]:14:10
  source: data.keys("foo")

```
~~~

  function keys: (xs) -> list                               via {:class "com.twineworks.tweakflow.std.Data$keys"}

doc
~~~
`(xs, key) -> boolean`

Returns `true` if `key` is a key in `xs`, false otherwise.
If `xs` is a `list`, `key` is cast to a `long`.
If `xs` is a `dict`, `key` is cast to a `string`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list`, nor a `dict`.

```ruby
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
  at: [interactive]:14:10
  source: data.has?("foo", 1)

> data.has?("foo" as list, 1)
true
```
~~~

  function has?: (xs, key) -> boolean                       via {:class "com.twineworks.tweakflow.std.Data$has"}

doc
~~~
`(xs) -> list `

Given a `list` or `dict` of `xs`, returns a list of values present in the structure.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
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
  at: [interactive]:14:10
  source: data.values("foo")
```
~~~

  function values: (xs) -> list                             via {:class "com.twineworks.tweakflow.std.Data$values"}

doc
~~~
`(dict xs) -> list`

Returns a `list` where each element is a `dict`. For each key `k` in `xs` there is a correpsonding element `{:key k, :value xs[k]}` in the `list`.

Returns `nil` if `xs` is `nil`.

```ruby
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

  function entries: (dict xs) -> list via {:class "com.twineworks.tweakflow.std.Data$entries"}

doc
~~~
`(x, list xs) -> list`

Returns a list consisting of `x` followed by all elements of `xs`.

Returns `nil` if `xs` is `nil`.

```ruby
> data.prepend("a", ["b", "c"])
["a", "b", "c"]

> data.prepend("a", [])
["a"]

> data.prepend("a", nil)
nil
```
~~~

  function prepend: (x, list xs) -> list                    via {:class "com.twineworks.tweakflow.std.Data$prepend"}

doc
~~~
`(list xs, x) -> list`

Returns a list consisting of all elements of `xs` followed by `x`.

Returns `nil` if `xs` is `nil`.

```ruby
> data.append(["a", "b"], "c")
["a", "b", "c"]

> data.append([], "a")
["a"]

> data.append(nil, "a")
nil
```
~~~
  function append: (list xs, x) -> list                     via {:class "com.twineworks.tweakflow.std.Data$append"}

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

```ruby
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
  at: [interactive]:14:10
  source: data.find([], nil)
```
~~~
  function find: (list xs, function p) ->                   via {:class "com.twineworks.tweakflow.std.Data$find"}

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

```ruby
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
  at: [interactive]:14:10
  source: data.find_index([], nil)
```
~~~
  function find_index: (list xs, function p) -> long        via {:class "com.twineworks.tweakflow.std.Data$findIndex"}


doc
~~~
`(list xs, long i, v) ->`

Returns a list consisting of all elements of `xs`, with elements starting at index `i` shifted to the right, and `v` inserted at position `i`.
If `i` is bigger than the largest index in `xs`, any intermediate indexes are created with value `nil`.

Returns `nil` if `xs` is nil.

Throws an error if index is negative or `nil`.

```ruby
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
  at: [interactive]:14:10
  source: data.insert([], -2, "a")

> data.insert([], nil, "a")
ERROR:
  code: NIL_ERROR
  message: cannot insert at nil index
  at: [interactive]:14:10
  source: data.insert([], nil, "a")

```
~~~

  function insert: (list xs, long i, v) ->                  via {:class "com.twineworks.tweakflow.std.Data$insert"}

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

```ruby
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
  at: [interactive]:14:10
  source: data.delete([], -2)
```
~~~

  function delete: (xs, key) ->                             via {:class "com.twineworks.tweakflow.std.Data$delete"}

doc
~~~
`(xs, list keys, not_found) ->`

Given a `list` or `dict` `xs`, returns a collection of the same type, with elements at all `keys` from `xs`.
If `xs` does not have any of the `keys`, `not_found` is included in the result instead.

Returns `nil` if `xs` is `nil` or `keys` is `nil`.

Throws an error if `xs` is a `list` and a `key` cannot be cast to `long`.\
Throws an error if `xs` is a `dict` and a `key` cannot be cast to `string`.

```ruby
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

  function select: (xs, list keys, not_found) ->            via {:class "com.twineworks.tweakflow.std.Data$select"}

doc
~~~
`(xs, function p) ->`

Given a `list` or `dict` `xs`, returns a collection of the same type,
with all elements for which predicate function `p` returns a value that casts to `boolean` `true`.

If `p` accepts one argument, `x` is passed. If `p` accepts two arguments, `x` and the key or index of `x` in `xs` is passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.\
Throws an error if `p` is `nil`.

```ruby
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
  at: [interactive]:14:10
  source: data.filter([], nil)

> data.filter(1, (x) -> true)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: filter is not defined for type long
  at: [interactive]:14:10
  source: data.filter(1, (x) -> true)
```
~~~

  function filter: (xs, function p) ->                      via {:class "com.twineworks.tweakflow.std.Data$filter"}

doc
~~~
`(list xs, seed) -> list`

Returns a list with all elements of `xs` in random order. The reordering of items is based on `seed`.
The same seed will shuffle lists of equal length in the same way.

Returns `nil` if `xs` is `nil`.

```ruby
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

  function shuffle: (list xs, seed) -> list                 via {:class "com.twineworks.tweakflow.std.Data$shuffle"}

doc
~~~
`(list xs) -> list `

Returns a list with all duplicates in `xs` removed. Values are considered duplicates when they compare as equal.

Returns `nil` if `xs` is `nil`.

```ruby
> data.unique([1, 1, 2, 3, 3])
[1, 2, 3]

> data.unique([1.0, 1, 2.0, 2, 3.0, 3])
[1.0, 2.0, 3.0]

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

  function unique: (list xs) -> list                        via {:class "com.twineworks.tweakflow.std.Data$unique"}

doc
~~~
`(long start=0, long end=0) -> list`

Returns a list with long values starting at `start`, and ending at `end`.

Returns an empty list if `start > end`.

Returns `nil` if `start` is `nil` or `end` is `nil`.

```ruby
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

  function range: (long start=0, long end=0) -> list          via {:class "com.twineworks.tweakflow.std.Data$range"}

doc
~~~
`(list xs, function p) -> boolean`

Tests items `x` in `xs` with predicate function `p`. If `p` accepts one argument, `x` is passed. If `p` accepts
two arguments, `x` and the index of `x` in `xs` are passed.

Returns true if `p` returns a value that casts to boolean `true` for any `x` in `xs`.
Returns false otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```ruby
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

  function any?: (list xs, function p) -> boolean           via {:class "com.twineworks.tweakflow.std.Data$any"}

doc
~~~
`(list xs, function p) -> boolean`

Tests items `x` in `xs` with predicate function `p`. If `p` accepts one argument, `x` is passed. If `p` accepts
two arguments, `x` and the index of `x` in `xs` are passed.

Returns false if `p` returns a value that casts to boolean `true` for any `x` in `xs`.
Returns true otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `p` is `nil`.

```ruby
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

  function none?: (list xs, function p) -> boolean          via {:class "com.twineworks.tweakflow.std.Data$none"}

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

```ruby
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

  function all?: (list xs, function p) -> boolean           via {:class "com.twineworks.tweakflow.std.Data$all"}

doc
~~~
`(list xs) -> list`

Returns a list containing all elements of `xs` with the exception of the last one.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```ruby
> data.init([1,2,3])
[1, 2]

> data.init(nil)
nil

> data.init([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
  at: [interactive]:14:10
  source: data.init([])

```
~~~

  function init: (list xs) -> list                          via {:class "com.twineworks.tweakflow.std.Data$init"}

doc
~~~
`(list xs) -> list`

Returns a list containing all elements of `xs` with the exception of the first one.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```ruby
> data.tail([1,2,3])
[2, 3]

> data.tail(nil)
nil

> data.tail([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
  at: [interactive]:14:10
  source: data.tail([])
```
~~~

  function tail: (list xs) -> list                          via {:class "com.twineworks.tweakflow.std.Data$tail"}

doc
~~~
`(list xs) -> any`

Returns the first element in `xs`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```ruby
> data.head([1,2,3])
1

> data.head(nil)
nil

> data.head([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
  at: [interactive]:14:10
  source: data.head([])
```
~~~

  function head: (list xs) -> any                           via {:class "com.twineworks.tweakflow.std.Data$head"}

doc
~~~
`(list xs) -> any`

Returns the last element in `xs`.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is empty.

```ruby
> data.last([1,2,3])
3

> data.last(nil)
nil

> data.last([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: list must not be empty
  at: [interactive]:14:10
  source: data.last([])
```
~~~

  function last: (list xs) -> any                           via {:class "com.twineworks.tweakflow.std.Data$last"}

doc
~~~
`(list xs, long start=0, long end=nil) -> list`

Returns a sublist of `xs` starting at index `start` inclusively, and extending to index `end` exclusively.
If `end` is `nil`, the sublist extends to the end of `xs`.

Returns an empty list if `start` is greater than the last index in `xs`.
Returns an empty list if `start >= end`.

Returns `nil` if `xs` is `nil`.

Throws an error if `start` is `nil` or `start < 0`.

```ruby
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

  function slice: (list xs, long start=0, long end=nil) -> list via {:class "com.twineworks.tweakflow.std.Data$slice"}

doc
~~~
`(list xs, long s=1) -> list`

Partitions `xs` into slices. Returns a list of slices, each of size `s`. The last slice may contain less than `s` items.

Returns an empty list if `xs` is empty.

Returns `nil` if any argument is `nil`.

Throws an error if `s <= 0`.


```ruby
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
    if xs == nil || s == nil then nil
    if s <= 0 then throw {:message "s must be positive, was #{s}" :code "ILLEGAL_ARGUMENT"}
    if empty?(xs) then []
    let {
      slice_count: math.ceil(size(xs)/s)
      init: {:xs xs, :slices []}
      iteration: (a) -> {
                  :xs drop(s, a[:xs]),
                  :slices [...a[:slices], take(s, a[:xs])]
                 }
    }
    fun.times(slice_count, init, iteration)[:slices]

doc
~~~
`(list xs) -> list`

Returns a list that contains all elements of `xs` in reverse order.

Returns `nil` if `xs` is `nil`.

```ruby
> data.reverse([1, 2, 3])
[3, 2, 1]

> data.reverse(nil)
nil
```
~~~

  function reverse: (list xs) -> list                       via {:class "com.twineworks.tweakflow.std.Data$reverse"}

doc
~~~
`(list xs, function f) -> list`

Returns a list sorted according to comparator function f. The sorting algorithm is guaranteed to be stable.

The comparator function accepts two arguments: a and b. \
If a < b, f returns a negative number.\
If a > b, f returns a positive number.\
If a == b, f returns 0.

```ruby
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

  function sort: (list xs, function f) -> list              via {:class "com.twineworks.tweakflow.std.Data$sort"}

doc
~~~
`(long n, x) -> list`

Returns a list of length `n` that contains `x` in every position.

Returns `nil` if `n` is `nil`.

Throws an error if `n` is negative.

```ruby
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
  at: [interactive]:14:10
  source: data.repeat(-2, "foo")
```
~~~

  function repeat: (long n, x) -> list                    via {:class "com.twineworks.tweakflow.std.Data$repeat"}

doc
~~~
`(list lists) -> list`

Returns a list that contains all elements of all `lists`. Lists are concatenated in given order.

Returns `nil` if `lists` is `nil`. \
Returns `nil` if any element of `lists` is `nil`.

Throws an error if any element of `lists` is not a `list`.

```ruby
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
  at: [interactive]:14:10
  source: data.concat(["foo", "bar"])
```
~~~

  function concat: (list lists) -> list                     via {:class "com.twineworks.tweakflow.std.Data$concat"}

doc
~~~
`(list dicts) -> dict`

Returns a `dict` that contains all entries from all given `dicts`. Dicts are merged left to right.
In case of duplicate keys across `dicts`, the rightmost occurrence takes precedence.

Returns `nil` if `dicts` is `nil`. \
Returns `nil` if any element of `dicts` is `nil`.

Throws an error if any element of `dicts` is not a `dict`.

```ruby
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
  at: [interactive]:14:10
  source: data.merge(["foo", "bar"])
```
~~~

  function merge: (list dicts) -> dict                      via {:class "com.twineworks.tweakflow.std.Data$merge"}

doc
~~~
`(long n, list xs) -> list`

Returns a list containing the first `n` elements of `xs`.
Returns `xs` if `n` is greater than the size of `xs`.
Returns an empty list if `n` is negative.

Returns `nil` if `n` is `nil` or `xs` is `nil`.

```ruby
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

  function take: (long n, list xs) -> list                  via {:class "com.twineworks.tweakflow.std.Data$take"}

doc
~~~
`(function p, list xs) -> list`

Returns a list containing leading elements `x` of `xs` for which `p(x)` casts to `boolean` `true`.

Returns `nil` if `p` is `nil` or `xs` is `nil`.

```ruby

> data.take_while((x) -> x < 5, [2, 3, 3, 2, 5, 1, 2])
[2, 3, 3, 2]

> data.take_while((x) -> x % 2 == 0, [0, 2, 4, 6, 7, 8, 9, 10])
[0, 2, 4, 6]

> data.take_while((x) -> x % 2 == 0, [1, 2, 3, 4])
[]

> data.take_while(nil, [1, 2, 3])
nil

> data.take_while((x) -> true, nil)
nil
```
~~~

  function take_while: (function p, list xs) -> list
    if p == nil then nil
    if xs == nil then nil

    let {
      max: size(xs)
      count: fun.while((i) -> p(xs[i]) && i < max, 0, math.inc)
    }
    take(count, xs)

doc
~~~
`(function p, list xs) -> list`

Returns a list containing leading elements `x` of `xs` for which `p(x)` casts to `boolean` `false`.

Returns `nil` if `p` is `nil` or `xs` is `nil`.

```ruby

> data.take_until((x) -> x >= 5, [2, 3, 3, 2, 5, 1, 2])
[2, 3, 3, 2]

> data.take_until((x) -> x % 2 != 0, [0, 2, 4, 6, 7, 8, 9, 10])
[0, 2, 4, 6]

> data.take_until((x) -> x % 2 != 0, [1, 2, 3, 4])
[]

> data.take_until((x) -> x > 10, [1, 2, 3, 4])
[1, 2, 3, 4]

> data.take_until(nil, [1, 2, 3])
nil

> data.take_until((x) -> true, nil)
nil
```
~~~

  take_until: (function p, list xs) -> list
    if p == nil then nil
    if xs == nil then nil

    let {
      max: size(xs)
      count: fun.until((i) -> p(xs[i]) || i >= max, 0, math.inc)
    }
    take(count, xs)

doc
~~~
`(long n, list xs) -> list`

Returns a list of elements in `xs` skipping the first `n` elements.
Returns an empty list if `n` is greater than the size of `xs`.
Returns `xs` if `n` is negative.

Returns `nil` if `n` is `nil` or `xs` is `nil`.

```ruby
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
  function drop: (long n, list xs) -> list                  via {:class "com.twineworks.tweakflow.std.Data$drop"}

doc
~~~
`(function p, list xs) -> list`

Returns a list of elements in `xs` skipping leading elements `x` for which `p(x)` casts to `boolean` `true`.

Returns `nil` if `p` is `nil` or `xs` is `nil`.

```ruby
> data.drop_while((x) -> x <= 2, [1, 2, 3, 4])
[3, 4]

> data.drop_while((x) -> x > 10, [1, 2, 3])
[1, 2, 3]

> data.drop_while((x) -> false, nil)
nil

> data.drop_while(nil, ["a", "b", "c"])
nil
```
~~~

  function drop_while: (function p, list xs) -> list
    if p == nil then nil
    if xs == nil then nil

    let {
      max: size(xs)
      count: fun.while((i) -> p(xs[i]) && i < max, 0, math.inc)
    }
    drop(count, xs)

doc
~~~
`(function p, list xs) -> list`

Returns a list of elements in `xs` skipping leading elements `x` for which `p(x)` casts to `boolean` `false`.

Returns `nil` if `p` is `nil` or `xs` is `nil`.

```ruby
> data.drop_until((x) -> x > 2, [1, 2, 3, 4])
[3, 4]

> data.drop_until((x) -> x <= 2, [1, 2, 3, 4])
[1, 2, 3, 4]

> data.drop_until((x) -> x > 10, [1, 2, 3, 4])
[]

> data.drop_until((x) -> false, nil)
nil

> data.drop_until(nil, ["a", "b", "c"])
nil
```
~~~

  function drop_until: (function p, list xs) -> list
    if p == nil then nil
    if xs == nil then nil

    # let {
    #   start: size(xs)-1
    #   count: fun.until((i) -> p(xs[i]) && i >= 0, start, math.dec)
    # }

    let {
      max: size(xs)
      count: fun.until((i) -> p(xs[i]) || i >= max, 0, math.inc)
    }
    drop(count, xs)

doc
~~~
`(xs, x) -> boolean`

If `xs` is a `list`, returns `true` if `xs` contains `x`, `false` otherwise.\n
If `xs` is a `dict`, returns `true` if any entry in `xs` contains `x` as a value, `false` otherwise.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
> data.contains?([1,2,3], 2)
true

> data.contains?([1,2,3], 4)
false

> data.contains?({:a 1, :b 2, :c 3}, 2)
true

> data.contains?({:a 1, :b 2, :c 3}, 4)
false

> data.contains?(nil, 0)
nil

> data.contains?("foo", "bar")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: contains? is not defined for type string
  at: [interactive]:14:10
  source: data.contains?("foo", "bar")
```
~~~

  function contains?: (xs, x) -> boolean                       via {:class "com.twineworks.tweakflow.std.Data$contains"}

doc
~~~
`(list xs, x, long start=0) -> long`

If `xs` contains `x` at or after index `start` returns the index of the earliest such occurrence. Returns -1 otherwise.

Returns `nil` if `xs` is `nil`.\
Returns `nil` if `start` is `nil`.

```ruby
> data.index_of([1,2,3], 2)
1

> data.index_of([1,2,3], 4)
-1

> data.index_of([1,2,3], 2, 2)
-1

> data.index_of([1,2,3,1,2,3], 3, 4)
5

> data.index_of(nil, 2)
nil

> data.index_of([1,2,3], 3, nil)
nil
```
~~~

  function index_of: (list xs, x, long start=0) -> long         via {:class "com.twineworks.tweakflow.std.Data$indexOf"}

doc
~~~
`(list xs, x, long end=nil) -> long`

If `xs` contains `x` at or before index `end` returns the index of the last such occurence. Returns -1 otherwise.
If `end` is `nil`, it is interpreted as the last index of `xs`.

Returns `nil` if `xs` is `nil`.

```ruby
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

> data.last_index_of(nil, 2)
nil
```
~~~

  function last_index_of: (list xs, x, long end=nil) -> long   via {:class "com.twineworks.tweakflow.std.Data$lastIndexOf"}

doc
~~~
`(dict xs, x) -> string`

If `xs` contains an entry with value `x`, returns the key of that entry.
If there are multiple such entries, it is undefined which of the keys is returned.

Returns `nil` if `xs` does not contain an entry with value `x`.

Returns `nil` if `xs` is `nil`.

```ruby
> data.key_of({:a 1, :b 2}, 1)
"a"

> data.key_of({:a 1, :b 2}, 3)
nil

> data.key_of({:foo 1, :bar 1}, 1)
"foo"

> data.key_of({:foo 1, :doo 1}, 1)
"doo"

> data.key_of(nil, 1)
nil
```
~~~

  function key_of: (dict xs, x) -> string                   via {:class "com.twineworks.tweakflow.std.Data$keyOf"}

doc
~~~
`(list xs) -> list`

Builds a result list iterating over all elements `x` in `xs`. If `x` is a list, it is concatenated to the result list.
Otherwise the element is appended to the list. Returns the result list.

Returns nil if `xs` is `nil`.

```ruby
> data.flatten([[1, 2, 3], 4, 5, [6, 7, 8]])
[1, 2, 3, 4, 5, 6, 7, 8]

> data.flatten([1, 2, 3])
[1, 2, 3]

> data.flatten([1, nil, []])
[1, nil]

> data.flatten([[[1]], [[2]]])
[[1], [2]]

> data.flatten(nil)
nil
```
~~~

  function flatten: (list xs) -> list
    reduce(xs, [], (a, x) -> if x is list then [...a, ...x] else [...a, x])

doc
~~~
`(xs, function f) -> any`

If `xs` is a `list`, returns a `list` of all `x` in `xs` mapped through `f`.
If `f` accepts one argument, `x` is passed.
If `f` accepts two arguments, `x` and the index of `x` are passed.

If `xs` is a `dict`, returns a `dict` of all entries mapped through `f`.
If `f` accepts one argument, each entrie's value is passed.
If `f` accepts two arguments, each entries's value and the key are passed.

Returns `nil` if `xs` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
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
  at: [interactive]:14:10
  source: data.map("foo", (x) -> x)
```
~~~

  function map: (xs, function f) -> via {:class "com.twineworks.tweakflow.std.Data$map"}

doc
~~~
`(xs, function f) -> list`

Maps each `x` of `xs` through `f`, concatenating the results if they are lists, appending them otherwise.

If `f` accepts a single argument, `x` is passed.
If `f` accepts two arguments, `x` and its index or key are passed.

Returns the resulting list.

Returns `nil` if `xs` is `nil`.\n
Returns `nil` if `f` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
> data.flatmap([1, 2, 3], (x) -> data.repeat(x, x))
[1, 2, 2, 3, 3, 3]

> data.flatmap([1, 2, 1], (x) -> if x == 1 then ["a", "b", "c"] else "-")
["a", "b", "c", "-", "a", "b", "c"]

> data.flatmap(nil, (x) -> x)
nil

> data.flatmap([1, 2, 3], nil)
nil

> data.flatmap("foo", (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: map is not defined for type string
  at: [interactive]:14:10
  source: data.flatmap("foo", (x) -> x)
```
~~~

  function flatmap: (xs, function f) -> list
    reduce(map(xs, f), [], (a, x) -> if x is list then [...a, ...x] else [...a, x])

doc
~~~
`(xs, function f) -> list`

Maps each `x` of `xs` through `f`, concatenating the results if they are lists, ignoring them otherwise.

If `f` accepts a single argument, `x` is passed.
If `f` accepts two arguments, `x` and its index or key are passed.

Returns the resulting list.

Returns `nil` if `xs` is `nil`.\n
Returns `nil` if `f` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
> data.mapcat([1, 2, 3], (x) -> data.repeat(x, x))
[1, 2, 2, 3, 3, 3]

> data.mapcat([1, 2, 1], (x) -> if x == 1 then ["a", "b", "c"] else "nothing")
["a", "b", "c", "a", "b", "c"]

> data.mapcat(nil, (x) -> x)
nil

> data.mapcat([1, 2, 3], nil)
nil

> data.mapcat("foo", (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: map is not defined for type string
  at: [interactive]:14:10
  source: data.mapcat("foo", (x) -> x)
```
~~~
  function mapcat: (xs, function f) -> list
    reduce(map(xs, f), [], (list a, x) -> if x is list then [...a, ...x] else a)

doc
~~~
`(list xs, list ys) -> list`

Returns a list of same length as `xs`. Each index `i` contains the value `[xs[i], ys[i]]`.

Returns `nil` if `xs` or `ys` are `nil`.

```ruby
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
    if xs == nil then nil
    if ys == nil then nil
    reduce(xs, [], (z, a, i) -> [...z, [a, ys[i]]])


doc
~~~
`(list keys, list values) -> dict`

Returns a `dict` with all keys from `keys`. Each key contains the value `values[i]`, where `i`
is the index of the key in `keys`.

In case there are duplicate keys in `keys`, the last index of a key provides the corresponding value.

Returns `nil` if `keys` or `values` are `nil`.

```ruby
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
    if keys == nil then nil
    if values == nil then nil
    reduce(keys, {}, (a, k, i) -> put(a, k, values[i]))

doc
~~~
`(list xs, s) -> list`

Returns a `list` with all elements from `xs` separated by `s`.

Returns `nil` if `xs` is `nil`.

```ruby
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
    if xs == nil then nil
    reduce(xs, [],          # build a new list
      (a, x, i) ->
        if i == 0
          [x]               # first item remains as is
          [...a, s, x]      # follow-up items are preceded with seperator
    )

doc
~~~
`(xs, init, function f) -> any`

Accumulates all `x` in `list` or `dict` `xs` to a single value using `f`, and starting with `init`.

If `f` accepts two arguments, the accumulated value and `x` are passed.
If `f` accepts three arguments, the accumulated value, `x`, and the index or key of `x` are passed.

`f` returns the new accumulated value.

Returns `nil` if `xs` is `nil` or `f` is `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby
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

> data.reduce([1, 2, 3], 0, nil)
nil

> data.reduce("foo", 0, (x) -> x)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce is not defined for type string
  at: [interactive]:14:10
  source: data.reduce("foo", 0, (x) -> x)
```
~~~

  function reduce: (xs, init, function f) -> any                via {:class "com.twineworks.tweakflow.std.Data$reduce"}

doc
~~~
`(xs, init, function p, function f) -> any`

Accumulates all `x` in `list` or `dict` `xs` to a single value using `f`, and starting with `init`.

If `f` accepts two arguments, the accumulated value and `x` are passed.
If `f` accepts three arguments, the accumulated value, `x`, and the index or key of `x` are passed.

`f` returns the new accumulated value.

Stops the process, if predicate function `p` returns a value that casts to `boolean` `true`
for the current accumulated value.

Returns `nil` if `xs`, `f`, or `p` are `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby

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

> data.reduce_until([1, 2, 3], 0, nil, (a, x) -> a)
nil

> data.reduce_until([1, 2, 3], 0, (a) -> true, nil)
nil

> data.reduce_until("foo", 0, (a) -> true, (a, x) -> a)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce_until is not defined for type string
  at: [interactive]:14:10
  source: data.reduce_until("foo", 0, (a) -> true, (a, x) -> a)
```
~~~

  function reduce_until: (xs, init, function p, function f) ->  via {:class "com.twineworks.tweakflow.std.Data$reduce_until"}

doc
~~~
`(xs, init, function p, function f) -> any`

Accumulates all `x` in `list` or `dict` `xs` to a single value using `f`, and starting with `init`.

If `f` accepts two arguments, the accumulated value and `x` are passed.
If `f` accepts three arguments, the accumulated value, `x`, and the index or key of `x` are passed.

`f` returns the new accumulated value.

Stops the process, if predicate function `p` returns a value that casts to `boolean` `false`
for the current accumulated value.

Returns `nil` if `xs`, `f`, or `p` are `nil`.

Throws an error if `xs` is neither a `list` nor a `dict`.

```ruby

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

> data.reduce_while([1, 2, 3], 0, nil, (a, x) -> a)
nil

> data.reduce_while([1, 2, 3], 0, (a) -> true, nil)
nil

> data.reduce_while("foo", 0, (a) -> true, (a, x) -> a)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: reduce_while is not defined for type string
  at: [interactive]:14:10
  source: data.reduce_while("foo", 0, (a) -> true, (a, x) -> a)
```
~~~

  function reduce_while: (xs, init, function p, function f) ->  via {:class "com.twineworks.tweakflow.std.Data$reduce_while"}

}


doc
~~~
The time library provides functions for processing datetime values.
~~~
export library time {

doc
~~~
The instant of time at `1970-01-01T00:00:00Z`
~~~
  datetime epoch: 1970-01-01T00:00:00Z

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full seconds between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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
  function seconds_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$secondsBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full minutes between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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

  function minutes_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$minutesBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full hours between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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
  function hours_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$hoursBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full days between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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

  function days_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$daysBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full months between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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

  function months_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$monthsBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> long`

Returns the number of full years between given datetimes.

Returns a negative number if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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
  function years_between: (datetime start_inclusive, datetime end_exclusive) -> long via {:class "com.twineworks.tweakflow.std.Time$yearsBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> dict`

Returns a dict with long values on keys `:years`, `:months`, and `:days`, representing a calendar period between two datetimes.

The values are negative if start datetime is after end datetime.

Returns `nil` if any argument is `nil`.

```ruby
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

  function period_between: (datetime start_inclusive, datetime end_exclusive) -> dict via {:class "com.twineworks.tweakflow.std.Time$periodBetween"}

doc
~~~
`(datetime start_inclusive, datetime end_exclusive) -> dict`

Returns a dict with long values on keys `:seconds`, and `:nano_seconds`, representing a duration between two datetimes.

`:seconds` are negative if start datetime is after end datetime.
`:nano_seconds` is never negative and within the range of `0 - 999,999,999`.
A duration of `-1` nanosecond is represented as `-1` `:seconds` and `999,999,999` `:nanoseconds`.

Returns `nil` if any argument is `nil`.

```ruby
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
  function duration_between: (datetime start_inclusive, datetime end_exclusive) -> dict via {:class "com.twineworks.tweakflow.std.Time$durationBetween"}

doc
~~~
`(datetime start, long years=0, long months=0, long days=0) -> datetime`

Adds a calendar period to the given start datetime and returns the result.

Supply negative period numbers to effectively subtract a period.

Returns `nil` if any argument is `nil`.

```ruby

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

  function add_period: (datetime start, long years=0, long months=0, long days=0) -> datetime via {:class "com.twineworks.tweakflow.std.Time$addPeriod"}

doc
~~~
`(datetime start, long seconds=0, long nano_of_second=0) -> datetime`

Adds a time duration to the given start datetime and returns the result.

Supply negative numbers to effectively subtract a duration.

Returns `nil` if any argument is `nil`.

```ruby

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

  function add_duration: (datetime start, long seconds=0, long nano_of_second=0) -> datetime via {:class "com.twineworks.tweakflow.std.Time$addDuration"}

doc
~~~
`(datetime x) -> long`

Returns the year component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.year(time.epoch)
1970

> time.year(2017-02-21T)
2017

> time.year(nil)
nil
```
~~~

  function year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$year"}

doc
~~~
`(datetime x) -> long`

Returns the month component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.month(time.epoch)
1

> time.month(2017-02-21T)
2

> time.month(nil)
nil
```
~~~

  function month: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$month"}

doc
~~~
`(datetime x) -> long`

Returns the day of month component of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.day_of_month(time.epoch)
1

> time.day_of_month(2017-02-21T)
21

> time.day_of_month(nil)
nil
```
~~~

  function day_of_month: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfMonth"}

doc
~~~
`(datetime x) -> long`

Returns the day of year of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.day_of_year(time.epoch)
1

> time.day_of_year(2017-02-21T)
52

> time.day_of_year(nil)
nil
```
~~~

  function day_of_year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfYear"}

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

```ruby
> time.day_of_week(time.epoch)
4

> time.day_of_week(2017-05-24T)
3

> time.day_of_week(nil)
nil
```
~~~
  function day_of_week: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfWeek"}

doc
~~~
`(datetime x) -> long`

Returns the hour of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.hour(time.epoch)
0

> time.hour(2017-02-21T23:00:00)
23

> time.hour(nil)
nil
```
~~~
  function hour: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$hour"}

doc
~~~
`(datetime x) -> long`

Returns the minute of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.minute(time.epoch)
0

> time.minute(2017-02-21T00:59:00)
59

> time.minute(nil)
nil
```
~~~

  function minute: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$minute"}

doc
~~~
`(datetime x) -> long`

Returns the second of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.second(time.epoch)
0

> time.second(2017-02-21T00:00:12)
12

> time.second(nil)
nil
```
~~~

  function second: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$second"}

doc
~~~
`(datetime x) -> long`

Returns the nanoseconds of given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
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

  function nano_of_second: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$nanoOfSecond"}

doc
~~~
`(datetime x) -> long`

Returns the week of year for given datetime `x`. The ISO-8601 definition is used, where a week starts on Monday and the first week has a minimum of 4 days.

Returns `nil` if `x` is `nil`.

```ruby
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

  function week_of_year: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$weekOfYear"}

doc
~~~
`(datetime x) -> long`

Returns the number of seconds in the offset from UTC for given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
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
  function offset_seconds: (datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$offsetSeconds"}

doc
~~~
`(datetime x) -> long`

Returns the time zone for given datetime `x`.

Returns `nil` if `x` is `nil`.

```ruby
> time.zone(time.epoch)
"UTC"

> time.zone(2010-01-01T00:00:00.00+02:00)
"UTC+02:00"

> time.zone(2010-01-01T00:00:00.00+01:00@`Europe/Berlin`)
"Europe/Berlin"

> time.zone(nil)
nil
```
~~~
  function zone: (datetime x) -> string via {:class "com.twineworks.tweakflow.std.Time$zone"}

doc
~~~
`(datetime x, long year) -> datetime`

Returns the datetime `x` with the year field set to `year`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given year.

```ruby
> time.with_year(time.epoch, 2007)
2007-01-01T00:00:00Z@UTC

> time.with_year(time.epoch, nil)
nil

> time.with_year(nil, 2017)
nil

> time.with_year(time.epoch, 1000000000)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for Year (valid values -999999999 - 999999999): 1000000000
  at: [interactive]:14:10
  source: time.with_year(time.epoch, 1000000000)
```
~~~

  function with_year: (datetime x, long year) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withYear"}

doc
~~~
`(datetime x, long month) -> datetime`

Returns the datetime `x` with the month field set to `month`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given month.

```ruby
> time.with_month(time.epoch, 6)
1970-06-01T00:00:00Z@UTC

> time.with_month(time.epoch, nil)
nil

> time.with_month(nil, 6)
nil

> time.with_month(time.epoch, 13)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for MonthOfYear (valid values 1 - 12): 13
  at: [interactive]:14:10
  source: time.with_month(time.epoch, 13)

```
~~~

  function with_month: (datetime x, long month) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withMonth"}


doc
~~~
`(datetime x, long day_of_month) -> datetime`

Returns the datetime `x` with the day_of_month field set to `day_of_month`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `day_of_month`.

```ruby
> time.with_day_of_month(time.epoch, 23)
1970-01-23T00:00:00Z@UTC

> time.with_day_of_month(time.epoch, nil)
nil

> time.with_day_of_month(nil, 23)
nil

> time.with_day_of_month(time.epoch, 33)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for DayOfMonth (valid values 1 - 28/31): 33
  at: [interactive]:14:10
  source: time.with_day_of_month(time.epoch, 33)
```
~~~

  function with_day_of_month: (datetime x, long day_of_month) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withDayOfMonth"}

doc
~~~
`(datetime x, long hour) -> datetime`

Returns the datetime `x` with the hour field set to `hour`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `hour`.

```ruby
> time.with_hour(time.epoch, 4)
1970-01-01T04:00:00Z@UTC

> time.with_hour(time.epoch, nil)
nil

> time.with_hour(nil, 4)
nil

> time.with_hour(time.epoch, 25)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Invalid value for HourOfDay (valid values 0 - 23): 25
  at: [interactive]:14:10
  source: time.with_hour(time.epoch, 25)
```
~~~

  function with_hour: (datetime x, long hour) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withHour"}

doc
~~~
`(datetime x, long minute) -> datetime`

Returns the datetime `x` with the minute field set to `minute`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `minute`.

```ruby
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
  at: [interactive]:14:10
  source: time.with_minute(time.epoch, 78)
```
~~~

  function with_minute: (datetime x, long hour) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withMinute"}

doc
~~~
`(datetime x, long second) -> datetime`

Returns the datetime `x` with the second field set to `second`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `second`.

```ruby
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
  at: [interactive]:14:10
  source: time.with_second(time.epoch, 78)
```
~~~

  function with_second: (datetime x, long second) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withSecond"}

doc
~~~
`(datetime x, long nano_of_second) -> datetime`

Returns the datetime `x` with the nano_of_second field set to `nano_of_second`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `nano_of_second`.

```ruby
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
  at: [interactive]:14:10
  source: time.with_nano_of_second(time.epoch, 1000000000)
```
~~~

  function with_nano_of_second: (datetime x, long nano_of_second) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withNanoOfSecond"}

doc
~~~
`(datetime x, string tz) -> datetime`

Returns the datetime `x` with the time zone field set to `tz`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `tz`.

```ruby
> time.with_zone(time.epoch, "Europe/Berlin")
1970-01-01T00:00:00+01:00@`Europe/Berlin`

> time.with_zone(time.epoch, "GMT+08:00")
1970-01-01T00:00:00+08:00@`GMT+08:00`

> time.with_zone(time.epoch, nil)
nil

> time.with_zone(nil, "Europe/Berlin")
nil

> time.with_zone(time.epoch, "---")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: unknown time zone id: ---
  at: [interactive]:14:10
  source: time.with_zone(time.epoch, "---")
```
~~~

  function with_zone: (datetime x, string tz) -> datetime via {:class "com.twineworks.tweakflow.std.Time$withTz"}


doc
~~~
`(datetime x, string tz) -> datetime`

Returns a datetime representing the same instant as `x` in time zone `tz`.

Returns `nil` if any argument is `nil`.

Throws an error if no datetime can be constructed with given `tz`.

```ruby
> time.same_instant_at_zone(time.epoch, "Europe/Berlin")
1970-01-01T01:00:00+01:00@`Europe/Berlin`

> time.same_instant_at_zone(time.epoch, "GMT+08:00")
1970-01-01T08:00:00+08:00@`GMT+08:00`

# the instant in time is the same
> time.compare(1970-01-01T01:00:00+01:00@`Europe/Berlin`, 1970-01-01T08:00:00+08:00@`GMT+08:00`)
0

> time.same_instant_at_zone(time.epoch, nil)
nil

> time.same_instant_at_zone(nil, "Europe/Berlin")
nil

> time.same_instant_at_zone(time.epoch, "---")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: unknown time zone id: ---
  at: [interactive]:14:10
  source: time.same_instant_at_zone(time.epoch, "---")
```
~~~

  function same_instant_at_zone: (datetime x, string tz) -> datetime via {:class "com.twineworks.tweakflow.std.Time$sameInstantAtZone"}


doc
~~~
`(long s) -> datetime`

Returns a datetime representing epoch + `s` seconds in time zone `UTC`.

Returns `nil` if `s` is `nil`.

```ruby

> time.unix_timestamp(0)
1970-01-01T00:00:00Z@UTC

> time.unix_timestamp(1496933995)
2017-06-08T14:59:55Z@UTC

> time.unix_timestamp(-1496933995)
1922-07-26T09:00:05Z@UTC
```
~~~

  function unix_timestamp: (long s) ->
    add_duration(epoch, s)


doc
~~~
`(long ms) -> datetime`

Returns a datetime representing epoch + `ms` milliseconds in time zone `UTC`.

Returns `nil` if `s` is `nil`.

```ruby

> time.unix_timestamp_ms(0)
1970-01-01T00:00:00Z@UTC

> time.unix_timestamp_ms(1496933995763)
2017-06-08T14:59:55.763Z@UTC

> time.unix_timestamp_ms(-1496933995763)
1922-07-26T09:00:04.237Z@UTC
```
~~~

  function unix_timestamp_ms: (long ms) ->
    add_duration(epoch, ms // 1000, (ms % 1000) * 1000000)

doc
~~~
`(datetime a, datetime b) -> long`

Compares two datetimes `a` and `b`.

Returns -1 if `a` precedes `b`.\
Returns 1 if `b` precedes `a`.\
Returns 0 if `a` and `b` describe the same instant in time.\
Assumes `nil` to precede any non-nil datetime.

```ruby
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
      d: duration_between(a, b)
    }
    if d[:seconds] < 0 then 1
    if d[:seconds] > 0 || (d[:seconds] == 0 && d[:nano_seconds] > 0) then -1
    else 0


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

`f` returns `nil` if passed `nil` as an argument.

Throws an error if any argument is `nil`.\
Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) for the DateTimeFormatter of the Java language.

```ruby
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

  function formatter: (string pattern="uuuu-MM-dd'T'HH:mm:ssZZZZZ'@`'VV'`'", string lang="en-US") -> function via {:class "com.twineworks.tweakflow.std.Time$formatter"}


doc
~~~
```
(
  string pattern="uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']",
  boolean lenient=false,
  string lang="en-US",
  string default_tz="UTC"
) -> function
```

Returns a function `f` that accepts a single string paramter `x` and returns a parsed datetime value as specified by the supplied
[pattern](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns) and language tag.

If `lenient` is false, `f` throws an error on strings that parse to invalid dates.\
If `lenient` is true, the parser will attempt to correct invalid datetime values. A date of January 32nd will parse as February 1st, for example.

The default time zone is used unless the pattern parses both a time component and a time zone in which case the parsed values are used.

```ruby
> f: time.parser("uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']")
function

> f(nil)
nil

> f("2017-04-23T21:43:11")
2017-04-23T21:43:11Z@UTC

> f("2017-04-23T21:43:11@`Europe/Berlin`")
2017-04-23T21:43:11+02:00@`Europe/Berlin`

# patterns specifies mandatory @ sign and backticks before timezone
> f("2017-04-23T21:43:11 Europe/Berlin")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Text '2017-04-23T21:43:11 Europe/Berlin' could not be parsed, unparsed text found at index 19
  at: [interactive]:14:10
  source: f("2017-04-23T21:43:11 Europe/Berlin")

# strict parser
> f: time.parser('uuuu-MM-dd')
function

# there is no march 32nd
> f("2015-03-32")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Text '2015-03-32' could not be parsed: Invalid value for DayOfMonth (valid values 1 - 28/31): 32
  at: [interactive]:14:10
  source: f("2015-03-32")

# lenient parser
> f: time.parser('uuuu-MM-dd', true)
function

# lenient parser interprets march 32nd as april 1st
> f("2015-03-32")
2015-04-01T00:00:00Z@UTC

# parses local times setting the default time zone
> f: time.parser('uuuu-MM-dd[ HH:mm:ss]', default_tz: 'America/Chicago')
function

> f("2017-06-22 12:34:11")
2017-06-22T12:34:11-05:00@`America/Chicago`

> f("2017-06-22")
2017-06-22T00:00:00-05:00@`America/Chicago`

```
~~~

  function parser: (string pattern="uuuu-MM-dd'T'HH:mm:ss[ZZZZZ]['@`'VV'`']", boolean lenient=false, string lang="en-US", string default_tz="UTC") -> function via {:class "com.twineworks.tweakflow.std.Time$parser"}

doc
~~~
`() -> list`

Returns a list of all known time zone ids.

```ruby
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

  function zones: () -> list via {:class "com.twineworks.tweakflow.std.Time$zones"}
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

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not numeric and not `nil`.

```ruby
> math.abs(100)
100

> math.abs(-100)
100

> math.abs(-2.0)
2.0

> math.abs(-Infinity)
Infinity

> math.abs(NaN)
NaN

> math.abs(math.min_long)
ERROR:
  code: NUMBER_OUT_OF_BOUNDS
  message: cannot represent magnitude as long
  at: [interactive]:14:10
  source: math.abs(math.min_long)

> math.abs(math.min_long as double)
9.223372036854776E18

> math.abs(nil)
nil

> math.abs("hello")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot determine magnitude of type string
  at: [interactive]:14:10
  source: math.abs("hello")
```
~~~

  function abs: (x) ->                                      via {:class "com.twineworks.tweakflow.std.Math$abs"}

doc
~~~
`(any seed) -> double `

Returns a pseudo-random double between `0.0` inclusive and `1.0` exclusive, based on given `seed`.
This function is pure and deterministically returns the same number for the same `seed`.

To generate a sequence of pseudo-random numbers, you can use a previously generated number as the next `seed`.

```ruby
> dice_roll: (any seed) -> (math.rand(seed) *6 +1) as long
function

> dice_roll("hello")
3

> dice_roll("hi")
6

> \e
  dice_rolls: (long count, any seed) ->
    fun.iterate(1, count,
      {:seed [seed], :nums []},
      (d, i) -> let {
          r: math.rand(d[:seed])
          n: (r*6+1) as long
        }
        {
          :seed data.append(d[:seed], r*i)
          :nums data.append(d[:nums], n)
        }
    )[:nums]
\e
function

> dice_rolls(3, "foo")
[5, 1, 5]

> dice_rolls(20, "foo")
[5, 1, 5, 3, 3, 5, 6, 6, 5, 3, 3, 6, 1, 2, 1, 5, 4, 5, 3, 6]

> dice_rolls(3, "bar")
[6, 1, 1]

> dice_rolls(20, "bar")
[6, 1, 1, 3, 6, 4, 3, 5, 6, 5, 3, 5, 1, 1, 1, 5, 4, 2, 3, 1]

> math.rand(nil)
0.730967787376657

```
~~~
  function rand: (any seed) -> double                           via {:class "com.twineworks.tweakflow.std.Math$rand"}

doc
~~~
`(any x) -> any`

Increments `x` by one.

If `x` is a double, returns x+1.0. \
If `x` is a long, returns x+1. \

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not a numeric type.

```ruby
> math.inc(2.0)
3.0

> math.inc(-2.0)
-1.0

> math.inc(1)
2

> math.inc(math.max_long)
-9223372036854775808 # overflow to math.min_long

> math.inc(nil)
nil

> math.inc([])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot increment type list
  at: [interactive]:14:10
  source: math.inc([])
```
~~~

  function inc: (any x) -> any                              via {:class "com.twineworks.tweakflow.std.Math$inc"}

doc
~~~
`(any x) -> any`

Decrements `x` by one.

If `x` is a double, returns x-1.0. \
If `x` is a long, returns x-1. \

If `x` is `nil`, returns `nil`.

Throws an error if `x` is not a numeric type.

```ruby
> math.dec(2.0)
1.0

> math.dec(-2.0)
-3.0

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
  at: [interactive]:14:10
  source: math.dec([])
```
~~~

  function dec: (x) -> via {:class "com.twineworks.tweakflow.std.Math$dec"}

doc
~~~
`(a, b) -> long`

Compares long or double numbers a and b according to their numeric order.

Returns -1 if a < b.\
Returns 1 if a > b.\
Returns 0 if a == b.

The order reflected by this function sorts these values in order: `nil`, `NaN`, `-Infinity`, finite numeric values, and `Infinity`.

Throws an error if `a` or `b` are not `nil`, nor of type `long` or `double`.

```ruby
> math.compare(1, 1)
0

> math.compare(nil, 7)
-1

> math.compare(7.0, 2)
1

> data.sort([4, 3, 2.5, 1, 0.2, nil, NaN, -Infinity, Infinity], math.compare)
[nil, NaN, -Infinity, 0.2, 1, 2.5, 3, 4, Infinity]
```
~~~
  function compare: (a, b) -> long

    if !(a is long || a is double || a == nil) throw "cannot compare non-numeric: "..core.inspect(a)
    if !(b is long || b is double || b == nil) throw "cannot compare non-numeric: "..core.inspect(b)

    # handle nil case
    if a == nil
      if b == nil then 0 else -1

    if b == nil then 1

    # handle nan case
    if nan?(a)
      if nan?(b) then 0 else -1

    if nan?(b) then 1

    # handle regular numbers case
    if a < b then -1
    if a > b then 1
    else 0

doc
~~~
`(list xs) -> any`

Given a list of numeric `xs`, returns the smallest `x`.

Returns `nil` if `xs` is `nil`, `xs` is empty, or any `x` in `xs` is `nil`.

Throws an error if any `x` is non-numeric.

```ruby
> math.min([1,2,3])
1

> math.min([1.0, 2.0, -3.0])
-3.0

> math.min([1, nil])
nil

> math.min(nil)
nil

> math.min(["foo"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot compare type string
  at: [interactive]:14:10
  source: math.min(["foo"])
```
~~~

  function min: (list xs) -> any                            via {:class "com.twineworks.tweakflow.std.Math$min"}

doc
~~~
`(list xs) -> any`

Given a list of numeric `xs`, returns the largest `x`.

Returns `nil` if `xs` is `nil`, `xs` is empty, or any `x` in `xs` is `nil`.

Throws an error if any `x` is non-numeric.

```ruby
> math.max([1,2,3])
3

> math.max([1.0, 2.0, -3.0])
2.0

> math.max([1, nil])
nil

> math.max(nil)
nil

> math.max(["foo"])
ERROR:
  code: ILLEGAL_ARGUMENT
  message: cannot compare type string
  at: [interactive]:14:10
  source: math.max(["foo"])
```
~~~

  function max: (list xs) -> any                            via {:class "com.twineworks.tweakflow.std.Math$max"}

doc
~~~
`(double x) -> double`

Given a double `x`, returns `x` rounded to the closest integral value.

Returns `nil` if `x` is `nil`.

```ruby
> math.round(2.3)
2.0

> math.round(-2.3)
-2.0

> math.round(nil)
nil
```
~~~

  function round: (double x) -> double                      via {:class "com.twineworks.tweakflow.std.Math$round"}

doc
~~~
`(double x) -> double`

Given a double `x`, returns `x` to the next larger integral value.

Returns `nil` if `x` is `nil`.

```ruby
> math.ceil(2.3)
3.0

> math.ceil(-2.3)
-2.0

> math.ceil(nil)
nil
```
~~~

  function ceil:  (double x) -> double                      via {:class "com.twineworks.tweakflow.std.Math$ceil"}

doc
~~~
`(double x) -> double`

Given a double `x`, returns `x` to the next smaller integral value.

Returns `nil` if `x` is `nil`.

```ruby
> math.floor(2.3)
2.0

> math.floor(-2.3)
-3.0

> math.floor(nil)
nil
```
~~~

  function floor: (double x) -> double                      via {:class "com.twineworks.tweakflow.std.Math$floor"}

doc
~~~
`(double x) -> boolean`

Given a double `x`, returns `true` if `x` is NaN, returns `false` otherwise.

```ruby
> math.nan?(2.3)
false

> math.nan?(NaN)
true

> math.nan?(nil)
false
```
~~~

  function nan?:  (double x) -> boolean                     via {:class "com.twineworks.tweakflow.std.Math$nan"}

doc
~~~
`(double x) -> double`

Given a double `x`, returns the square root of `x`.

Returns `NaN` if `x` is negative.

Returns `nil` if `x` is `nil`.

```ruby
> math.sqrt(9.0)
3.0

> math.sqrt(-9.0)
NaN

> math.sqrt(nil)
nil
```
~~~

  function sqrt:  (double x) -> double x ** 0.5

doc
~~~
`(double x) -> double`

Returns the trigonometric sine of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```ruby
> math.sin(0)
0.0

> math.sin(math.pi/2)
1.0

> math.sin(1)
0.8414709848078965
```
~~~

  function sin: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$sin"}

doc
~~~
`(double x) -> double`

Returns the trigonometric cosine of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```ruby
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

  function cos: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$cos"}


doc
~~~
`(double x) -> double`

Returns the trigonometric tangent of angle `x` given in radians. \
Returns `NaN` if `x` is an `Infinity` or `NaN`.\
Returns `nil` if `x` is `nil`.

```ruby
> math.tan(0)
0.0

> math.tan(1)
1.5574077246549023

> math.tan(math.pi/4)
0.9999999999999999
```
~~~

  function tan: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$tan"}

doc
~~~
`(double x) -> double`

Returns the arc sine of `x` in the range of -pi/2 throuh pi/2. \
Returns `NaN` if `x` is `NaN` or its absolute value is greater than 1.\
Returns `nil` if `x` is `nil`.

```ruby
> math.asin(0)
0.0

> math.asin(0.5)
0.5235987755982989

> math.asin(1)
1.5707963267948966
```
~~~

  function asin: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$asin"}

doc
~~~
`(double x) -> double`

Returns the arc cosine of `x` in the range of 0.0 throuh pi. \
Returns `NaN` if `x` is `NaN` or its absolute value is greater than 1.\
Returns `nil` if `x` is `nil`.

```ruby
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

  function acos: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$acos"}

doc
~~~
`(double x) -> double`

Returns the arc tangent of `x` in the range of -pi/2 through pi/2. \
Returns `NaN` if `x` is `NaN`.\
Returns `nil` if `x` is `nil`.

```ruby
> math.atan(0)
0.0

> math.atan(-1)
-0.7853981633974483

> math.atan(1)
0.7853981633974483
```
~~~

  function atan: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$atan"}


doc
~~~
`(double x) -> double`

Returns the natural logarithm (base e) of `x`.

Returns `NaN` if `x` is `NaN` or less than `0`.\
Returns `-Infinity` if `x` is `0` \
Returns `Infinity` if `x` is `Infinity` \
Returns `nil` if `x` is `nil`.

```ruby
> math.log(0)
-Infinity

> math.log(1)
0.0

> math.log(math.e**2)
2.0
```
~~~

  function log: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$log"}


doc
~~~
`(double x) -> double`

Returns the base 10 logarithm of `x`.

Returns `NaN` if `x` is `NaN` or less than `0`.\
Returns `-Infinity` if `x` is `0` \
Returns `Infinity` if `x` is `Infinity` \
Returns `nil` if `x` is `nil`.

```ruby
> math.log10(0)
-Infinity

> math.log10(1)
0.0

> math.log10(100)
2.0
```
~~~

  function log10: (double x) -> double via {:class "com.twineworks.tweakflow.std.Math$log10"}


doc
~~~
`(long x) -> long`

Given a long `x`, returns the number of bits set to 1 in the binary two's complement representation of `x`.

Returns `nil` if `x` is `nil`.

```ruby
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
  function bit_count: (long x) -> long                      via {:class "com.twineworks.tweakflow.std.Math$bitCount"}


doc
~~~
```
(
  string pattern='0.##',
  dict decimal_symbols=nil,
  string rounding_mode="half_up",
  boolean always_show_decimal_separator=false
) -> function
```

Returns a function `f` that accepts a single `long` or `double` parameter `x`, and returns a string representation of `x` using the
supplied [pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html),
[decimal_symbols](#decimal-symbols),
[rounding mode](https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html), and
[always_show_decimal_separator](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html#setDecimalSeparatorAlwaysShown-boolean-) flag.

If `decimal_symbols` is `nil` (the default), 'en-US' decimal symbols are used.

`f` returns `nil` if passed `nil` as an argument.
`f` throws an error if `x` is neither a `double`, nor a `long` value.

Throws an error if `rounding_mode` is `nil`.\
Throws an error if `pattern` is not a valid [pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html) for the DecimalFormat of the Java language.

```ruby
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
  function formatter: (string pattern='0.##', dict decimal_symbols=nil, string rounding_mode="half_up", boolean always_show_decimal_separator=false) -> function via {:class "com.twineworks.tweakflow.std.Math$formatter"}


doc
~~~
```
(
  string pattern='0.##',
  dict decimal_symbols=nil,
  boolean lenient=false
) -> function
```

Returns a function `f` that accepts a single string paramter `x` and returns a parsed numeric value as specified by the supplied
[pattern](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html) and `decimal_symbols`.

Uses `en-US` decimal symbols if `decimal_symbols` is `nil`.

If `lenient` is `false` then `x` must parse as a number in its entirety. Partial matches throw an error.\
If `lenient` is `true` then partial matches of `x` return the number that results from parsing the partial match.

`f` returns `nil` if `x` is `nil`.

Throws an error if `pattern` or `decimal_symbols` is invalid.


```ruby
> f: math.parser()

> f("203.23")
203.23

> f("203.23kg")
ERROR:
  code: ILLEGAL_ARGUMENT
  message: Partial match not allowed. Parsing ended at index: 6
  at: [interactive]:14:10
  source: f("203.23kg")

# lenient parser
> f: math.parser('0.##', nil, true)
function

> f("203.23kg")
203.23

> f: math.parser('#,##0.##', locale.decimal_symbols('hi-IN'))
function

> f("१०३")
103
```
~~~

  function parser: (string pattern='0.##', dict decimal_symbols=nil, boolean lenient=false) -> function via {:class "com.twineworks.tweakflow.std.Math$parser"}

doc
~~~
The double value that is closer than any other to `e`, the base of the natural logarithms.
~~~
  double e:   2.718281828459045

doc
~~~
The double value that is closer than any other to `pi`, the ratio of the circumference of a circle to its diameter.
~~~

  double pi:  3.141592653589793

doc
~~~
The smallest representable long value: `-9223372036854775808`.
~~~

  long min_long: 0x8000000000000000

doc
~~~
The largest representable long value: `9223372036854775807`.
~~~

  long max_long: 0x7FFFFFFFFFFFFFFF
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

Returns `nil` if `n` is negative or `nil`.
Returns `nil` if `f` is `nil`.

```ruby
> double_up: (x) -> 2*x
function

> fun.times(10, 1, double_up)
1024
```

Example: computing [Fibonacci numbers](https://en.wikipedia.org/wiki/Fibonacci_number)

```ruby
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

  function times: (long n, any x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$times"}

doc
~~~
`(function p, any x, function f) -> any`

Calls `f` repeatedly with one argument. The first call of `f` receives `x` as argument.
Subsequent calls to `f` receive as argument the result of the previous call.

Before each call to `f`, the argument is tested by predicate function `p`.
If `p` returns `true`, execution completes and the tested argument is returned.

Returns the first argument for which `p` evaluates to `true`.

Returns `nil` if `p` is `nil`.
Returns `nil` if `f` is `nil`.


```ruby
# keep doubling up 1 until the result exceeds 60,000
> fun.until((x) -> x>60000, 1, (x) -> x*2)
65536

# Generate natural numbers, until their sum exceeds 100.
# The data structure is a dict containing the list of
# numbers at key :nums and their sum at key :sum.

> \e
  next: (x) ->
    let {
      n: data.size(x[:nums])+1
    }
    {
      :nums data.append(x[:nums], n),
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
  function until: (function p, any x, function f) ->            via {:class "com.twineworks.tweakflow.std.Fun$until"}

doc
~~~
`(function p, any x, function f) -> any`

Calls `f` repeatedly with one argument. The first call of `f` receives `x` as argument.
Subsequent calls to `f` receive as argument the result of the previous call.

Before each call to `f`, the argument is tested by predicate function `p`.
If `p` returns `false`, execution completes and the tested argument is returned.

Returns the first argument for which `p` evaluates to `false`.

Returns `nil` if `p` is `nil`.\
Returns `nil` if `f` is `nil`.


```ruby
# keep doubling up 1 while the result is smaller than 60,000
> fun.while((x) -> x<60000, 1, (x) -> x*2)
65536

# Generate natural numbers, while their sum is smaller than 100.
# The data structure is a dict containing the list of
# numbers at key :nums and their sum at key :sum.

> \e
  next: (x) ->
    let {
      n: data.size(x[:nums])+1
    }
    {
      :nums data.append(x[:nums], n),
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

  function while: (function p, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$doWhile"}

doc
~~~
`(long start, long end, any x, function f) -> any`

Calls `f` repeatedly with two arguments. The first call of `f` receives `x` as first argument.
Subsequent calls to `f` receive as first argument the result of the previous call.

The second argument is an index starting at `start` and ending inclusively at `end`.

Returns the result of the last call to `f`.

Returns `nil` if `start`, `end`, or `f` are `nil`.

Returns `x` if `end` < `start`.

```ruby
# add natural numbers 1 to 10
> fun.iterate(1, 10, 0, (sum, i) -> sum+i)
55

# make a triangle string
> fun.iterate(1, 8, "\n", (str, i) -> str .. strings.join(data.repeat(i, "*")) .. "\n")
"
*
**
***
****
*****
******
*******
********
"
```
~~~

  function iterate: (long start, long end, x, function f) ->      via {:class "com.twineworks.tweakflow.std.Fun$iterate"}

doc
~~~
`(any state, list fs) -> any`

Calls functions from given `fs` in sequence, passing `state` as the argument to the first function,
and the result of the previous call to subsequent calls. Returns result of final call.

Returns `state` if `fs` is empty.
Returns `nil` if `fs` is nil.

```ruby
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
    data.reduce(fs, state, (a, f) -> f(a))

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

Returns `nil` if `fs` is `nil`.
Throws an error if `fs` is empty.

```ruby
> f: fun.chain([(x) -> x+10, (x) -> x*2]) # add 10, then double result
function

> f(1)  # (1+10)*2
22
```
~~~

  function chain: (list fs) -> function
    if fs == nil then nil
    if data.size(fs) == 0 then throw "function list cannot be empty"
    data.reduce(fs, nil, (a, f) -> if a == nil then f else (x) -> f(a(x)))

doc
~~~
`(list fs) -> function`

Given a list of functions `fs`, returns a composite function that calls all functions in `fs` in reverse order, consistent
with conventional mathematical notation.

`fun.compose([f, g])(x) == f(g(x))`

Returns `nil` if `fs` is `nil`.
Throws an error if `fs` is empty.

```ruby
> f: fun.compose([(x) -> x+10, (x) -> x*2]) # double result, then add 10
function

> f(1)  # (1*2)+10
12
```
~~~

  function compose: (list fs) -> function
    if fs == nil then nil
    if data.size(fs) == 0 then throw "function list cannot be empty"
    data.reduce(data.reverse(fs), nil, (a, f) -> if a == nil then f else (x) -> f(a(x)))

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

```ruby
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

  function signature: (function f) -> dict via {:class "com.twineworks.tweakflow.std.Fun$signature"}

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

```ruby
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
  function languages: (lang='en-US') -> dict via {:class "com.twineworks.tweakflow.std.Locale$languages"}

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
| nan                 | The string representing NaN (Not a Number) values.     |

Returns `en-US` decimal symbols if `lang` is `nil`.

```ruby
> locale.decimal_symbols()
{
  :infinity "∞",
  :grouping_separator ",",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "0",
  :decimal_separator ".",
  :nan "�"
}

> locale.decimal_symbols('fr')
{
  :infinity "∞",
  :grouping_separator " ",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "0",
  :decimal_separator ",",
  :nan "�"
}

> locale.decimal_symbols('hi-IN')
{
  :infinity "∞",
  :grouping_separator ",",
  :minus_sign "-",
  :exponent_separator "E",
  :zero_digit "०",
  :decimal_separator ".",
  :nan "�"
}

```
~~~

  function decimal_symbols: (lang='en-US') -> dict via {:class "com.twineworks.tweakflow.std.Locale$decimalSymbols"}

}