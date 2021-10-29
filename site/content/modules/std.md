---
title: std.tf
---

# module std.tf{#std}

The `std` module contains libraries for general computing tasks.



<div
      data-meta='true'
      data-meta-id='std'
      data-meta-type='module'
      data-meta-name='std.tf'
	    data-meta-tags=''
    ></div>

## library core{#core}

The core library contains utility functions to process values at a basic level.



<div
      data-meta='true'
      data-meta-id='core'
      data-meta-type='library'
      data-meta-name='core'
	    data-meta-tags=''
    ></div>

### id{#core-id}

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



<div
      data-meta='true'
      data-meta-id='core-id'
      data-meta-type='var'
      data-meta-name='id'
	    data-meta-tags='core'
    ></div>

### inspect{#core-inspect}

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




<div
      data-meta='true'
      data-meta-id='core-inspect'
      data-meta-type='var'
      data-meta-name='inspect'
	    data-meta-tags='core'
    ></div>

### present?{#core-present?}

`(x) -> boolean`

Returns `false` if `x` is `nil`, returns `true` otherwise.

```tweakflow
> core.present?("foo")
true

> core.present?(nil)
false
```



<div
      data-meta='true'
      data-meta-id='core-present?'
      data-meta-type='var'
      data-meta-name='present?'
	    data-meta-tags='core'
    ></div>

### nil?{#core-nil?}

`(x) -> boolean`

Returns `true` if `x` is `nil`, returns `false` otherwise.

```tweakflow
> core.nil?("foo")
false

> core.nil?(nil)
true
```



<div
      data-meta='true'
      data-meta-id='core-nil?'
      data-meta-type='var'
      data-meta-name='nil?'
	    data-meta-tags='core'
    ></div>

### hash{#core-hash}

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



<div
      data-meta='true'
      data-meta-id='core-hash'
      data-meta-type='var'
      data-meta-name='hash'
	    data-meta-tags='core'
    ></div>

### eval{#core-eval}

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



<div
      data-meta='true'
      data-meta-id='core-eval'
      data-meta-type='var'
      data-meta-name='eval'
	    data-meta-tags='core'
    ></div>

## library strings{#strings}

The strings library contains basic functions for text processing.



<div
      data-meta='true'
      data-meta-id='strings'
      data-meta-type='library'
      data-meta-name='strings'
	    data-meta-tags=''
    ></div>

### concat{#strings-concat}

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



<div
      data-meta='true'
      data-meta-id='strings-concat'
      data-meta-type='var'
      data-meta-name='concat'
	    data-meta-tags='strings'
    ></div>

### length{#strings-length}

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



<div
      data-meta='true'
      data-meta-id='strings-length'
      data-meta-type='var'
      data-meta-name='length'
	    data-meta-tags='strings'
    ></div>

### substring{#strings-substring}

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



<div
      data-meta='true'
      data-meta-id='strings-substring'
      data-meta-type='var'
      data-meta-name='substring'
	    data-meta-tags='strings'
    ></div>

### replace{#strings-replace}

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



<div
      data-meta='true'
      data-meta-id='strings-replace'
      data-meta-type='var'
      data-meta-name='replace'
	    data-meta-tags='strings'
    ></div>

### join{#strings-join}

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



<div
      data-meta='true'
      data-meta-id='strings-join'
      data-meta-type='var'
      data-meta-name='join'
	    data-meta-tags='strings'
    ></div>

### trim{#strings-trim}

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



<div
      data-meta='true'
      data-meta-id='strings-trim'
      data-meta-type='var'
      data-meta-name='trim'
	    data-meta-tags='strings'
    ></div>

### lower_case{#strings-lower_case}

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



<div
      data-meta='true'
      data-meta-id='strings-lower_case'
      data-meta-type='var'
      data-meta-name='lower_case'
	    data-meta-tags='strings'
    ></div>

### upper_case{#strings-upper_case}

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



<div
      data-meta='true'
      data-meta-id='strings-upper_case'
      data-meta-type='var'
      data-meta-name='upper_case'
	    data-meta-tags='strings'
    ></div>

### compare{#strings-compare}

`(string a, string b) -> long`

Compares two strings lexicographically. The comparison is based on the Unicode value of each character in the strings.

Returns -1 if a < b.\
Returns 1 if a > b.\
Returns 0 if a == b.\

This function considers `nil` less than any non-nil string.

```tweakflow
> data.sort(["Aaron", "Eve", "Adam", "Joe", "Beth", "Linda"], strings.compare)
["Aaron", "Adam", "Beth", "Eve", "Joe", "Linda"]
```



<div
      data-meta='true'
      data-meta-id='strings-compare'
      data-meta-type='var'
      data-meta-name='compare'
	    data-meta-tags='strings'
    ></div>

### comparator{#strings-comparator}

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



<div
      data-meta='true'
      data-meta-id='strings-comparator'
      data-meta-type='var'
      data-meta-name='comparator'
	    data-meta-tags='strings'
    ></div>

### chars{#strings-chars}

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



<div
      data-meta='true'
      data-meta-id='strings-chars'
      data-meta-type='var'
      data-meta-name='chars'
	    data-meta-tags='strings'
    ></div>

### code_points{#strings-code_points}

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



<div
      data-meta='true'
      data-meta-id='strings-code_points'
      data-meta-type='var'
      data-meta-name='code_points'
	    data-meta-tags='strings'
    ></div>

### of_code_points{#strings-of_code_points}

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



<div
      data-meta='true'
      data-meta-id='strings-of_code_points'
      data-meta-type='var'
      data-meta-name='of_code_points'
	    data-meta-tags='strings'
    ></div>

### split{#strings-split}

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



<div
      data-meta='true'
      data-meta-id='strings-split'
      data-meta-type='var'
      data-meta-name='split'
	    data-meta-tags='strings'
    ></div>

### starts_with?{#strings-starts_with?}

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



<div
      data-meta='true'
      data-meta-id='strings-starts_with?'
      data-meta-type='var'
      data-meta-name='starts_with?'
	    data-meta-tags='strings'
    ></div>

### ends_with?{#strings-ends_with?}

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



<div
      data-meta='true'
      data-meta-id='strings-ends_with?'
      data-meta-type='var'
      data-meta-name='ends_with?'
	    data-meta-tags='strings'
    ></div>

### index_of{#strings-index_of}

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



<div
      data-meta='true'
      data-meta-id='strings-index_of'
      data-meta-type='var'
      data-meta-name='index_of'
	    data-meta-tags='strings'
    ></div>

### last_index_of{#strings-last_index_of}

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



<div
      data-meta='true'
      data-meta-id='strings-last_index_of'
      data-meta-type='var'
      data-meta-name='last_index_of'
	    data-meta-tags='strings'
    ></div>

### char_at{#strings-char_at}

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



