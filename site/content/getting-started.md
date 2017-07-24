---
title: Getting started
---

# Getting started with tweakflow

This document is an interactive step by step guide to tweakflow expressions. Its goal is to give you a feeling for the syntax and expressive capabilities of tweakflow. It takes approximately **x** minutes to complete. 

## Starting a tweakflow REPL

[Download](link/to/releases) a recent release jar. And launch it using:

`java -jar tweakflow-your-version.jar itf`

The `itf` argument tells tweakflow that you would like to start an interactive session also known as Read-Evaluate-Print-Loop, REPL for short.

You should see a prompt similar to this:

```
$ java -jar tweakflow-0.0.1-SNAPSHOT.jar itf
tweakflow interactive shell    \? for help, \q to quit
std.tf> 
```

The prompt tells you which module context you are in. In our case it is the [std.tf](/tweakflow/reference/std.tf) module which is the standard library module that comes with tweakflow. You can quit out of the REPL at any time by entering `\q`.

## Evaluating expressions

You can now type in expressions, and the REPL will evaluate them, and print the results. 

Evaluate some number expressions using conventional operators:

```
tweakflow interactive shell    \? for help, \q to quit
std.tf> 1+3
4
std.tf> 2*8
16
std.tf>
```

Evaluate boolean expressions:

```
std.tf> 1 < 5
true
std.tf> 2*3 == 6
true
std.tf> 2*3 < 3
false
```

Evaluate strings and use the string concatenation operator:

```
std.tf> "Hello World!"
"Hello World!"
std.tf> "Hello " .. "World!"
"Hello World!"
```

Create some lists, and nest them too:

```
std.tf> [1, 2, "hello"]
[1, 2, "hello"]
std.tf> [[1, 2], ["one", "two"]]
[[1, 2], ["one", "two"]]
```

Create a dictionary:

```
std.tf> {:one 1, :two 2}
{
  :one 1,
  :two 2
}
```

Create a datetime value. You don't need to supply the time if you only need precision of days.

```
std.tf> 2017-01-23T
2017-01-23T00:00:00Z@`UTC`
```

You can include the time, but omit the time zone, implying UTC time, which is fine for most applications that only use local time, and don't care about time zone differences.

```
std.tf> 2017-01-23T18:23:11
2017-01-23T18:23:11Z@`UTC`
```

You can also go fully granular and create a fully zoned datetime value, complete with date, time, timezone offset and political timezone:

```
std.tf> 2017-01-23T18:23:11+01:00@`Europe/Berlin`
2017-01-23T18:23:11+01:00@`Europe/Berlin`
```

Let's call some functions from the standard library. We're using positional arguments, since these functions only take one argument.

```
std.tf> strings.length("foo")
3
std.tf> data.unique([1,1,2,3,3,2,1,2,3])
[1, 2, 3]
```

Now add 100 days to a date, and see where you end up. The function [add_period](link to add/period) takes multiple arguments, and it's convenient to just supply the days using named arguments, and leave the rest at default 0.

```
std.tf> time.add_period(2017-01-01T, days: 100)
2017-04-11T00:00:00Z@`UTC`
```

You can easily define a function yourself, and even call it immediately inline:

```
std.tf> (x) -> x*x
function
std.tf> ((x) -> x*x)(5)
25
```

## Expression-scoped variables

