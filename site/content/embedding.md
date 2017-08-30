---
title: Embedding Guide
---

## Requirements
Tweakflow requires Java 8 or later.

## Getting tweakflow
You can get the tweakflow jar from the [releases page](https://github.com/twineworks/tweakflow/releases) or from maven central:
```xml
<dependency>
    <groupId>com.twineworks</groupId>
    <artifactId>tweakflow</artifactId>
    <version>{{< releaseTag >}}</version>
</dependency>
```

## Tweakflow values
Whenever the application is exchanging data with tweakflow code, it does so through immutable value objects of class [Value](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/Value.java). Variable values, function parameters, function return values, everything is a value in tweakflow.

### Creating values
The [Values](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/Values.java) class provides static singleton members for certain constants and factory methods for creating values of all types.

The `nil` value is represented by the static singleton value `Values.NIL`. Boolean `true` and `false` are represented by singletons `Values.TRUE` and `Values.FALSE`.

For all other cases Values provides the overloaded static factory method `make`.

Creating strings, longs, and doubles is straightforward. Just pass a String, long or double to the overloaded `Values.make` factory method.

```java
Value a = Values.make("foo");  // a string
Value b = Values.make(1);      // a long
Value c = Values.make(1.2d);   // a double
```

To create a datetime, first create a [DateTimeValue](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/DateTimeValue.java) using one of its constructors, and pass that to `Values.make`.

```java
Value now = Values.make(
  new DateTimeValue(ZonedDateTime.now())); // a datetime
```

To create a dict, first create a [DictValue](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/DictValue.java), which is a persistent data structure mapping string keys to value objects. Remember that DictValue objects are immutable and all manipulation methods yield a new object. Pass a DictValue to `Values.make`, to create a tweakflow dict.

```java
Value dict = Values.make(new DictValue()
    .put("foo", Values.make("hello"))
    .put("bar", Values.make(42L)));
```

To create a list, first create a [ListValue](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/ListValue.java), which is a persistent data structure. Remember that ListValue objects are immutable and all manipulation methods yield a new object. Pass a ListValue to `Values.make`, to create a tweakflow list.

```java
Values list = Values.make(new ListValue()
    .append(Values.make("hello"))
    .append(Values.make("world")));
```
To create a function, first create a [UserFunctionValue](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/UserFunctionValue.java) which in turn consists of a [FunctionSignature](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/FunctionSignature.java) and an implementation object implementing the  [UserFunction](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/UserFunction.java) tag interface as well as the arity interface matching the function's parameter count. See [Functions in Java](/reference.html#functions-in-java) for details, and the implementation of the standard library function [regex.matching](/modules/std.html#matching) for an [example](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/std/Regex.java#L62), which returns a function to the caller.

The following snipped creates a function that takes one string parameter named `x` with default value `"hello"`, and returns a boolean.
```java
Values.make(
    new UserFunctionValue(
        new FunctionSignature(Collections.singletonList(
            new FunctionParameter(0, "x", Types.STRING, Values.make("hello"))),
            Types.BOOLEAN),
        new MyAwesomeImplementation())); // implements UserFunction and Arity1UserFunction
```

### Inspecting values

A [Value](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/Value.java) object reports its `type`, which is one of the singleton objects on [Types](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/types/Types.java). It also has boolean `is{TypeName}` methods to determine the type.

Depending on the type of the value, call the method named after the type to retrieve the contained payload object. Strings are retrieved using `string`, lists are retrieved using `list`, and so on. Longs and doubles are retrieved using `longNum` and `doubleNum` respectively. Method names `long` and `double` are not permissible in Java.

It is an error to call the wrong payload function for the type of the value. Calling `string` for a value of type `long` will result in a runtime exception.

```java
Value v = obtainSomeValue();

if (v.isList()){
  ListValue listValue = v.list();
  // process...
}
else if (v.isDict()){
  DictValue dictValue = v.dict();
  // process...
}
else {
  throw new AssertionError("I need a list or a dict");
}
```

To get the literal notation of a tweakflow value, use [ValueInspector.inspect](https://github.com/twineworks/tweakflow/blob/releases/{{< releaseTag >}}/src/main/java/com/twineworks/tweakflow/lang/values/ValueInspector.java).

```java
String dump = ValueInspector.inspect(
    Values.make(new ListValue()
        .append(Values.make("hello"))
        .append(Values.make("world")))
);

System.out.println(dump);

// outputs: ["hello", "world"]
```

## Evaluating expressions
Evaluating expressions in an empty scope is just a function call. The following program just prints '3' to standard out.

```java
import com.twineworks.tweakflow.lang.TweakFlow;

public class SimpleEvaluation {

  public static void main(String[] args) {
    System.out.println(
        TweakFlow.evaluate("1+2").toString()
    );
  }

}
```
This type of simple evaluation happens in empty scope. There is no access to modules or library functions. The expression must be entirely self-contained.

## Evaluating a set of variables

## Evaluating a set of modules
Evaluating module files consists of the following steps:

  - Set up a load path so tweakflow knows where to look for imported modules.
  - Compile the main module. Any imports will be searched on the load path.
  - Supply initial values for any variables declared as `provided`.
  - Evaluate everything, or selectively just the modules/libraries/vars the application is interested in.

### Setting up the load path
The load path is a set of locations where tweakflow looks for imported modules. Each location can point to the file system, a resource path, or an in-memory map location.

Builders exist for the load path and individual locations to make construction easy.

The following creates a load path that makes the standard library available as `std` and also allows loading modules from the file system. The current working directory is placed on the load path, and import paths resolve relative to it.

```java
LoadPath loadPath = new LoadPath.Builder()
    .addStdLocation()
    .addCurrentWorkingDirectory()
    .build();
```
There are many options to specify load path locations, and tailor them to the application's needs.

### Compiling modules
Tweakflow starts compilation by loading an entry module, or multiple modules in case the application uses the global modules feature. Any imported modules are searched on the load path. Tweakflow returns a compiled set of modules wrapped in a runtime object. Compilation verifies the syntax, performs semantic analysis, and transforms the loaded modules into runtime structures that can be evaluated.

```java
Runtime runtime = TweakFlow.compile(loadPath, "user_module.tf");
```
A runtime object represents all loaded modules, which in turn contain libraries, and variables. It makes them accessible to the host application. The runtime is used to set the values of provided variables, to evaluate user variables, and to retrieve variable values.

The runtime offers all loaded modules as a map of internal keys to actual module objects. To learn the key of the main module, just supply its path to the `unitKey` method.

```java
Runtime.Module module = runtime.getModules().get(runtime.unitKey("user_module.tf"));
```

### Supplying values for provided variables
If the host application supplies any variables, it must set their values before evaluation of user expressions. Assuming the host application provides a module with a variable like this:

```tweakflow
library lib {
  provided long a;
}
```

The module supplies a handle to the contained library, which in turn supplies a handle to the variable. The host application can then set the provided value.

```java
Runtime.Var a = module.getLibrary("lib").getVar("a");
a.update(Values.make("Hello World"));
```
The call to update sets the value and triggers evaluation of any dependent variables. The application can update provided values at any time. Any dependent variables that reference them are updated automatically.

### Evaluating user variables
Modules, libraries and variables are available through the runtime object. The runtime itself as well as all contained entities have `evaluate` methods, which recursively evaluate all contents.

The value of a variable is available through its `getValue` method once the variable has been evaluated. A variable can be evaluated directly through the `Runtime.Var` object or indirectly when the module or library it resides in is evaluated.

The host application is free to decide when to evaluate user variables, and which subset of them, depending on the semantics of the application.

Assuming the following module is fetched from the runtime:

```tweakflow
library lib {
  a: 1
  b: a+2
}
```

Then the following code evaluates the module and retrieves the values of the variables.
```java
Runtime.Var varA = module.getLibrary("lib").getVar("a");
Runtime.Var varB = module.getLibrary("lib").getVar("b");

module.evaluate();

Value a = varA.getValue(); // 1
Value b = varB.getValue(); // 3
```
### Calling tweakflow functions

## Error handling
Tweakflow throws `LangException` whenever something goes irrecoverably wrong. There are three categories of errors that can happen: parse errors, compilation errors, or runtime errors. Parse errors indicate unrecognized syntax. Compilation errors occur when syntax is fine, but semantics don't hold up. Referencing variables that do not exist is a common compilation error. Runtime errors occur when tweakflow code throws errors during evaluation using the `throw` syntax. A LangException holds an error code and a message describing the error condition.

See com.twineworks.tweakflow.embedding.EvalExpressionWithStd for examples of handling all kinds of possible errors.