<div
      data-meta='true'
      data-meta-id='strings-char_at'
      data-meta-type='var'
      data-meta-name='char_at'
	    data-meta-tags='strings'
    ></div>

### code_point_at{#strings-code_point_at}

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



<div
      data-meta='true'
      data-meta-id='strings-code_point_at'
      data-meta-type='var'
      data-meta-name='code_point_at'
	    data-meta-tags='strings'
    ></div>

### to_bytes{#strings-to_bytes}

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



<div
      data-meta='true'
      data-meta-id='strings-to_bytes'
      data-meta-type='var'
      data-meta-name='to_bytes'
	    data-meta-tags='strings'
    ></div>

### from_bytes{#strings-from_bytes}

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



<div
      data-meta='true'
      data-meta-id='strings-from_bytes'
      data-meta-type='var'
      data-meta-name='from_bytes'
	    data-meta-tags='strings'
    ></div>

### charsets{#strings-charsets}

`() -> list`

Returns known charsets. Each item in the returned list is a charset name suitable for passing to
functions requiring a charset name, such as [to_bytes](#strings-to_bytes) and [from_bytes](#strings-from_bytes).



<div
      data-meta='true'
      data-meta-id='strings-charsets'
      data-meta-type='var'
      data-meta-name='charsets'
	    data-meta-tags='strings'
    ></div>

## library regex{#regex}

The regex library provides functions to work with regular expressions.
Pattern syntax is that of the [Java regular expression language](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html).



<div
      data-meta='true'
      data-meta-id='regex'
      data-meta-type='library'
      data-meta-name='regex'
	    data-meta-tags=''
    ></div>

### matching{#regex-matching}

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



<div
      data-meta='true'
      data-meta-id='regex-matching'
      data-meta-type='var'
      data-meta-name='matching'
	    data-meta-tags='regex'
    ></div>

### capturing{#regex-capturing}

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



<div
      data-meta='true'
      data-meta-id='regex-capturing'
      data-meta-type='var'
      data-meta-name='capturing'
	    data-meta-tags='regex'
    ></div>

### scanning{#regex-scanning}

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



<div
      data-meta='true'
      data-meta-id='regex-scanning'
      data-meta-type='var'
      data-meta-name='scanning'
	    data-meta-tags='regex'
    ></div>

### splitting{#regex-splitting}

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



<div
      data-meta='true'
      data-meta-id='regex-splitting'
      data-meta-type='var'
      data-meta-name='splitting'
	    data-meta-tags='regex'
    ></div>

### replacing{#regex-replacing}

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



<div
      data-meta='true'
      data-meta-id='regex-replacing'
      data-meta-type='var'
      data-meta-name='replacing'
	    data-meta-tags='regex'
    ></div>

### quote{#regex-quote}

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



<div
      data-meta='true'
      data-meta-id='regex-quote'
      data-meta-type='var'
      data-meta-name='quote'
	    data-meta-tags='regex'
    ></div>

## library data{#data}

The data library contains functions for manipulation of lists and dictionaries.



<div
      data-meta='true'
      data-meta-id='data'
      data-meta-type='library'
      data-meta-name='data'
	    data-meta-tags=''
    ></div>

### size{#data-size}

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



<div
      data-meta='true'
      data-meta-id='data-size'
      data-meta-type='var'
      data-meta-name='size'
	    data-meta-tags='data'
    ></div>

### empty?{#data-empty?}

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



<div
      data-meta='true'
      data-meta-id='data-empty?'
      data-meta-type='var'
      data-meta-name='empty?'
	    data-meta-tags='data'
    ></div>

### get{#data-get}

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



<div
      data-meta='true'
      data-meta-id='data-get'
      data-meta-type='var'
      data-meta-name='get'
	    data-meta-tags='data'
    ></div>

### put{#data-put}

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



<div
      data-meta='true'
      data-meta-id='data-put'
      data-meta-type='var'
      data-meta-name='put'
	    data-meta-tags='data'
    ></div>

### get_in{#data-get_in}

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



<div
      data-meta='true'
      data-meta-id='data-get_in'
      data-meta-type='var'
      data-meta-name='get_in'
	    data-meta-tags='data'
    ></div>

### put_in{#data-put_in}

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



<div
      data-meta='true'
      data-meta-id='data-put_in'
      data-meta-type='var'
      data-meta-name='put_in'
	    data-meta-tags='data'
    ></div>

### update{#data-update}

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



<div
      data-meta='true'
      data-meta-id='data-update'
      data-meta-type='var'
      data-meta-name='update'
	    data-meta-tags='data'
    ></div>

### update_in{#data-update_in}

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



<div
      data-meta='true'
      data-meta-id='data-update_in'
      data-meta-type='var'
      data-meta-name='update_in'
	    data-meta-tags='data'
    ></div>

### keys{#data-keys}

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



<div
      data-meta='true'
      data-meta-id='data-keys'
      data-meta-type='var'
      data-meta-name='keys'
	    data-meta-tags='data'
    ></div>

### has?{#data-has?}

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



<div
      data-meta='true'
      data-meta-id='data-has?'
      data-meta-type='var'
      data-meta-name='has?'
	    data-meta-tags='data'
    ></div>

### values{#data-values}

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



<div
      data-meta='true'
      data-meta-id='data-values'
      data-meta-type='var'
      data-meta-name='values'
	    data-meta-tags='data'
    ></div>

### entries{#data-entries}

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



<div
      data-meta='true'
      data-meta-id='data-entries'
      data-meta-type='var'
      data-meta-name='entries'
	    data-meta-tags='data'
    ></div>

### prepend{#data-prepend}

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



<div
      data-meta='true'
      data-meta-id='data-prepend'
      data-meta-type='var'
      data-meta-name='prepend'
	    data-meta-tags='data'
    ></div>

### append{#data-append}

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



<div
      data-meta='true'
      data-meta-id='data-append'
      data-meta-type='var'
      data-meta-name='append'
	    data-meta-tags='data'
    ></div>

### find{#data-find}

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



<div
      data-meta='true'
      data-meta-id='data-find'
      data-meta-type='var'
      data-meta-name='find'
	    data-meta-tags='data'
    ></div>

### find_index{#data-find_index}

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



<div
      data-meta='true'
      data-meta-id='data-find_index'
      data-meta-type='var'
      data-meta-name='find_index'
	    data-meta-tags='data'
    ></div>

### insert{#data-insert}

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



<div
      data-meta='true'
      data-meta-id='data-insert'
      data-meta-type='var'
      data-meta-name='insert'
	    data-meta-tags='data'
    ></div>

### delete{#data-delete}

`(xs, key) -> `

Given a `list` or `dict` `xs`, returns a collection of the same type consisting of all elements of `xs`, except for the element at `key`.
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



<div
      data-meta='true'
      data-meta-id='data-delete'
      data-meta-type='var'
      data-meta-name='delete'
	    data-meta-tags='data'
    ></div>

### select{#data-select}

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



<div
      data-meta='true'
      data-meta-id='data-select'
      data-meta-type='var'
      data-meta-name='select'
	    data-meta-tags='data'
    ></div>

### pluck{#data-pluck}

`(xs, key) ->`

Given a `list` or `dict` `xs`, returns a collection of the same type and of the same size.
Each element `el` of `xs` must be a `list`, `dict`, or `nil`. The given `key` is extracted
from each `el` and placed in the corresponding position in the result. If `key` is not
present in `el`, `nil` is extracted.

Returns `nil` if `xs` is `nil` or `key` is `nil`.

Throws an error if `key` cannot be cast to `string` or `long` when indexing into elements of
type `dict` or `list`.

Throws an error if any element of `xs` is not a `dict`, `list`, or `nil`.


```tweakflow
> data.pluck([{:id 1, :name "Sherlock", :address "221B Baker Street" }, {:id 2, :name "Bruce", :address "1007 Mountain Drive"}], "name")
["Sherlock", "Bruce"]

> data.pluck([["a", "b"], ["c", "d"]], 0)
["a", "c"]

> data.pluck([["a", "b"], ["c"], ["d", "e"]], 1)
["b", nil, "e"]

> data.pluck({:roll_1 [2, 4], :roll_2 [6, 5], :roll_3 [1, 3]}, 0)
{:roll_1 2, :roll_2 6, :roll_3 1}

> data.pluck(nil, 0)
nil

> data.pluck([{:a 1, :b 2}, {:a 3, :b 4}], nil)
nil
```



<div
      data-meta='true'
      data-meta-id='data-pluck'
      data-meta-type='var'
      data-meta-name='pluck'
	    data-meta-tags='data'
    ></div>

### omit{#data-omit}

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



<div
      data-meta='true'
      data-meta-id='data-omit'
      data-meta-type='var'
      data-meta-name='omit'
	    data-meta-tags='data'
    ></div>

### filter{#data-filter}

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



<div
      data-meta='true'
      data-meta-id='data-filter'
      data-meta-type='var'
      data-meta-name='filter'
	    data-meta-tags='data'
    ></div>

### shuffle{#data-shuffle}

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



<div
      data-meta='true'
      data-meta-id='data-shuffle'
      data-meta-type='var'
      data-meta-name='shuffle'
	    data-meta-tags='data'
    ></div>

### choice{#data-choice}

`(list xs, seed) -> any`

Returns a random item from `xs`. The selection is based on `seed`.
The same seed will consistently select the same index from same-length lists.

Returns `nil` if `xs` is `nil` or empty.

```tweakflow
> data.choice([1, 2, 3, 4, 5, 6], "foo")
1

> data.choice([1, 2, 3, 4, 5, 6], "bar")
6

> data.choice([1, 2, 3, 4, 5, 6], "seed")
2

> data.choice([], "seed")
nil

> data.choice(nil, "seed")
nil
```



<div
      data-meta='true'
      data-meta-id='data-choice'
      data-meta-type='var'
      data-meta-name='choice'
	    data-meta-tags='data'
    ></div>

### sample{#data-sample}

`(list xs, long count, boolean with_return, seed) -> list`

Returns a random selection of `count` items from random indexes in `xs`. The selection is based on `seed`.
The same seed will consistently select the same indexes from same-length lists.

If `with_return` is `true`, indexes can be selected multiple times. If `with_return` is `false`,
each index in `xs` can be selected only once.

In case `with_return` is `false` and `count` is larger than the number of items in `xs`, the returned list is
limited in length to the number of items available.

Returns an empty list if `count` is negative.

Returns `nil` if `xs` is `nil`, `count` is `nil`, or `with_return` is `nil`.


```tweakflow
> data.sample([1, 2, 3, 4, 5, 6], 4, true, "seed")
[2, 5, 5, 2]

> data.sample([1, 2, 3, 4, 5, 6], 4, false, "seed")
[4, 1, 6, 3]

> data.sample([1, 2, 3, 4, 5, 6], 10, true, "seed")
[2, 5, 5, 2, 5, 2, 6, 4, 4, 6]

> data.sample([1, 2, 3, 4, 5, 6], 10, false, "seed")
[4, 1, 6, 3, 5, 2]

> data.sample([1, 2, 3, 4, 5, 6], 10, true, "foo")
[1, 3, 6, 4, 2, 3, 3, 5, 1, 5]

> data.sample([1, 2, 3, 4, 5, 6], 10, false, "foo")
[3, 4, 5, 2, 6, 1]
```



<div
      data-meta='true'
      data-meta-id='data-sample'
      data-meta-type='var'
      data-meta-name='sample'
	    data-meta-tags='data'
    ></div>

### unique{#data-unique}

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



<div
      data-meta='true'
      data-meta-id='data-unique'
      data-meta-type='var'
      data-meta-name='unique'
	    data-meta-tags='data'
    ></div>

### range{#data-range}

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



<div
      data-meta='true'
      data-meta-id='data-range'
      data-meta-type='var'
      data-meta-name='range'
	    data-meta-tags='data'
    ></div>

### any?{#data-any?}

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



<div
      data-meta='true'
      data-meta-id='data-any?'
      data-meta-type='var'
      data-meta-name='any?'
	    data-meta-tags='data'
    ></div>

### none?{#data-none?}

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



<div
      data-meta='true'
      data-meta-id='data-none?'
      data-meta-type='var'
      data-meta-name='none?'
	    data-meta-tags='data'
    ></div>

### all?{#data-all?}

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



<div
      data-meta='true'
      data-meta-id='data-all?'
      data-meta-type='var'
      data-meta-name='all?'
	    data-meta-tags='data'
    ></div>

### init{#data-init}

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



<div
      data-meta='true'
      data-meta-id='data-init'
      data-meta-type='var'
      data-meta-name='init'
	    data-meta-tags='data'
    ></div>

### tail{#data-tail}

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



<div
      data-meta='true'
      data-meta-id='data-tail'
      data-meta-type='var'
      data-meta-name='tail'
	    data-meta-tags='data'
    ></div>

### head{#data-head}

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



<div
      data-meta='true'
      data-meta-id='data-head'
      data-meta-type='var'
      data-meta-name='head'
	    data-meta-tags='data'
    ></div>

### last{#data-last}

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



<div
      data-meta='true'
      data-meta-id='data-last'
      data-meta-type='var'
      data-meta-name='last'
	    data-meta-tags='data'
    ></div>

### slice{#data-slice}

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



<div
      data-meta='true'
      data-meta-id='data-slice'
      data-meta-type='var'
      data-meta-name='slice'
	    data-meta-tags='data'
    ></div>

### slices{#data-slices}

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



<div
      data-meta='true'
      data-meta-id='data-slices'
      data-meta-type='var'
      data-meta-name='slices'
	    data-meta-tags='data'
    ></div>

### reverse{#data-reverse}

`(list xs) -> list`

Returns a list that contains all elements of `xs` in reverse order.

Returns `nil` if `xs` is `nil`.

```tweakflow
> data.reverse([1, 2, 3])
[3, 2, 1]

> data.reverse(nil)
nil
```



<div
      data-meta='true'
      data-meta-id='data-reverse'
      data-meta-type='var'
      data-meta-name='reverse'
	    data-meta-tags='data'
    ></div>

### sort{#data-sort}

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



<div
      data-meta='true'
      data-meta-id='data-sort'
      data-meta-type='var'
      data-meta-name='sort'
	    data-meta-tags='data'
    ></div>

### repeat{#data-repeat}

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



<div
      data-meta='true'
      data-meta-id='data-repeat'
      data-meta-type='var'
      data-meta-name='repeat'
	    data-meta-tags='data'
    ></div>

### concat{#data-concat}

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



<div
      data-meta='true'
      data-meta-id='data-concat'
      data-meta-type='var'
      data-meta-name='concat'
	    data-meta-tags='data'
    ></div>

### merge{#data-merge}

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



<div
      data-meta='true'
      data-meta-id='data-merge'
      data-meta-type='var'
      data-meta-name='merge'
	    data-meta-tags='data'
    ></div>

### take{#data-take}

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



<div
      data-meta='true'
      data-meta-id='data-take'
      data-meta-type='var'
      data-meta-name='take'
	    data-meta-tags='data'
    ></div>

### take_while{#data-take_while}

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



<div
      data-meta='true'
      data-meta-id='data-take_while'
      data-meta-type='var'
      data-meta-name='take_while'
	    data-meta-tags='data'
    ></div>

### take_until{#data-take_until}

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



<div
      data-meta='true'
      data-meta-id='data-take_until'
      data-meta-type='var'
      data-meta-name='take_until'
	    data-meta-tags='data'
    ></div>

### drop{#data-drop}

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



<div
      data-meta='true'
      data-meta-id='data-drop'
      data-meta-type='var'
      data-meta-name='drop'
	    data-meta-tags='data'
    ></div>

### drop_while{#data-drop_while}

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



<div
      data-meta='true'
      data-meta-id='data-drop_while'
      data-meta-type='var'
      data-meta-name='drop_while'
	    data-meta-tags='data'
    ></div>

### drop_until{#data-drop_until}

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



<div
      data-meta='true'
      data-meta-id='data-drop_until'
      data-meta-type='var'
      data-meta-name='drop_until'
	    data-meta-tags='data'
    ></div>

### contains?{#data-contains?}

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



<div
      data-meta='true'
      data-meta-id='data-contains?'
      data-meta-type='var'
      data-meta-name='contains?'
	    data-meta-tags='data'
    ></div>

### index_of{#data-index_of}

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



<div
      data-meta='true'
      data-meta-id='data-index_of'
      data-meta-type='var'
      data-meta-name='index_of'
	    data-meta-tags='data'
    ></div>

### last_index_of{#data-last_index_of}

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



<div
      data-meta='true'
      data-meta-id='data-last_index_of'
      data-meta-type='var'
      data-meta-name='last_index_of'
	    data-meta-tags='data'
    ></div>

### key_of{#data-key_of}

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



<div
      data-meta='true'
      data-meta-id='data-key_of'
      data-meta-type='var'
      data-meta-name='key_of'
	    data-meta-tags='data'
    ></div>

### flatten{#data-flatten}

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



<div
      data-meta='true'
      data-meta-id='data-flatten'
      data-meta-type='var'
      data-meta-name='flatten'
	    data-meta-tags='data'
    ></div>

### map{#data-map}

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



<div
      data-meta='true'
      data-meta-id='data-map'
      data-meta-type='var'
      data-meta-name='map'
	    data-meta-tags='data'
    ></div>

### flatmap{#data-flatmap}

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



<div
      data-meta='true'
      data-meta-id='data-flatmap'
      data-meta-type='var'
      data-meta-name='flatmap'
	    data-meta-tags='data'
    ></div>

### mapcat{#data-mapcat}

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



<div
      data-meta='true'
      data-meta-id='data-mapcat'
      data-meta-type='var'
      data-meta-name='mapcat'
	    data-meta-tags='data'
    ></div>

### zip{#data-zip}

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



<div
      data-meta='true'
      data-meta-id='data-zip'
      data-meta-type='var'
      data-meta-name='zip'
	    data-meta-tags='data'
    ></div>

### zip_dict{#data-zip_dict}

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



<div
      data-meta='true'
      data-meta-id='data-zip_dict'
      data-meta-type='var'
      data-meta-name='zip_dict'
	    data-meta-tags='data'
    ></div>

### index_by{#data-index_by}

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

In case `f(x)` returns `nil` the corresponding `x` is omitted from the result.

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




<div
      data-meta='true'
      data-meta-id='data-index_by'
      data-meta-type='var'
      data-meta-name='index_by'
	    data-meta-tags='data'
    ></div>

### interpose{#data-interpose}

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



<div
      data-meta='true'
      data-meta-id='data-interpose'
      data-meta-type='var'
      data-meta-name='interpose'
	    data-meta-tags='data'
    ></div>

### reduce{#data-reduce}

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



<div
      data-meta='true'
      data-meta-id='data-reduce'
      data-meta-type='var'
      data-meta-name='reduce'
	    data-meta-tags='data'
    ></div>

### reduce_until{#data-reduce_until}

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



<div
      data-meta='true'
      data-meta-id='data-reduce_until'
      data-meta-type='var'
      data-meta-name='reduce_until'
	    data-meta-tags='data'
    ></div>

### reduce_while{#data-reduce_while}

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



<div
      data-meta='true'
      data-meta-id='data-reduce_while'
      data-meta-type='var'
      data-meta-name='reduce_while'
	    data-meta-tags='data'
    ></div>

## library time{#time}

The time library provides functions for processing datetime values.



<div
      data-meta='true'
      data-meta-id='time'
      data-meta-type='library'
      data-meta-name='time'
	    data-meta-tags=''
    ></div>

### epoch{#time-epoch}

`datetime`

The instant of time at `1970-01-01T00:00:00Z`



<div
      data-meta='true'
      data-meta-id='time-epoch'
      data-meta-type='var'
      data-meta-name='epoch'
	    data-meta-tags='time'
    ></div>

### of{#time-of}


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



<div
      data-meta='true'
      data-meta-id='time-of'
      data-meta-type='var'
      data-meta-name='of'
	    data-meta-tags='time'
    ></div>

### at{#time-at}


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



<div
      data-meta='true'
      data-meta-id='time-at'
      data-meta-type='var'
      data-meta-name='at'
	    data-meta-tags='time'
    ></div>

### seconds_between{#time-seconds_between}

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



<div
      data-meta='true'
      data-meta-id='time-seconds_between'
      data-meta-type='var'
      data-meta-name='seconds_between'
	    data-meta-tags='time'
    ></div>

### minutes_between{#time-minutes_between}

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



<div
      data-meta='true'
      data-meta-id='time-minutes_between'
      data-meta-type='var'
      data-meta-name='minutes_between'
	    data-meta-tags='time'
    ></div>

### hours_between{#time-hours_between}

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



<div
      data-meta='true'
      data-meta-id='time-hours_between'
      data-meta-type='var'
      data-meta-name='hours_between'
	    data-meta-tags='time'
    ></div>

### days_between{#time-days_between}

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



<div
      data-meta='true'
      data-meta-id='time-days_between'
      data-meta-type='var'
      data-meta-name='days_between'
	    data-meta-tags='time'
    ></div>

### months_between{#time-months_between}

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



<div
      data-meta='true'
      data-meta-id='time-months_between'
      data-meta-type='var'
      data-meta-name='months_between'
	    data-meta-tags='time'
    ></div>

### years_between{#time-years_between}

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



<div
      data-meta='true'
      data-meta-id='time-years_between'
      data-meta-type='var'
      data-meta-name='years_between'
	    data-meta-tags='time'
    ></div>

### period_between{#time-period_between}

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



<div
      data-meta='true'
      data-meta-id='time-period_between'
      data-meta-type='var'
      data-meta-name='period_between'
	    data-meta-tags='time'
    ></div>

### duration_between{#time-duration_between}

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



<div
      data-meta='true'
      data-meta-id='time-duration_between'
      data-meta-type='var'
      data-meta-name='duration_between'
	    data-meta-tags='time'
    ></div>

### add_period{#time-add_period}

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



<div
      data-meta='true'
      data-meta-id='time-add_period'
      data-meta-type='var'
      data-meta-name='add_period'
	    data-meta-tags='time'
    ></div>

### add_duration{#time-add_duration}

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



<div
      data-meta='true'
      data-meta-id='time-add_duration'
      data-meta-type='var'
      data-meta-name='add_duration'
	    data-meta-tags='time'
    ></div>

### year{#time-year}

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



<div
      data-meta='true'
      data-meta-id='time-year'
      data-meta-type='var'
      data-meta-name='year'
	    data-meta-tags='time'
    ></div>

### month{#time-month}

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



<div
      data-meta='true'
      data-meta-id='time-month'
      data-meta-type='var'
      data-meta-name='month'
	    data-meta-tags='time'
    ></div>

### day_of_month{#time-day_of_month}

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



<div
      data-meta='true'
      data-meta-id='time-day_of_month'
      data-meta-type='var'
      data-meta-name='day_of_month'
	    data-meta-tags='time'
    ></div>

### day_of_year{#time-day_of_year}

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



<div
      data-meta='true'
      data-meta-id='time-day_of_year'
      data-meta-type='var'
      data-meta-name='day_of_year'
	    data-meta-tags='time'
    ></div>

### day_of_week{#time-day_of_week}

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



<div
      data-meta='true'
      data-meta-id='time-day_of_week'
      data-meta-type='var'
      data-meta-name='day_of_week'
	    data-meta-tags='time'
    ></div>

### hour{#time-hour}

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



<div
      data-meta='true'
      data-meta-id='time-hour'
      data-meta-type='var'
      data-meta-name='hour'
	    data-meta-tags='time'
    ></div>

### minute{#time-minute}

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



<div
      data-meta='true'
      data-meta-id='time-minute'
      data-meta-type='var'
      data-meta-name='minute'
	    data-meta-tags='time'
    ></div>

### second{#time-second}

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



<div
      data-meta='true'
      data-meta-id='time-second'
      data-meta-type='var'
      data-meta-name='second'
	    data-meta-tags='time'
    ></div>

### nano_of_second{#time-nano_of_second}

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



<div
      data-meta='true'
      data-meta-id='time-nano_of_second'
      data-meta-type='var'
      data-meta-name='nano_of_second'
	    data-meta-tags='time'
    ></div>

### week_of_year{#time-week_of_year}

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



<div
      data-meta='true'
      data-meta-id='time-week_of_year'
      data-meta-type='var'
      data-meta-name='week_of_year'
	    data-meta-tags='time'
    ></div>

### offset_seconds{#time-offset_seconds}

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



<div
      data-meta='true'
      data-meta-id='time-offset_seconds'
      data-meta-type='var'
      data-meta-name='offset_seconds'
	    data-meta-tags='time'
    ></div>

### zone{#time-zone}

`(datetime x) -> string`

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



<div
      data-meta='true'
      data-meta-id='time-zone'
      data-meta-type='var'
      data-meta-name='zone'
	    data-meta-tags='time'
    ></div>

### end_of_month{#time-end_of_month}

`(datetime x, long add_months=0) -> datetime`

Returns the datetime representing the end of month of a given datetime `x`. The time component of `x` is preserved
in the result.

If `add_months` is non-zero, the given amount of months is added to `x` and the end of month of the resulting
datetime is returned.

Returns `nil` if `x` is `nil`.
Returns `nil` if `add_months` is `nil`.

```tweakflow
> time.end_of_month(time.epoch)
1970-01-31T00:00:00Z@UTC

> time.end_of_month(2020-02-21T)
2020-02-29T00:00:00Z@UTC

> time.end_of_month(2019-06-02T, -1)
2019-05-31T00:00:00Z@UTC

> time.end_of_month(2019-02-02T12:23:34@Europe/Berlin, 12)
2020-02-29T12:23:34+01:00@Europe/Berlin

> time.day_of_month(nil)
nil
```



<div
      data-meta='true'
      data-meta-id='time-end_of_month'
      data-meta-type='var'
      data-meta-name='end_of_month'
	    data-meta-tags='time'
    ></div>

### with_year{#time-with_year}

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



<div
      data-meta='true'
      data-meta-id='time-with_year'
      data-meta-type='var'
      data-meta-name='with_year'
	    data-meta-tags='time'
    ></div>

### with_month{#time-with_month}

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



<div
      data-meta='true'
      data-meta-id='time-with_month'
      data-meta-type='var'
      data-meta-name='with_month'
	    data-meta-tags='time'
    ></div>

### with_day_of_month{#time-with_day_of_month}

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



<div
      data-meta='true'
      data-meta-id='time-with_day_of_month'
      data-meta-type='var'
      data-meta-name='with_day_of_month'
	    data-meta-tags='time'
    ></div>

### with_hour{#time-with_hour}

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



<div
      data-meta='true'
      data-meta-id='time-with_hour'
      data-meta-type='var'
      data-meta-name='with_hour'
	    data-meta-tags='time'
    ></div>

### with_minute{#time-with_minute}

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



<div
      data-meta='true'
      data-meta-id='time-with_minute'
      data-meta-type='var'
      data-meta-name='with_minute'
	    data-meta-tags='time'
    ></div>

### with_second{#time-with_second}

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



<div
      data-meta='true'
      data-meta-id='time-with_second'
      data-meta-type='var'
      data-meta-name='with_second'
	    data-meta-tags='time'
    ></div>

### with_nano_of_second{#time-with_nano_of_second}

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



<div
      data-meta='true'
      data-meta-id='time-with_nano_of_second'
      data-meta-type='var'
      data-meta-name='with_nano_of_second'
	    data-meta-tags='time'
    ></div>

### with_zone{#time-with_zone}

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



<div
      data-meta='true'
      data-meta-id='time-with_zone'
      data-meta-type='var'
      data-meta-name='with_zone'
	    data-meta-tags='time'
    ></div>

### same_instant_at_zone{#time-same_instant_at_zone}

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



<div
      data-meta='true'
      data-meta-id='time-same_instant_at_zone'
      data-meta-type='var'
      data-meta-name='same_instant_at_zone'
	    data-meta-tags='time'
    ></div>

### unix_timestamp{#time-unix_timestamp}

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



<div
      data-meta='true'
      data-meta-id='time-unix_timestamp'
      data-meta-type='var'
      data-meta-name='unix_timestamp'
	    data-meta-tags='time'
    ></div>

### unix_timestamp_ms{#time-unix_timestamp_ms}

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



<div
      data-meta='true'
      data-meta-id='time-unix_timestamp_ms'
      data-meta-type='var'
      data-meta-name='unix_timestamp_ms'
	    data-meta-tags='time'
    ></div>

### of_unix_timestamp{#time-of_unix_timestamp}

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



<div
      data-meta='true'
      data-meta-id='time-of_unix_timestamp'
      data-meta-type='var'
      data-meta-name='of_unix_timestamp'
	    data-meta-tags='time'
    ></div>

### of_unix_timestamp_ms{#time-of_unix_timestamp_ms}

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



<div
      data-meta='true'
      data-meta-id='time-of_unix_timestamp_ms'
      data-meta-type='var'
      data-meta-name='of_unix_timestamp_ms'
	    data-meta-tags='time'
    ></div>

### compare{#time-compare}

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



<div
      data-meta='true'
      data-meta-id='time-compare'
      data-meta-type='var'
      data-meta-name='compare'
	    data-meta-tags='time'
    ></div>

### formatter{#time-formatter}

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



<div
      data-meta='true'
      data-meta-id='time-formatter'
      data-meta-type='var'
      data-meta-name='formatter'
	    data-meta-tags='time'
    ></div>

### parser{#time-parser}

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



<div
      data-meta='true'
      data-meta-id='time-parser'
      data-meta-type='var'
      data-meta-name='parser'
	    data-meta-tags='time'
    ></div>

### zones{#time-zones}

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



<div
      data-meta='true'
      data-meta-id='time-zones'
      data-meta-type='var'
      data-meta-name='zones'
	    data-meta-tags='time'
    ></div>

## library math{#math}

The math library contains basic mathematical functions.



<div
      data-meta='true'
      data-meta-id='math'
      data-meta-type='library'
      data-meta-name='math'
	    data-meta-tags=''
    ></div>

### abs{#math-abs}

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



<div
      data-meta='true'
      data-meta-id='math-abs'
      data-meta-type='var'
      data-meta-name='abs'
	    data-meta-tags='math'
    ></div>

### rand{#math-rand}

`(any seed) -> double`

Returns a pseudo-random double between `0.0` inclusive and `1.0` exclusive, based on given `seed`.
This function deterministically returns the same number for the same `seed`.

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



<div
      data-meta='true'
      data-meta-id='math-rand'
      data-meta-type='var'
      data-meta-name='rand'
	    data-meta-tags='math'
    ></div>

### rands{#math-rands}

`(long count, any seed) -> list`

Returns a list of length `count` of pseudo-random double values between `0.0` inclusive and `1.0` exclusive, based on given `seed`.
This function deterministically returns the same list for the same `seed`.

`nil` is a valid `seed` value.

Returns `nil` if `count` is `nil`.

Throws an error if `count` is negative.

```tweakflow
> math.rands(2, "foo")
[0.7636542620472306, 0.4845448486565105]

> math.rands(2, "bar")
[0.8603985347346733, 0.7525012276982017]

> math.rands(3, nil)
[0.730967787376657, 0.24053641567148587, 0.6374174253501083]

> math.rands(-7)
ERROR:
  code: ILLEGAL_ARGUMENT
  message: count cannot be negative, found: -7
```



<div
      data-meta='true'
      data-meta-id='math-rands'
      data-meta-type='var'
      data-meta-name='rands'
	    data-meta-tags='math'
    ></div>

### inc{#math-inc}

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



<div
      data-meta='true'
      data-meta-id='math-inc'
      data-meta-type='var'
      data-meta-name='inc'
	    data-meta-tags='math'
    ></div>

### dec{#math-dec}

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



<div
      data-meta='true'
      data-meta-id='math-dec'
      data-meta-type='var'
      data-meta-name='dec'
	    data-meta-tags='math'
    ></div>

### compare{#math-compare}

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



<div
      data-meta='true'
      data-meta-id='math-compare'
      data-meta-type='var'
      data-meta-name='compare'
	    data-meta-tags='math'
    ></div>

### min{#math-min}

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



<div
      data-meta='true'
      data-meta-id='math-min'
      data-meta-type='var'
      data-meta-name='min'
	    data-meta-tags='math'
    ></div>

### max{#math-max}

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



<div
      data-meta='true'
      data-meta-id='math-max'
      data-meta-type='var'
      data-meta-name='max'
	    data-meta-tags='math'
    ></div>

### round{#math-round}

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



<div
      data-meta='true'
      data-meta-id='math-round'
      data-meta-type='var'
      data-meta-name='round'
	    data-meta-tags='math'
    ></div>

### ceil{#math-ceil}

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



<div
      data-meta='true'
      data-meta-id='math-ceil'
      data-meta-type='var'
      data-meta-name='ceil'
	    data-meta-tags='math'
    ></div>

### floor{#math-floor}

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



<div
      data-meta='true'
      data-meta-id='math-floor'
      data-meta-type='var'
      data-meta-name='floor'
	    data-meta-tags='math'
    ></div>

### NaN?{#math-NaN?}

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



<div
      data-meta='true'
      data-meta-id='math-NaN?'
      data-meta-type='var'
      data-meta-name='NaN?'
	    data-meta-tags='math'
    ></div>

### finite?{#math-finite?}

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



<div
      data-meta='true'
      data-meta-id='math-finite?'
      data-meta-type='var'
      data-meta-name='finite?'
	    data-meta-tags='math'
    ></div>

### sqrt{#math-sqrt}

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



<div
      data-meta='true'
      data-meta-id='math-sqrt'
      data-meta-type='var'
      data-meta-name='sqrt'
	    data-meta-tags='math'
    ></div>

### factorial{#math-factorial}

`(long x) -> decimal`

if `x` is non-negative, returns the factorial of `x`.

If `x` is `nil`, returns `nil`.

Throws an error if `x` is negative.

```tweakflow
> math.factorial(0)
1d

> math.factorial(1)
1d

> math.factorial(2)
2d

> math.factorial(25)
15511210043330985984000000d

> math.factorial(nil)
nil

> math.factorial(-1)
ERROR:
  code: ILLEGAL_ARGUMENT

```



<div
      data-meta='true'
      data-meta-id='math-factorial'
      data-meta-type='var'
      data-meta-name='factorial'
	    data-meta-tags='math'
    ></div>

### sin{#math-sin}

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



<div
      data-meta='true'
      data-meta-id='math-sin'
      data-meta-type='var'
      data-meta-name='sin'
	    data-meta-tags='math'
    ></div>

### cos{#math-cos}

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



<div
      data-meta='true'
      data-meta-id='math-cos'
      data-meta-type='var'
      data-meta-name='cos'
	    data-meta-tags='math'
    ></div>

### tan{#math-tan}

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



<div
      data-meta='true'
      data-meta-id='math-tan'
      data-meta-type='var'
      data-meta-name='tan'
	    data-meta-tags='math'
    ></div>

### asin{#math-asin}

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



<div
      data-meta='true'
      data-meta-id='math-asin'
      data-meta-type='var'
      data-meta-name='asin'
	    data-meta-tags='math'
    ></div>

### acos{#math-acos}

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



<div
      data-meta='true'
      data-meta-id='math-acos'
      data-meta-type='var'
      data-meta-name='acos'
	    data-meta-tags='math'
    ></div>

### atan{#math-atan}

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



<div
      data-meta='true'
      data-meta-id='math-atan'
      data-meta-type='var'
      data-meta-name='atan'
	    data-meta-tags='math'
    ></div>

### log{#math-log}

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



<div
      data-meta='true'
      data-meta-id='math-log'
      data-meta-type='var'
      data-meta-name='log'
	    data-meta-tags='math'
    ></div>

### log10{#math-log10}

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



<div
      data-meta='true'
      data-meta-id='math-log10'
      data-meta-type='var'
      data-meta-name='log10'
	    data-meta-tags='math'
    ></div>

### bit_count{#math-bit_count}

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



<div
      data-meta='true'
      data-meta-id='math-bit_count'
      data-meta-type='var'
      data-meta-name='bit_count'
	    data-meta-tags='math'
    ></div>

### formatter{#math-formatter}

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



<div
      data-meta='true'
      data-meta-id='math-formatter'
      data-meta-type='var'
      data-meta-name='formatter'
	    data-meta-tags='math'
    ></div>

### parser{#math-parser}

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



<div
      data-meta='true'
      data-meta-id='math-parser'
      data-meta-type='var'
      data-meta-name='parser'
	    data-meta-tags='math'
    ></div>

### e{#math-e}

The double value that is closer than any other to `e`, the base of the natural logarithms.



<div
      data-meta='true'
      data-meta-id='math-e'
      data-meta-type='var'
      data-meta-name='e'
	    data-meta-tags='math'
    ></div>

### pi{#math-pi}

The double value that is closer than any other to `pi`, the ratio of the circumference of a circle to its diameter.



<div
      data-meta='true'
      data-meta-id='math-pi'
      data-meta-type='var'
      data-meta-name='pi'
	    data-meta-tags='math'
    ></div>

### min_long{#math-min_long}

The smallest representable long value: `-9223372036854775808`.



<div
      data-meta='true'
      data-meta-id='math-min_long'
      data-meta-type='var'
      data-meta-name='min_long'
	    data-meta-tags='math'
    ></div>

### max_long{#math-max_long}

The largest representable long value: `9223372036854775807`.



<div
      data-meta='true'
      data-meta-id='math-max_long'
      data-meta-type='var'
      data-meta-name='max_long'
	    data-meta-tags='math'
    ></div>

## library decimals{#decimals}

The decimals library contains utility functions for working with decimal numbers.



<div
      data-meta='true'
      data-meta-id='decimals'
      data-meta-type='library'
      data-meta-name='decimals'
	    data-meta-tags=''
    ></div>

### ulp{#decimals-ulp}

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



<div
      data-meta='true'
      data-meta-id='decimals-ulp'
      data-meta-type='var'
      data-meta-name='ulp'
	    data-meta-tags='decimals'
    ></div>

### scale{#decimals-scale}

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



<div
      data-meta='true'
      data-meta-id='decimals-scale'
      data-meta-type='var'
      data-meta-name='scale'
	    data-meta-tags='decimals'
    ></div>

### with_scale{#decimals-with_scale}

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



<div
      data-meta='true'
      data-meta-id='decimals-with_scale'
      data-meta-type='var'
      data-meta-name='with_scale'
	    data-meta-tags='decimals'
    ></div>

### round{#decimals-round}

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



<div
      data-meta='true'
      data-meta-id='decimals-round'
      data-meta-type='var'
      data-meta-name='round'
	    data-meta-tags='decimals'
    ></div>

### plain{#decimals-plain}

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



<div
      data-meta='true'
      data-meta-id='decimals-plain'
      data-meta-type='var'
      data-meta-name='plain'
	    data-meta-tags='decimals'
    ></div>

### strip_trailing_zeros{#decimals-strip_trailing_zeros}

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



<div
      data-meta='true'
      data-meta-id='decimals-strip_trailing_zeros'
      data-meta-type='var'
      data-meta-name='strip_trailing_zeros'
	    data-meta-tags='decimals'
    ></div>

### divide{#decimals-divide}

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



<div
      data-meta='true'
      data-meta-id='decimals-divide'
      data-meta-type='var'
      data-meta-name='divide'
	    data-meta-tags='decimals'
    ></div>

### divide_integral{#decimals-divide_integral}

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



<div
      data-meta='true'
      data-meta-id='decimals-divide_integral'
      data-meta-type='var'
      data-meta-name='divide_integral'
	    data-meta-tags='decimals'
    ></div>

### from_double_exact{#decimals-from_double_exact}

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



<div
      data-meta='true'
      data-meta-id='decimals-from_double_exact'
      data-meta-type='var'
      data-meta-name='from_double_exact'
	    data-meta-tags='decimals'
    ></div>

## library fun{#fun}

The function library contains utility functions to call functions using certain patterns or conditions. Functions in this library
provide functionality similar to control-flow features in other languages.



<div
      data-meta='true'
      data-meta-id='fun'
      data-meta-type='library'
      data-meta-name='fun'
	    data-meta-tags=''
    ></div>

### times{#fun-times}

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



<div
      data-meta='true'
      data-meta-id='fun-times'
      data-meta-type='var'
      data-meta-name='times'
	    data-meta-tags='fun'
    ></div>

### until{#fun-until}

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



<div
      data-meta='true'
      data-meta-id='fun-until'
      data-meta-type='var'
      data-meta-name='until'
	    data-meta-tags='fun'
    ></div>

### while{#fun-while}

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



<div
      data-meta='true'
      data-meta-id='fun-while'
      data-meta-type='var'
      data-meta-name='while'
	    data-meta-tags='fun'
    ></div>

### iterate{#fun-iterate}

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



<div
      data-meta='true'
      data-meta-id='fun-iterate'
      data-meta-type='var'
      data-meta-name='iterate'
	    data-meta-tags='fun'
    ></div>

### thread{#fun-thread}

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



<div
      data-meta='true'
      data-meta-id='fun-thread'
      data-meta-type='var'
      data-meta-name='thread'
	    data-meta-tags='fun'
    ></div>

### chain{#fun-chain}

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



<div
      data-meta='true'
      data-meta-id='fun-chain'
      data-meta-type='var'
      data-meta-name='chain'
	    data-meta-tags='fun'
    ></div>

### compose{#fun-compose}

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



<div
      data-meta='true'
      data-meta-id='fun-compose'
      data-meta-type='var'
      data-meta-name='compose'
	    data-meta-tags='fun'
    ></div>

### signature{#fun-signature}

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



<div
      data-meta='true'
      data-meta-id='fun-signature'
      data-meta-type='var'
      data-meta-name='signature'
	    data-meta-tags='fun'
    ></div>

## library locale{#locale}

The locale library provides information about available localization conventions. Localization conventions
are relevant in the context of number and date formatting as well as string sorting.



<div
      data-meta='true'
      data-meta-id='locale'
      data-meta-type='library'
      data-meta-name='locale'
	    data-meta-tags=''
    ></div>

### languages{#locale-languages}

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



<div
      data-meta='true'
      data-meta-id='locale-languages'
      data-meta-type='var'
      data-meta-name='languages'
	    data-meta-tags='locale'
    ></div>

### decimal_symbols{#locale-decimal_symbols}

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



<div
      data-meta='true'
      data-meta-id='locale-decimal_symbols'
      data-meta-type='var'
      data-meta-name='decimal_symbols'
	    data-meta-tags='locale'
    ></div>

## library bin{#bin}

The bin library provides functions that operate on binary data.



<div
      data-meta='true'
      data-meta-id='bin'
      data-meta-type='library'
      data-meta-name='bin'
	    data-meta-tags=''
    ></div>

### concat{#bin-concat}

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



<div
      data-meta='true'
      data-meta-id='bin-concat'
      data-meta-type='var'
      data-meta-name='concat'
	    data-meta-tags='bin'
    ></div>

### size{#bin-size}

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



<div
      data-meta='true'
      data-meta-id='bin-size'
      data-meta-type='var'
      data-meta-name='size'
	    data-meta-tags='bin'
    ></div>

### byte_at{#bin-byte_at}

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



<div
      data-meta='true'
      data-meta-id='bin-byte_at'
      data-meta-type='var'
      data-meta-name='byte_at'
	    data-meta-tags='bin'
    ></div>

### word_at{#bin-word_at}

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



<div
      data-meta='true'
      data-meta-id='bin-word_at'
      data-meta-type='var'
      data-meta-name='word_at'
	    data-meta-tags='bin'
    ></div>

### dword_at{#bin-dword_at}

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



<div
      data-meta='true'
      data-meta-id='bin-dword_at'
      data-meta-type='var'
      data-meta-name='dword_at'
	    data-meta-tags='bin'
    ></div>

### long_at{#bin-long_at}

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



<div
      data-meta='true'
      data-meta-id='bin-long_at'
      data-meta-type='var'
      data-meta-name='long_at'
	    data-meta-tags='bin'
    ></div>

### of_byte{#bin-of_byte}

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



<div
      data-meta='true'
      data-meta-id='bin-of_byte'
      data-meta-type='var'
      data-meta-name='of_byte'
	    data-meta-tags='bin'
    ></div>

### of_word{#bin-of_word}

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



<div
      data-meta='true'
      data-meta-id='bin-of_word'
      data-meta-type='var'
      data-meta-name='of_word'
	    data-meta-tags='bin'
    ></div>

### of_dword{#bin-of_dword}

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



<div
      data-meta='true'
      data-meta-id='bin-of_dword'
      data-meta-type='var'
      data-meta-name='of_dword'
	    data-meta-tags='bin'
    ></div>

### of_long{#bin-of_long}

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



<div
      data-meta='true'
      data-meta-id='bin-of_long'
      data-meta-type='var'
      data-meta-name='of_long'
	    data-meta-tags='bin'
    ></div>

### of_float{#bin-of_float}

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



<div
      data-meta='true'
      data-meta-id='bin-of_float'
      data-meta-type='var'
      data-meta-name='of_float'
	    data-meta-tags='bin'
    ></div>

### of_double{#bin-of_double}

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



<div
      data-meta='true'
      data-meta-id='bin-of_double'
      data-meta-type='var'
      data-meta-name='of_double'
	    data-meta-tags='bin'
    ></div>

### float_at{#bin-float_at}

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



<div
      data-meta='true'
      data-meta-id='bin-float_at'
      data-meta-type='var'
      data-meta-name='float_at'
	    data-meta-tags='bin'
    ></div>

### double_at{#bin-double_at}

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



<div
      data-meta='true'
      data-meta-id='bin-double_at'
      data-meta-type='var'
      data-meta-name='double_at'
	    data-meta-tags='bin'
    ></div>

### slice{#bin-slice}

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



<div
      data-meta='true'
      data-meta-id='bin-slice'
      data-meta-type='var'
      data-meta-name='slice'
	    data-meta-tags='bin'
    ></div>

### to_hex{#bin-to_hex}

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



<div
      data-meta='true'
      data-meta-id='bin-to_hex'
      data-meta-type='var'
      data-meta-name='to_hex'
	    data-meta-tags='bin'
    ></div>

### from_hex{#bin-from_hex}

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



<div
      data-meta='true'
      data-meta-id='bin-from_hex'
      data-meta-type='var'
      data-meta-name='from_hex'
	    data-meta-tags='bin'
    ></div>

### base64_encode{#bin-base64_encode}

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



<div
      data-meta='true'
      data-meta-id='bin-base64_encode'
      data-meta-type='var'
      data-meta-name='base64_encode'
	    data-meta-tags='bin'
    ></div>

### base64_decode{#bin-base64_decode}

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



<div
      data-meta='true'
      data-meta-id='bin-base64_decode'
      data-meta-type='var'
      data-meta-name='base64_decode'
	    data-meta-tags='bin'
    ></div>

