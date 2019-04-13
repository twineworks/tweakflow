---
title: Getting started
---

# Getting started with tweakflow

This document is an interactive step-by-step guide to tweakflow expressions. Its goal is to give you a feeling for the syntax and expressive capabilities of tweakflow.

## Requirements

Tweakflow requires Java 8 or later. Builds are tested against JDK 8 and JDK 11.

## Getting tweakflow

[Download](https://github.com/twineworks/tweakflow/releases/latest) the latest release jar. You can also obtain it from maven central using the following dependency:

```xml
<dependency>
    <groupId>com.twineworks</groupId>
    <artifactId>tweakflow</artifactId>
    <version>{{< version >}}</version>
</dependency>
```

## Starting a tweakflow REPL
Launch the interactive REPL using:

`java -jar tweakflow-{{< version >}}.jar repl`

The `repl` argument tells tweakflow to start an interactive session also known as Read-Evaluate-Print-Loop, REPL for short.

You should see a prompt similar to this:

```none
$ java -jar tweakflow-{{< version>}}.jar repl
tweakflow interactive shell    \? for help, \q to quit
std.tf>
```

The prompt tells you which module context you are in. In our case it is the [std.tf](/modules/std.html) module which is the default standard library module that comes with tweakflow. You can quit out of the REPL at any time by entering `\q`.

## Evaluating expressions

You can now type in expressions, and the REPL will evaluate them, and print the results. If you embed tweakflow in your application, you can empower your users to  use these kinds of expressions to communicate values back to your app.

Evaluate some number expressions using conventional operators:

```tweakflow
> 1+3
4
> 2*8
16
```

Evaluate boolean expressions:

```tweakflow
> 1 < 5
true
> 2*3 == 6
true
> 2*3 < 3
false
```

Evaluate strings and use the string concatenation operator:

```tweakflow
> "Hello World!"
"Hello World!"
> "Hello " .. "World!"
"Hello World!"
```

Create some lists, and nest them too:

```tweakflow
> [1, 2, "hello"]
[1, 2, "hello"]

> [[1, 2], ["one", "two"]]
[[1, 2], ["one", "two"]]
```

Create a dictionary:

```tweakflow
> {:one 1, :two 2}
{
  :one 1,
  :two 2
}
```

Create a datetime value. If you don't need supply the time, it defaults to midnight UTC time.

```tweakflow
> 2017-01-23T
2017-01-23T00:00:00Z@`UTC`
```

You can also fully specify a zoned datetime value, complete with date, time, timezone offset and political timezone:

```tweakflow
> 2017-01-23T18:23:11+01:00@`Europe/Berlin`
2017-01-23T18:23:11+01:00@`Europe/Berlin`
```

## Calling functions

Let's call some functions from the standard library:

```tweakflow
> strings.length("foo")
3

> data.unique([1,1,2,3,3,2,1,2,3])
[1, 2, 3]

> strings.index_of("banana", "nan")
2
```

You were using positional arguments. Let's add 100 days to a date, and see where we end up. The function [add_period](/modules/std.html#add-period) takes multiple parameters. Let's just supply the start date and days to add using named arguments, and leave the other parameters at their default values.

```tweakflow
> time.add_period(start: 2017-01-01T, days: 100)
2017-04-11T00:00:00Z@`UTC`
```

You can even start with positional arguments, and switch to named arguments later in the call.

```tweakflow
> time.add_period(2017-01-01T, days: 100)
2017-04-11T00:00:00Z@`UTC`
```

## Variables

The REPL allows you to define ad-hoc variables visible to all expressions in the session. Let's use that ability to assign a name to a function.

```tweakflow
> square: (x) -> x*x
function

> square(5)
25
```

You can assign any expression to a variable, and the tweakflow REPL will re-evaluate the expression value each time you type in a new definition.

```tweakflow
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

```tweakflow
> \v
# 4 interactive variables defined
square: (x) -> x*x
x: 10
y: 4
z: x*y
```

You can inspect what your session variable values are using `\i`.

```tweakflow
> \i
# interactive section
  square: function
  `$`: 40
  x: 10
  y: 4
  z: 40
```

The REPL defines a special variable `$` that it maintains. It is defined as the most recently entered expression.

## String interpolation

You can include the values of expressions in double quoted strings using the `#{expression}` escape sequence.

```tweakflow
> name: "Joe"
"Joe"

> "Hello #{name}"
"Hello Joe"

> "Hello #{strings.upper_case(name)}"
"Hello JOE"
```

There are other escape sequences like `\n` for newlines and `\t` for tabs.

## Defining functions

Functions are values. You can assign them to variables and pass them around. In fact, many standard library functions accept functions as parameters, or return them as a result.

Functions are written as `(parameter list) -> return value`. Let's define and call a simple function.

```tweakflow
> next: (x) -> x+1
function

> next(2)
3
```

The [data.map](/modules/std.html#map) function from the standard library takes a list and a function, and returns a new list, in which all items have been transformed by the given function.

```tweakflow
> data.map([1, 0, 3, -2], next)
[2, 1, 4, -1]
```

You can write functions inline without naming them. Functions are just values, like strings and numbers.

```tweakflow
> data.map([1, 0, 3, -2], (x) -> x*x)
[1, 0, 9, 4]
```

## Local variables

You can define helper variables scoped to an expression using let:

```tweakflow
> let {sq: (x) -> x*x; five: 5;} sq(five)
25
```

Tweakflow code can be formatted across multiple lines. But the REPL interprets hitting enter as a request to evaluate the current line as an expression, which can make entering multi-line expressions in the REPL impractical.

If you want to format your expression using multiple lines, you can enter multi-line edit mode using `\e` and the REPL will accept multiple lines as part of a single expression until you enter `\e` again. The REPL indicates you are in multi-line mode by placing a `*` in the prompt. You can rewrite the above example in multi-line mode on the REPL like this:

```tweakflow
> \e
let {
  sq: (x) -> x*x;
  five: 5;
}
sq(five)
\e
25
```

## Types

Every value in tweakflow has an associated type. You can determine the types using `typeof`:

```tweakflow
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

```tweakflow
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

## Conditionals

Tweakflow supports a standard `if` construct to perform conditional calculations.

```tweakflow
> parity: (x) -> if x % 2 == 0 then "even" else "odd"
function

> parity(3)
"odd"

> parity(2)
"even"
```

The syntax is:
```text
if condition then then_expression else else_expression
```
Both the `then_expression` and the `else_expression` are mandatory, but the `then` and `else` keywords are optional, allowing you to write nested conditions that look like a sequence of tests.

Define a function that returns the sign of a number as `-1`, `0`, or `1` if the number is negative, zero, or positive:

```tweakflow
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


## Conclusion

You now have a good feeling for the nature of tweakflow expressions. Check out the [language reference](/reference.html) for more detail. It contains formal syntax information, describes advanced features like list comprehensions and pattern matching, and describes the library and module system.

Check out the [embedding guide](/embedding.html) for details on how to include tweakflow in your JVM application.
