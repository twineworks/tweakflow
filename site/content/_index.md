---
title: "tweakflow: embeddable scripting language for the JVM"
---
# Tweakflow

A safe, embeddable scripting language for the JVM. \
[![Java 8+](https://img.shields.io/badge/java-8--11-4c7e9f.svg)](http://java.oracle.com)
[![License](https://img.shields.io/badge/license-MIT-4c7e9f.svg)](https://raw.githubusercontent.com/twineworks/tweakflow/master/LICENSE.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.twineworks/tweakflow/badge.svg)](http://search.maven.org/#search|gav|1|g:"com.twineworks"%20AND%20a:"tweakflow")
[![Travis Build Status](https://travis-ci.org/twineworks/tweakflow.svg?branch=master)](https://travis-ci.org/twineworks/tweakflow)
[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/v1u88koademagp2c/branch/master?svg=true)](https://ci.appveyor.com/project/slawo-ch/tweakflow/branch/master)
[![Join the chat at https://gitter.im/twineworks/tweakflow](https://badges.gitter.im/twineworks/tweakflow.svg)](https://gitter.im/twineworks/tweakflow?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


## Why tweakflow?

JVM applications can use tweakflow to interact with users through an expression-based scripting runtime. Users can safely interact with application-provided data and the application can collect any dynamically calculated results.

Tweakflow keeps the application in control of the data exchange. Users cannot arbitrarily call into application internals.

This is in contrast to other embeddable languages like JRuby and JavaScript that have the ability to call into application internals via Java interop features, which tweakflow deliberately does not offer.

# Language features

## A simple computation model
Tweakflow has values and functions acting on them. All language constructs like variables, libraries, and modules merely serve to name and organize values and functions into sensible groups. Application users do not have to learn any programming paradigms to start using tweakflow expressions. If they are able to use formulas in a spreadsheet application, they are able to use tweakflow.

### Batteries included
Tweakflow comes with a [standard library](/modules/std.html) that allows users to perform common tasks when working with data. Your application can limit or extend the standard library to suit its needs.

### Test framework included
Tweakflow comes with an extensible [spec framework](/tools.html#spec-runner) similar to mocha, rspec, etc. The [tests for the standard library](https://github.com/twineworks/tweakflow/tree/master/src/test/resources/spec/std) are implemented with it.

## Dynamically typed
Tweakflow is a dynamically typed language. Data types include booleans, strings, longs, doubles, datetimes and functions, as well as nestable lists and dictionaries. All data types have literal notations.

## All data is immutable
All values in tweakflow are immutable. It is always safe to pass values between user expressions and the host application without worrying about mutable state or object identity.

## All functions are pure
All functions in tweakflow are pure and free of observable side-effects. A tweakflow function, given the same arguments, will always return the same result. The host application handles all non-pure operations like file I/O.

## Embedded documentation and meta-data
Tweakflow supports documentation annotations as well as arbitrary meta-data on variables, libraries and modules. This feature supports interactive help as well as automated generation of project documentation.
