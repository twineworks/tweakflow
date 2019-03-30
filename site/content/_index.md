---
title: "tweakflow: an expression language for the JVM"
---

# Rationale

Many applications benefit from user input and configuration based on information the application only knows at runtime. Applications can use tweakflow to expose runtime information, and allow users to define, reference and combine provided variables to express values.

Tweakflow is designed to offer application users a formula-like notation for expressions that is familiar from spreadsheet applications. Tweakflow also supports user-defined functions, libraries, and modules, if more sophistication is needed.

Tweakflow keeps the application in control of the data exchange mechanism with user-supplied expressions. The application controls which code users can call. Applications can expose functions implemented in Java, but users cannot arbitrarily call into application internals.

# Language features

## A simple computation model
Tweakflow has values and functions acting on them. All language constructs like variables, libraries, and modules merely serve to name and organize values and functions into sensible groups. Application users do not have to learn any programming paradigms to start using tweakflow expressions. If they can use formulas in a spreadsheet application, they can use tweakflow.

## Dynamically typed
Tweakflow is a dynamically typed language. Data types include booleans, strings, longs, doubles, datetimes and functions, as well as nestable lists and dictionaries. All data types have literal notations.

## All data is immutable
All values in tweakflow are immutable. It is always safe to pass values between user expressions and the host application without worrying about mutable state or object identity.

## All functions are pure
All functions in tweakflow are pure and free of observable side-effects. A tweakflow function, given the same arguments, will always return the same result. The host application handles all non-pure operations like file I/O.

## Automatic dependency tracking
When the application changes an input variable, tweakflow efficiently recalculates the values of any user variables that depend on it. Much like a spreadsheet application updates dependent formula cells when a cell changes.

## Runtime errors can be thrown and caught
Users can throw and catch errors within expressions, allowing them to gracefully handle unexpected or missing input.

## Embedded documentation and meta-data
Tweakflow supports documentation annotations as well as arbitrary meta-data on variables, libraries and modules. This feature supports interactive help as well as automated generation of project documentation.