You can define helper variables scoped to an expression using [let](/tweakflow/reference/#let):

```
std.tf> let {sq: (x) -> x*x; five: 5} sq(five)
25
```

The REPL interprets hitting enter as a request to evaluate the expression. If you want to format your expression using multiple lines, you can enter multi-line edit mode using `\e` and the REPL will accept multiple lines as part of a single expression until you enter `\e` again. The REPL indicates you are in multi-line mode by placing a `*` in the prompt.

```
std.tf> \e
std.tf* let {
std.tf*   sq: (x) -> x*x
std.tf*   five: 5
std.tf* }
std.tf* sq(five)
std.tf* \e
25
```

## Variables

The REPL allows you to define ad-hoc variables visible to all expressions in the session. Let's use that ability to assign a name to a function.

```
std.tf> square: (x) -> x*x
function
std.tf> square(5)
25
```

You can assign any expression to a variable, and the tweakflow REPL will re-evaluate the expression value each time you type in a new definition.

```
std.tf> x: 10
10
std.tf> y: 2
2
std.tf> z: x*y
20
std.tf> y: 4
4
std.tf> z
40
```

You can check your session variable definitions using `\v`.

```
std.tf> \v
4 interactive variables defined
square: (x) -> x*x
x: 10
y: 4
z: x*y
std.tf>
```

You can inspect what your session variable values are using `\i`.

```
std.tf> \i
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

```
std.tf> name: "Joe"
"Joe"
std.tf> "Hello #{name}"
"Hello Joe"
```

Single quoted strings do not support escape sequences, and variable interpolation does not happen.

```
std.tf> 'Hello #{name}' 
"Hello \#{name}"
```

## Functions

Functions are values. You can assign them to variables and pass them around. In fact, many standard library functions accept functions as parameters, or return them as a result.

Functions are notated as `(parameter list) -> return value`. Let's define and call a simple function.

```
std.tf> next: (x) -> x+1
function
std.tf> next(2)
3
```

The [data.map](link/to/map) function from the standard library takes a list and a function, and returns a new list, in which all items have been transformed by the given function.

```
std.tf> data.map([1, 0, 3, -2], next)
[2, 1, 4, -1]
```

You can write functions inline without naming them. They are just values, like strings and numbers.

```
std.tf> data.map([1, 0, 3, -2], (x) -> x*x)
[1, 0, 9, 4]
```

You can create functions that remember values you supply at the time they are defined. Such functions are called closures, because they 'close over' values. The function `make_adder` creates and returns a function that accepts an argument and adds a given constant `a`. 

```
std.tf> make_adder: (a) -> ((x) -> x+a)
function
std.tf> add_1: make_adder(1)
function
std.tf> add_1(0)
1
std.tf> add_1(1)
2
std.tf> add_2: make_adder(2)
function
std.tf> add_2(0)
2
std.tf> add_2(1)
3
```

In a similar fashion the standard library makes functions for you that are parameterized to your specifications. The next example asks the standard library to give you a [formatter](link/to/formatter) function to convert numbers to strings.

```
std.tf> f: math.formatter('0.00', rounding_mode: 'half_up')
function
std.tf> f(2)
"2.00"
std.tf> f(2.123)
"2.12"
std.tf> f: math.formatter('0.00', rounding_mode: 'up')
function
std.tf> f(2.123)
"2.13"
```

## Conditionals

Tweakflow supports a standard `if` construct to perform conditional calculations. It's formal syntax is: `if expression then? then_expression else? else_expression`

```
std.tf> parity: (x) -> if x % 2 == 0 then "even" else "odd"
function
std.tf> parity(3)
"odd"
std.tf> parity(2)
"even"
std.tf>
```

Both the `then_expression` and the `else_expression` are mandatory, but the `then` and `else` keywords are optional, allowing you to write nested conditions that look like a sequence of tests.

Define a function that returns the sign of a number as -1, 0, or 1 if the number is negative, zero, or positive:

```
std.tf> \e
sgn: (x) -> 
  if x > 0 then 1 
  if x < 0 then -1
  else 0
\e
function
std.tf> sgn(0)
0
std.tf> sgn(-2.3)
-1
std.tf> sgn(9.2)
1
```

## List comprehensions

Tweakflow supports list comprehensions allowing you to generate, transform, combine and filter lists succinctly. A list comprehension uses generators to define variables that loop over a list expression. They nest if more than one generator is present. Generators have the following syntax: `identifier '<-' expression`.

Variable definitions create helper variables. They are in scope for all subsequent expressions in the list comprehension.

Expressions act as filters. If they cast to boolean true, the current entry is part of the result list, if they cast to boolean false, the current element is omitted.

The closing expression describes the individual item to include in the result list.

Let's look at some examples.

Create a list of coordinates for a chess board:

```
std.tf> \e 
for 
  x <- ["a", "b", "c", "d", "e", "f", "g", "h"], 
  y <- [1, 2, 3, 4, 5, 6, 7, 8],
  x .. y
\e
["a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "g1", "g2", "g3", "g4", "g5", "g6", "g7", "g8", "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8"]
std.tf>
```

Create a list of [pythagorean triples](https://en.wikipedia.org/wiki/Pythagorean_triple) trying sides up to the size of 15. 

```
std.tf> \e
for
  a <- data.range(1, 15),
  b <- data.range(a, 15),
  c: math.sqrt(a*a + b*b),  
  (c as long) == c,         
  [a, b, c as long]
\e
[[3, 4, 5], [5, 12, 13], [6, 8, 10], [8, 15, 17], [9, 12, 15]]
std.tf> 
```

Above example loops over `a` going from 1 to 15, and `b` going from `a` to `15`, calculates `c`, and filters out any non-integer `c` values. If `c` happens to be an integer, the triple `[a, b, c]` is included in the result list.

## Pattern matching

Tweakflow supports matching on value, type and structure of an input value, additionally supporting a guard expression before a match is accepted. The `@` sign followed by a variable name is used to indicate a captured match scoped to the expression associated with a pattern.

Let's look at some examples. 

This example matches on value. The function `vowel?` returns true if called with a string holding a single latin vowel, false otherwise.

```
std.tf> \e
vowel?: (x) ->
  match x
    "a"     -> true
    "e"     -> true
    "i"     -> true
    "o"     -> true
    "u"     -> true
    default -> false
\e
function
std.tf> vowel?("a")
true
std.tf> vowel?("b")
false
std.tf> vowel?("e")
true
```

The next example matches on type. The function `numeric?` returns true if called with an argument of a numeric type, false otherwise.

```
std.tf> \e
numeric?: (x) ->
  match x
    long    -> true
    double  -> true
    default -> false
\e
function
std.tf> numeric?(1.3)
true
std.tf> numeric?(4)
true
std.tf> numeric?("foo")
false
```

The next example matches on structure. The function `magnitude` accepts a vector of two dimensions and returns its length. The vector may be a list with two numbers, or a dict that has only keys `:x` and `:y` with numeric values. The function returns `nil` if no valid vector was given. It uses the `numeric?` function from the previous example in guard expressions, so unexpected inputs are not processed.

```
std.tf> \e
magnitude: (vec) ->
  match vec
    [@x, @y],       numeric?(x) && numeric?(y) -> math.sqrt(x*x + y*y)
    {:x @x, :y @y}, numeric?(x) && numeric?(y) -> math.sqrt(x*x + y*y)
    default -> nil
\e
function
std.tf> magnitude([0,0])
0.0
std.tf> magnitude([3, 4])
5.0
std.tf> magnitude(["foo", 4])
nil
std.tf> magnitude([3, 4, 5])
nil
std.tf> magnitude({:x 3, :y 4})
5.0
std.tf> magnitude({:x 3, :y 4, :z 4})
nil
```

The next example matches on partial structure. The function `pairs` transforms a list of the form `[a, b, c, d, …]` into a list of pairs `[[a, b], [c, d], ...]`. If the list has an odd number of items, the last item is discarded. If the argument is not a list, the function returns `nil`.

```
std.tf> \e
pairs: (xs) ->
  match xs
    [@a, @b, @...tail]  -> [[a, b], ...pairs(tail)]
    [@]                 -> []
    []                  -> []
    default             -> nil
\e
function
std.tf> pairs([1, 2, 3, 4])
[[1, 2], [3, 4]]
std.tf> pairs([1, 2, 3])
[[1, 2]]
std.tf> pairs([1])
[]
std.tf> pairs("foo")
nil
std.tf> 
```

## Throwing and catching errors

Tweakflow supports throwing and catching errors within expressions. You can `throw` any value you like to represent an error, and if that happens within a `try` -`catch` block, the block evaluates to the `catch` expression.

The function `ensure_weekend` accepts a date time value `x`, and returns `x` if it falls within a Saturday or Sunday. It throws an error otherwise. The REPL logs that an error has been thrown and mentions the thrown error as `value`.

```
std.tf> \e
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

std.tf> ensure_weekend(2016-12-30T)
ERROR: {
  ... snip ...
  :value {
    :message "x must be a weekend day, but is: Friday",
    :day_of_week "Friday"
  }
  ... snip ...  
}

std.tf> ensure_weekend(2016-12-31T)
2016-12-31T00:00:00Z@`UTC`

std.tf> ensure_weekend(2017-01-01T)
2017-01-01T00:00:00Z@`UTC`
```

The function `analyze_dates` accepts a list of datetimes, and returns a list where each  datetime that falls on a weekend is retained, and non-weekends are replaced with a custom error message constructed from the error details `ensure_weekend` provides.  

```
std.tf> \e
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

std.tf> analyze_dates([2016-12-30T, 2016-12-31T, 2017-01-01T, 2017-01-02T, 2017-01-03T])
["Bad Friday", 2016-12-31T00:00:00Z@`UTC`, 2017-01-01T00:00:00Z@`UTC`, "Bad Monday", "Bad Tuesday"]
```

## Debugging

Sometimes users need help debugging an unexpected result. Tweakflow users can use the [debug](link to ref) construct, to log the value of any expression. The host application decides what happens with debugged values. The REPL just prints them to screen.

Debug itself is an expression that evaluates to the value being debugged. Let's write a function that given a filename, returns the file's extension including the dot. It debugs the value of a temporary variable.

```
std.tf> \e
extension: (name) ->
  let {
    dot_position: debug strings.last_index_of(name, '.')    
  }
  strings.substring(name, dot_position)
\e
function

std.tf> extension("file.txt")
4
".txt"

std.tf> extension("file")
-1
ERROR: {
  :message "from must not be negative: -1",
  :code "INDEX_OUT_OF_BOUNDS",
  ...
}
```

You can also define an unused expression-local variable that generates a more speaking output.

```
std.tf> \e
extension: (name) ->
  let {
    dot_position: strings.last_index_of(name, '.')
    _debug: debug "DEBUG: last position of dot in #{name}: "..dot_position
  }
  strings.substring(name, dot_position)
\e
function

std.tf> extension("file.txt")
"DEBUG: last position of dot in file.txt: 4"
".txt"

std.tf> extension("file")
"DEBUG: last position of dot in file: -1"
ERROR: {
  :message "from must not be negative: -1",
  :code "INDEX_OUT_OF_BOUNDS",
  ...
}
```

Above debug output should help sorting out the function to account for the fact that the argument might not contain a dot.

If two expressions are supplied to debug, separated by comma, the first one is passed to the host application for debugging, and the second is what the debug expression evaluates to.

As an example, let's debug a function with some conditional branches, logging which branches are taken.

```
std.tf> \e
sgn: (x) -> 
  debug "DEBUG: calculating sign of x: #{x}",
  if x > 0 then debug "DEBUG: x is positive", 1 
  if x < 0 then debug "DEBUG: x is negative", -1
  else debug "DEBUG: x is zero or nil", 0
\e
function

std.tf> sgn(10)
"DEBUG: calculating sign of x: 10"
"DEBUG: x is positive"
1

std.tf> sgn(-10)
"DEBUG: calculating sign of x: -10"
"DEBUG: x is negative"
-1

std.tf> sgn(0)
"DEBUG: calculating sign of x: 0"
"DEBUG: x is zero or nil"
0
```

## Conclusion

Above tutorial is designed to give a feel for the nature of tweakflow expressions. Check out the [reference](ref link) for more information about the language itself. The [embedding]() guide explains how to include tweakflow in your application.

 