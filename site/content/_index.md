---
title: "tweakflow: an embeddable scripting language for the JVM"
---

# Rationale

Applications can use tweakflow to expose runtime information to an expression-based scripting runtime, allowing users to safely interact with the provided data and collect scripted results.

Tweakflow keeps the application in control of the data exchange. Users cannot arbitrarily call into application internals. 

# Language features

## A simple computation model
Tweakflow has values and functions acting on them. All language constructs like variables, libraries, and modules merely serve to name and organize values and functions into sensible groups. Application users do not have to learn any programming paradigms to start using tweakflow expressions. If they are able to use formulas in a spreadsheet application, they are able to use tweakflow.

### Batteries included
Tweakflow comes with a [standard library](/modules/std.html) that allows users to perform common tasks when working with data. Your application can limit or extend the standard library to suit its needs.

## Dynamically typed
Tweakflow is a dynamically typed language. Data types include booleans, strings, longs, doubles, datetimes and functions, as well as nestable lists and dictionaries. All data types have literal notations.

## All data is immutable
All values in tweakflow are immutable. It is always safe to pass values between user expressions and the host application without worrying about mutable state or object identity.

## All functions are pure
All functions in tweakflow are pure and free of observable side-effects. A tweakflow function, given the same arguments, will always return the same result. The host application handles all non-pure operations like file I/O.

## Embedded documentation and meta-data
Tweakflow supports documentation annotations as well as arbitrary meta-data on variables, libraries and modules. This feature supports interactive help as well as automated generation of project documentation.
