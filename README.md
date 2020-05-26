# Tweakflow - safe embeddable scripting for the JVM

[![Java 8+](https://img.shields.io/badge/java-8--13-4c7e9f.svg)](http://java.oracle.com)
[![License](https://img.shields.io/badge/license-MIT-4c7e9f.svg)](https://raw.githubusercontent.com/twineworks/tweakflow/master/LICENSE.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.twineworks/tweakflow/badge.svg)](http://search.maven.org/#search|gav|1|g:"com.twineworks"%20AND%20a:"tweakflow")
[![Travis Build Status](https://travis-ci.org/twineworks/tweakflow.svg?branch=master)](https://travis-ci.org/twineworks/tweakflow)
[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/v1u88koademagp2c/branch/master?svg=true)](https://ci.appveyor.com/project/slawo-ch/tweakflow/branch/master)
[![Join the chat at https://gitter.im/twineworks/tweakflow](https://badges.gitter.im/twineworks/tweakflow.svg)](https://gitter.im/twineworks/tweakflow?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Applications can use tweakflow to expose runtime information to an expression-based scripting runtime, allowing users to safely interact with the provided data and collect scripted results.

Tweakflow keeps the application in control of the data exchange. Users cannot arbitrarily call into application internals.

## Requirements
Java 8 or later is required. Builds are tested against JDK 8, 11 and 13.

## Getting the jar
Get the latest release jar from [github](https://github.com/twineworks/tweakflow/releases/latest), or from [maven central](http://search.maven.org/#search|gav|1|g:"com.twineworks"%20AND%20a:"tweakflow").

## Getting started

Start the REPL using:
```bash
$ java -jar tweakflow-1.0.2.jar repl
```

Start typing expressions for the REPL to evaluate:
```tweakflow
tweakflow interactive shell    \? for help, \q to quit
std.tf> 1+2
3

std.tf> "Hello " .. "World"
"Hello World"

std.tf> data.map([1, 2, 3], (x) -> x*x)
[1, 4, 9]
```

See the [getting started](https://twineworks.github.io/tweakflow/getting-started.html) guide for a short guided tour of language features.

### Syntax Highlighting

  - [HighlightJS grammar](https://github.com/twineworks/highlightjs-tweakflow) for the web
  - [Language Extension](https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow) for VS Code / [source](https://github.com/twineworks/tweakflow-vscode)

## Language features

### A simple computation model
Tweakflow has values and functions acting on them. All language constructs like variables, libraries, and modules merely serve to name and organize values and functions into sensible groups. Application users do not have to learn any programming paradigms to start using tweakflow expressions.

### Dynamically typed
Tweakflow is a dynamically typed language. Data types include booleans, strings, longs, doubles, exact decimal numbers, datetimes and functions, as well as nestable lists and dictionaries. All data types have literal notations.

### All data is immutable
All values in tweakflow are immutable. It is always safe to pass values between user expressions and the host application without worrying about mutable state or object identity.

### All functions are pure
All functions in tweakflow are pure and free of observable side-effects. A tweakflow function, given the same arguments, will always return the same result. The host application must take care of all non-pure operations like file I/O.

### Batteries included
Tweakflow comes with a [standard library](https://twineworks.github.io/tweakflow/modules/std.html) that allows users to perform common tasks when working with data. Your application can limit or extend the standard library to suit its needs.

### Test framework included
Tweakflow comes with an extensible [spec framework](https://twineworks.github.io/tweakflow/tools.html#spec-runner) similar to mocha, rspec, etc. The [tests for the standard library](https://github.com/twineworks/tweakflow/tree/master/src/test/resources/spec/std) are implemented with it.

### Automatic dependency tracking
When the application changes an input variable, tweakflow efficiently recalculates the values of any user variables that depend on it. Much like a spreadsheet application updates dependent formula cells when a cell changes.

### Inline documentation and meta-data
Tweakflow supports documentation annotations as well as arbitrary meta-data on variables, libraries and modules. This feature supports interactive help as well as automated generation of project documentation.

## Using tweakflow standalone
Tweakflow is designed to be an expression language embedded in a bigger application. Much like formula languages are embedded in spreadsheet applications. However, for prototyping, development and testing, it can be handy to invoke tweakflow directly.

See the [tools](https://twineworks.github.io/tweakflow/tools.html) guide, for more information on the tweakflow REPL, runner, and documentation tool.

## Embedding
Evaluating simple expressions is as easy as:

```java
TweakFlow.evaluate("1+2"); // returns the Value 3
```

Or it can be more sophisticated, providing users with application variables they can reference. Your app is in control of functions and variables available to user expressions.

The host application can allow users to define variables, group them into libraries and even separate modules for reuse across their projects. How much sophistication is available to users depends on how much your application wants to expose.

We've embedded tweakflow as the expression engine in our data automation tool: [Tweakstreet](https://tweakstreet.io)

See the [embedding](https://twineworks.github.io/tweakflow/embedding.html) guide for more information and examples.

## License
Tweakflow uses the business friendly [MIT license](https://opensource.org/licenses/MIT).

## Support
Open source does not mean you're on your own. Tweakflow is developed by [Twineworks GmbH](http://twineworks.com). Twineworks offers commercial support and consulting services. [Contact us](mailto:hi@twineworks.com) if you'd like us to help with a project.
