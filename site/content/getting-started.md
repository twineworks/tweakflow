---
title: Getting started
---

# Getting started with tweakflow

This document is an interactive step-by-step guide to tweakflow expressions. Its goal is to give you a feeling for the syntax and expressive capabilities of tweakflow. 

## Starting a tweakflow REPL

[Download](https://github.com/twineworks/tweakflow/releases) the latest release jar. And launch it using:

`java -jar tweakflow-version.jar itf`

The `itf` argument tells tweakflow to start an interactive session also known as Read-Evaluate-Print-Loop, REPL for short.

You should see a prompt similar to this:

```none
$ java -jar tweakflow-version.jar itf
tweakflow interactive shell    \? for help, \q to quit
std.tf>
```

The prompt tells you which module context you are in. In our case it is the [std.tf](/tweakflow/modules/std.html) module which is the default standard library module that comes with tweakflow. You can quit out of the REPL at any time by entering `\q`.

## Evaluating expressions

You can now type in expressions, and the REPL will evaluate them, and print the results.

Evaluate some number expressions using conventional operators:

```ruby
> 1+3
4
> 2*8
16
```

Evaluate boolean expressions:

```ruby
> 1 < 5
true
> 2*3 == 6
true
> 2*3 < 3
false
```

Evaluate strings and use the string concatenation operator:

```ruby
> "Hello World!"
"Hello World!"
> "Hello " .. "World!"
"Hello World!"
```

Create some lists, and nest them too:

```ruby
> [1, 2, "hello"]
[1, 2, "hello"]
> [[1, 2], ["one", "two"]]
[[1, 2], ["one", "two"]]
```

Create a dictionary:

```ruby
> {:one 1, :two 2}
{
  :one 1,
  :two 2
}
```

Create a datetime value. If you don't need supply the time, it defaults to midnight UTC time.

```ruby
> 2017-01-23T
2017-01-23T00:00:00Z@`UTC`
```

You can also fully specify a zoned datetime value, complete with date, time, timezone offset and political timezone:

```ruby
> 2017-01-23T18:23:11+01:00@`Europe/Berlin`
2017-01-23T18:23:11+01:00@`Europe/Berlin`
```

## Calling functions

Let's call some functions from the standard library:

```ruby
> strings.length("foo")
3
> data.unique([1,1,2,3,3,2,1,2,3])
[1, 2, 3]
> strings.index_of("banana", "nan")
2
```

You were using positional arguments. Let's add 100 days to a date, and see where we end up. The function [add_period](/tweakflow/modules/std.html#add-period) takes multiple parameters. Let's just supply the start date and days to add using named arguments, and leave the other parameters at their default values.

```ruby
> time.add_period(start: 2017-01-01T, days: 100)
2017-04-11T00:00:00Z@`UTC`
```

You can even start with positional arguments, and switch to named arguments later in the call.

```ruby
> time.add_period(2017-01-01T, days: 100)
2017-04-11T00:00:00Z@`UTC`
```

## Variables

The REPL allows you to define ad-hoc variables visible to all expressions in the session. Let's use that ability to assign a name to a function.

```ruby
> square: (x) -> x*x
function
> square(5)
25
```

You can assign any expression to a variable, and the tweakflow REPL will re-evaluate the expression value each time you type in a new definition.

```ruby
> x: 10
10
> y: 2
2
> z: x*y
20
> y: 4
4
> z
40
```

You can check your session variable definitions using `\v`.

```ruby
> \v
# 4 interactive variables defined
square: (x) -> x*x
x: 10
y: 4
z: x*y
```

You can inspect what your session variable values are using `\i`.

```ruby
> \i
# interactive section
  square: function
  `$`: 40
  x: 10
  y: 4
  z: 40
```

The REPL defines a special variable `$` that it maintains. It is defined as the most recently entered expression.

## Variable interpolation in strings

You can include the values of variables in double quoted strings using the `#{varname}` escape sequence.

```ruby
> name: "Joe"
"Joe"
> "Hello #{name}"
"Hello Joe"
```

There are other escape sequences like `\n` for newlines and `\t` for tabs. 

## Local variables

You can define helper variables scoped to an expression using let:

```ruby
> let {sq: (x) -> x*x; five: 5} sq(five)
25
```

Tweakflow code can be formatted across multiple lines. But the REPL interprets hitting enter as a request to evaluate the current line as an expression, which can make entering multi-line expressions in the REPL impractical.

If you want to format your expression using multiple lines, you can enter multi-line edit mode using `\e` and the REPL will accept multiple lines as part of a single expression until you enter `\e` again. The REPL indicates you are in multi-line mode by placing a `*` in the prompt. You can rewrite the above example in multi-line mode on the REPL like this:

```ruby
> \e
let {
  sq: (x) -> x*x
  five: 5
}
sq(five)
\e
25
```

## Types

Every value in tweakflow has an associated type. You can determine the types using `typeof`: 

```ruby
> typeof "Hello"
"string"
> typeof 2.3
"double"
> typeof []
"list"
> typeof (x) -> x*x
"function"
```

You can convert between many types automatically. Tweakflow is very conservative about automatic conversion. If there is a greater loss of information than can be expected from the nature of the types, it throws an error.

```ruby
> "foo" as boolean
true
> 2.3 as string
"2.3"
> "2.3" as double
2.3
> 2.3 as long
2
> "2.3kg" as double
ERROR: {
  :message "Cannot cast 2.3kg to double",
  :code "CAST_ERROR",
  :source "\"2.3kg\" as double"
  ...
}
```

## Defining functions

Functions are values. You can assign them to variables and pass them around. In fact, many standard library functions accept functions as parameters, or return them as a result.

Functions are written as `(parameter list) -> return value`. Let's define and call a simple function.

```ruby
> next: (x) -> x+1
function
> next(2)
3
```

The [data.map](/tweakflow/modules/std.html#map) function from the standard library takes a list and a function, and returns a new list, in which all items have been transformed by the given function.

```ruby
> data.map([1, 0, 3, -2], next)
[2, 1, 4, -1]
```

You can write functions inline without naming them. Functions are just values, like strings and numbers.

```ruby
> data.map([1, 0, 3, -2], (x) -> x*x)
[1, 0, 9, 4]
```

Functions can also makes functions that are parameterized to your specifications. The next example asks the standard library to give you a [formatter](/tweakflow/modules/std.html#formatter-1) function to convert numbers to strings.

```ruby
> f: math.formatter('0.00', rounding_mode: 'half_up')
function
> f(2)
"2.00"
> f(2.123)
"2.12"
> f: math.formatter('0.00', rounding_mode: 'up')
function
> f(2.123)
"2.13"
```

## Conditionals

Tweakflow supports a standard `if` construct to perform conditional calculations. 

```ruby
> parity: (x) -> if x % 2 == 0 then "even" else "odd"
function
> parity(3)
"odd"
> parity(2)
"even"
```

The syntax is: `if condition then then_expression else else_expression`. Both the `then_expression` and the `else_expression` are mandatory, but the `then` and `else` keywords are optional, allowing you to write nested conditions that look like a sequence of tests.

Define a function that returns the sign of a number as `-1`, `0`, or `1` if the number is negative, zero, or positive:

```ruby
> \e
sgn: (x) ->
  if x > 0 then 1
  if x < 0 then -1
  else 0
\e
function
> sgn(0)
0
> sgn(-2.3)
-1
> sgn(9.2)
1
```

## List comprehensions

Tweakflow supports list comprehensions using `for` synax, allowing you to generate, transform, combine and filter lists. A list comprehension uses generators to define variables that loop over a list value. They nest if more than one generator is present. Generators have the following syntax: `identifier '<-' list_expression`.

Create a list of coordinates from given axes:

```ruby
> \e
for
  x <- ["a", "b", "c"],
  y <- [1, 2, 3, 4, 5, 6],
  x .. y
\e
["a1", "a2", "a3", "a4", "a5", "a6", "b1", "b2", "b3", "b4", "b5", "b6", "c1", "c2", "c3", "c4", "c5", "c6"]
```

Variable definitions create helper variables. They are in scope for all subsequent expressions in the list comprehension.

```ruby
> \e
for
  x  <- data.range(1, 3),
  y  <- data.range(x, 3),
  p: x*y,
  "#{x} * #{y} = #{p}"
\e
["1 * 1 = 1", "1 * 2 = 2", "1 * 3 = 3", "2 * 2 = 4", "2 * 3 = 6", "3 * 3 = 9"]
```

Free expressions act as filters. If they evaluate to boolean true, the current entry is part of the result list, if they evaluate to boolean false, the current element is omitted.

Create a list of [pythagorean triples](https://en.wikipedia.org/wiki/Pythagorean_triple) trying sides up to the size of 15.

```ruby
> \e
for
  a <- data.range(1, 15),
  b <- data.range(a, 15),
  c: math.sqrt(a*a + b*b),  
  (c as long) == c,         
  [a, b, c as long]
\e
[[3, 4, 5], [5, 12, 13], [6, 8, 10], [8, 15, 17], [9, 12, 15]]
```

Above example loops over `a` going from 1 to 15, and `b` going from `a` to `15`, calculates `c`, and filters out any non-integer `c` values. If `c` happens to be an integer, the triple `[a, b, c]` is included in the result list.

## Pattern matching

Tweakflow supports matching on value, type and structure of an input value, additionally supporting a guard expression before a match is accepted. The `@` sign followed by a variable name is used to indicate a captured match scoped to the expression associated with a pattern.

The next example matches on partial structure. The function `pairs` transforms a list of the form `[a, b, c, d, …]` into a list of pairs `[[a, b], [c, d], ...]`. If the list has an odd number of items, the last item is discarded. If the argument is not a list, the function returns `nil`.

```ruby
> \e
pairs: (xs) ->
  match xs
    [@a, @b, @...tail]  -> [[a, b], ...pairs(tail)]
    [@]                 -> []
    []                  -> []
    default             -> nil
\e
function
> pairs([1, 2, 3, 4])
[[1, 2], [3, 4]]
> pairs([1, 2, 3])
[[1, 2]]
> pairs([1])
[]
> pairs("foo")
nil
```

## Throwing and catching errors

Tweakflow supports throwing and catching errors within expressions. You can `throw` any value you like to represent an error, and if that happens within a `try/catch` block, the block evaluates to the `catch` expression.

The function `ensure_weekend` accepts a date time value `x`, and returns `x` if it falls within a Saturday or Sunday. It throws an error otherwise. The REPL logs that an error has been thrown and mentions the thrown error as `value`.

```ruby
> \e
ensure_weekend: (datetime x) ->
  if time.day_of_week(x) >= 6
    x
  else
    let {
      day: time.formatter('cccc')(x)
    }
    throw {
      :message "x must be a weekend day, but is: #{day}"
      :day_of_week day
    }
\e
function

> ensure_weekend(2016-12-30T) # a Friday
ERROR: {
  ... snip ...
  :value {
    :message "x must be a weekend day, but is: Friday",
    :day_of_week "Friday"
  }
  ... snip ...  
}

> ensure_weekend(2016-12-31T) # a Saturday
2016-12-31T00:00:00Z@`UTC`

> ensure_weekend(2017-01-01T) # a Sunday
2017-01-01T00:00:00Z@`UTC`
```

The function `analyze_dates` accepts a list of datetimes, and returns a list where each  datetime that falls on a weekend is retained, and non-weekends are replaced with a custom error message constructed from the error details `ensure_weekend` provides.  

```ruby
> \e
analyze_dates: (xs) ->
  let {
    f: (datetime d) ->
      try
        ensure_weekend(d)
      catch error
        "Bad "..error[:day_of_week]
  }
  data.map(xs, f)
\e
function

> analyze_dates([2016-12-30T, 2016-12-31T, 2017-01-01T, 2017-01-02T, 2017-01-03T])
["Bad Friday", 2016-12-31T00:00:00Z@`UTC`, 2017-01-01T00:00:00Z@`UTC`, "Bad Monday", "Bad Tuesday"]
```

## Debugging

Sometimes users need help debugging an unexpected result. Tweakflow users can use the debug construct, to log the value of any expression. The host application decides what happens with debugged values. The REPL just prints them to screen.

Debug itself is an expression that evaluates to the value being debugged. Let's write a function that given a filename, returns the file's extension including the dot. It debugs the value of a temporary variable named `dot_position`.

```ruby
> \e
extension: (name) ->
  let {
    dot_position: debug strings.last_index_of(name, '.')    
  }
  strings.substring(name, dot_position)
\e
function

> extension("file.txt")
4
".txt"

> extension("file")
-1
ERROR: {
  :message "from must not be negative: -1",
  :code "INDEX_OUT_OF_BOUNDS",
  ...
}
```

You can also define a local variable that generates a more speaking output.

```ruby
> \e
extension: (name) ->
  let {
    dot_position: strings.last_index_of(name, '.')
    _debug: debug "DEBUG: last position of dot in #{name}: #{dot_position}"
  }
  strings.substring(name, dot_position)
\e
function

> extension("file.txt")
"DEBUG: last position of dot in file.txt: 4"
".txt"

> extension("file")
"DEBUG: last position of dot in file: -1"
ERROR: {
  :message "from must not be negative: -1",
  :code "INDEX_OUT_OF_BOUNDS",
  ...
}
```

Above debug output should help sorting out the function to account for the fact that the argument might not contain a dot.

## Conclusion

You have a good feeling for the nature of tweakflow expressions. If you would like to know more, check out the [language reference](/tweakflow/reference.html) for detailed information about the language. 
