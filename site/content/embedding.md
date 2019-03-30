---
title: Embedding Guide
---

## Requirements
Tweakflow requires Java 8 or later. Builds are tested against JDK 8 and JDK 11.

## Getting tweakflow
You can get the tweakflow jar from the [releases page](https://github.com/twineworks/tweakflow/releases) or from maven central:
```xml
<dependency>
    <groupId>com.twineworks</groupId>
    <artifactId>tweakflow</artifactId>
    <version>{{< version >}}</version>
</dependency>
```

## Tweakflow values
Whenever the application is exchanging data with tweakflow code, it does so through immutable value objects of class [Value](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Value.java). Variable values, function parameters, function return values, everything is a value in tweakflow.

### Creating values
The [Values](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Values.java) class provides static singleton members for certain constants and factory methods for creating values of all types.

The `nil` value is represented by the static singleton value `Values.NIL`. Boolean `true` and `false` are represented by singletons `Values.TRUE` and `Values.FALSE`.

For all other cases Values provides the overloaded static factory method `make`.

Creating strings, longs, and doubles is straightforward. Just pass a String, long or double to the overloaded `Values.make` factory method.

```java
Value a = Values.make("foo");  // a string
Value b = Values.make(1);      // a long
Value c = Values.make(1.2d);   // a double
```

To create a datetime, first create a [DateTimeValue](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/DateTimeValue.java) using one of its constructors, and pass that to `Values.make`.

```java
Value now = Values.make(
  new DateTimeValue(ZonedDateTime.now())); // a datetime
```

To create a dict, first create a [DictValue](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/DictValue.java), which is a persistent data structure mapping string keys to value objects. Remember that DictValue objects are immutable and all manipulation methods yield a new object. Pass a DictValue to `Values.make`, to create a tweakflow dict.

```java
Value dict = Values.make(new DictValue()
    .put("foo", Values.make("hello"))
    .put("bar", Values.make(42L)));
```

To create a list, first create a [ListValue](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/ListValue.java), which is a persistent data structure. Remember that ListValue objects are immutable and all manipulation methods yield a new object. Pass a ListValue to `Values.make`, to create a tweakflow list.

```java
Values list = Values.make(new ListValue()
    .append(Values.make("hello"))
    .append(Values.make("world")));
```
To create a function, first create a [UserFunctionValue](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/UserFunctionValue.java) which in turn consists of a [FunctionSignature](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/FunctionSignature.java) and an implementation object implementing the  [UserFunction](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/UserFunction.java) tag interface as well as the arity interface matching the function's parameter count. See [Functions in Java](/reference.html#functions-in-java) for details, and the implementation of the standard library function [regex.matching](/modules/std.html#matching) for an [example](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/std/Regex.java#L62), which returns a function to the caller.

The following snippet creates a function that takes one string parameter named `x` with default value `"hello"`, and returns a boolean.
```java
Values.make(
    new UserFunctionValue(
        new FunctionSignature(Collections.singletonList(
            new FunctionParameter(0, "x", Types.STRING, Values.make("hello"))),
            Types.BOOLEAN),
        new MyAwesomeImplementation())); // implements UserFunction and Arity1UserFunction
```

### Inspecting values

A [Value](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/Value.java) object reports its `type`, which is one of the singleton objects on [Types](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/types/Types.java). It also has boolean `is{TypeName}` methods to determine the type.

Depending on the type of the value, call the method named after the type to retrieve the contained payload object. Strings are retrieved using `string`, lists are retrieved using `list`, and so on. Longs and doubles are retrieved using `longNum` and `doubleNum` respectively. Method names `long` and `double` are not allowed in Java.

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

To get the literal notation of a tweakflow value, use [ValueInspector.inspect](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/values/ValueInspector.java).

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
The simplest case of embedding tweakflow is to evaluate independent, self-contained expressions in an empty scope. This is just a call to [Tweakflow](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/TweakFlow.java).evaluate.

```java
TweakFlow.evaluate("1+2"); // returns 3
```
The scope is empty, which means there is no access to any modules including the standard library. The expression must be entirely self-contained.

To give the expression some context, but not go all the way to maintaining a complete runtime, an application might embed a user expression into a let construct that defines some variables.

The following snippet puts variables `first_name` and `last_name` into scope. The expression is supposed to evaluate to a greeting line.
```java
// exp stands in for a user-supplied expression
String exp = "if (first_name && last_name) then \n" +
    "'Dear ' .. first_name .. ' '.. last_name\n" +
    "else\n" +
    "'Dear customer'";

// make sure exp parses as an expression first
ParseResult parseResult = TweakFlow.parse(exp);

if (parseResult.isSuccess()){

  String firstName = "Mary";
  String lastName = "Poppins";

  // construct the full expression to evaluate
  String code = "let {" +
      "  first_name: \""+ LangUtil.escapeString(firstName)+"\";" +
      "  last_name: \""+ LangUtil.escapeString(lastName)+"\";" +
      "} "+exp;

  TweakFlow.evaluate(code); // returns "Dear Mary Poppins"
}
```
### Examples
See these [tests](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/test/java/com/twineworks/tweakflow/embedding/EvalExpressionInEmptyScopeTest.java) for working examples of expression evaluation.

## Evaluating a set of variables

If the application's use-case is to have users define a table of variables, then the [VarTable](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/util/VarTable.java) helper class supports implementing it efficiently. It creates an in-memory module with a library containing user variables, compiles to a runtime, and relates any compilation error to the offending variable. The application can provide a prologue with any imports and aliases it wants to be available in scope.

```java
VarTable table = new VarTable.Builder()
    .setPrologue(
            // provided by the application
            "alias customer.first_name as first_name;\n" +
            "alias customer.last_name as last_name;\n" +
            "library customer {\n" +
            "  provided first_name;\n" +
            "  provided last_name;\n" +
            "}"
    )
    // provided by the user
    .addVar("greeting", greetingExp)
    .addVar("avatar", avatarExp)
    .build();

Runtime runtime = table.compile();
```

### Examples

See the class [test](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/test/java/com/twineworks/tweakflow/util/VarTableTest.java) for examples  demonstrating usage and error handling.

The [LazilyProvidedVars](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/LazilyProvidedVars.java) sample compiles a var table and provides only variables that are actually referenced.

There is a [demo application](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/VarTableEvaluation.java) which uses a var table. It asks the user for some expressions and verifies them.

You can run it using:
```bash
java -cp tweakflow-{{< version >}}.jar com.twineworks.tweakflow.examples.VarTableEvaluation
```

An invocation might look like:

```text
Given a rectangle with sides of length a and b.
What is the formula to calculate the circumference?
circumference:
2*a+2*b
And the formula for calculating surface area?
area: a*b
Thanks. Checking answer...

Congratulations. The formulas seem to be correct.
```

## Evaluating modules

If users need the standard library, or need the ability to define variables, libraries, or even modules themselves, then the application must generate and compile the set of modules involved.

The result is a [runtime](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/runtime/Runtime.java) object that provides handles to all compiled modules, libraries, and variables. The application then supplies values for any variables it [provides](/reference.html#variables), and evaluates any variables it is interested in. The host application can determine which provided variables are actually referenced, so it can omit providing values that are not needed.

For the purposes of discussion, consider the following module.

```tweakflow
# user_module.tf

import core, data, math, strings from 'std'

library customer {
  provided first_name;
  provided last_name;
}

library user {
  greeting: if customer.first_name && customer.last_name
              "Hello #{customer.first_name} #{customer.last_name}"
            else
              "Dear anonymous"
}
```

The steps to compile a set of modules are:

  - Set up a load path so tweakflow knows where to look for imported modules.
  - Compile the modules. Any imports will be searched on the load path.

A module might be a resource, a file, or an in-memory object. The load path contains [LoadPathLocations](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/load/loadpath/LoadPathLocation.java) specifying where tweakflow will look for modules. Assuming user_module.tf was generated from user input and is just a string, the application must put it into a memory location, and place that location into the load path.

```java
MemoryLocation memLocation = new MemoryLocation.Builder()
            .add("user_module.tf", moduleText)
            .build();

LoadPath loadPath = new LoadPath.Builder()
    .addStdLocation() // ensure importing 'std' imports the standard library
    .add(memLocation) // memory location with "user_module.tf"
    .build();

Runtime runtime = TweakFlow.compile(loadPath, "user_module.tf");
```

To interact with a compiled runtime:

  - Supply values for any variables declared as `provided`.
  - Evaluate everything, or selectively just specific modules, libraries, or vars.

```java
// get the module out of the runtime
Runtime.Module module = runtime.getModules().get(runtime.unitKey("user_module.tf"));

// set customer.first_name, and customer.last_name provided vars
Runtime.Var firstName = module.getLibrary("customer").getVar("first_name");
Runtime.Var lastName = module.getLibrary("customer").getVar("last_name");
firstName.update(Values.make("Mary"));
lastName.update(Values.make("Poppins"));

// get a handle to user-supplied variable user.greeting
Runtime.Var greeting = module.getLibrary("user").getVar("greeting");

// evaluate greeting
greeting.evaluate();

// retrieve whatever greeting evaluated to
Value userGreeting = greeting.getValue();
```

The application can continue updating provided variables and any dependent variables are re-evaluated automatically.

Every variable update triggers a re-evaluation of dependent variables. The runtime object has `updateVars` methods that atomically update multiple variables at once, which reduces unnecessary evaluation overhead, and avoids temporary inconsistencies.

```java
for (Customer c : myCustomerCollection){
  runtime.updateVars(
    firstName, Values.make(c.getFirstName()),
    lastName, Values.make(c.getLastName())
  );
  String userGreeting = greeting.getValue().string();
}
```

### Examples
The [ModuleEvaluation](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/ModuleEvaluation.java) sample compiles and calls into a set of modules.

The [LazilyProvidedVars](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/LazilyProvidedVars.java) sample compiles a var table and provides only variables that are actually referenced by user-supplied expressions.

This [unit test](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/test/java/com/twineworks/tweakflow/embedding/EvalProvidedVarsTest.java) demonstrates supplying values for provided variables.

## Calling user functions
Users can provide tweakflow functions to the host application. The application can call them through the runtime using `call` on a runtime var object that evaluated to a function.

```java
// get a handle on time_format.format which evaluated to a function
Runtime.Var format = module.getLibrary("time_format").getVar("format");

// get now() as per local timezone
Value now = Values.make(new DateTimeValue(ZonedDateTime.now()));

// result of calling format with now as argument
String formattedDate = format.call(now).string();
```

In case the application wants to call a function in a tight loop, it is more efficient to create a callsite first, which can cache some information involved in calling a function.

```java
// some constant overhead creating the callsite
Arity1CallSite callSite = format.arity1CallSite();

for(int i=0;i<1000;i++){
  // less overhead per call when performing multiple calls
  System.out.println("var callsite: "+callSite.call(now).string());
}
```

If the function value is not the current value of a variable, but has been obtained by the application in some other way, there is no var handle to relate the call back into the runtime. In these cases the application can obtain a call context from the runtime using `createCallContext` to call into any function value, regardless of its source.

```java
Value f = getSomeFunctionValue();
// calling a function: variant 3, use runtime call context
CallContext callContext = runtime.createCallContext();
System.out.println("runtime call context: "+ callContext.call(f, now));
```

### Examples
The [CallingFunctions](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/CallingFunctions.java) sample contains demonstrations of all above techniques.

## Error handling
Tweakflow throws [LangExceptions](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/errors/LangException.java) whenever something goes wrong.

There are three categories of errors that can happen: parse errors, compilation errors, and runtime errors. Parse errors indicate unrecognized syntax. Compilation errors occur when syntax is fine, but semantics are invalid. Referencing undefined variables, or defining variables more than once are common compilation errors. Runtime errors occur when tweakflow code throws errors during evaluation using the [throw](/reference.html#throwing-errors) syntax.

An exception holds an [error code](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/errors/LangError.java) and a message describing the error condition. You can get the value that was thrown by calling `toErrorValue`. Calling `getDigestMessage` returns a detailed error message that includes stack trace information. The exception usually contains a [SourceInfo](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/lang/parse/SourceInfo.java) object which gives the exact location of the error condition. Note however that source info may be `null`, in case the error happens in a context where no source information is available.

### Examples
See these [test files](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/test/java/com/twineworks/tweakflow/embedding) for examples of handling errors. Each test file contains specific tests for error handling.

## Limiting evaluation time

The host application might wish to limit the evaluation time of user code. A good way to achieve this goal is to evaluate user code in a separate thread. The tweakflow interpreter reacts to thread interruption by throwing an exception.

The [LimitingExecTime](https://github.com/twineworks/tweakflow/blob/{{< gitRef >}}/src/main/java/com/twineworks/tweakflow/examples/LimitingExecTime.java) sample evaluates a set of expressions, each taking exponentially longer to evaluate than the next. Each expression is evaluated within an enforced time limit, so at some point the evaluations start timing out, and the application interrupts the evaluation thread.



