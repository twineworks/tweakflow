---
title: Tools
---

# Language Tools

The tweakflow jar offers entry points for some language tools that can be executed standalone. An interactive REPL, a runner that evaluates all given modules and calls a main function, and a documentation tool that extracts doc and metadata from modules.

## Interactive REPL

To launch the REPL run:
```bash
$ java -jar tweakflow-{{< version >}}.jar repl
```
The acronym `repl` stands for 'read evaluate process loop'. You should see a prompt similar to this:

```text
tweakflow interactive shell    \? for help, \q to quit
std.tf>
```
You can quit out of the REPL by entering `\q`. To get help, enter `\?`. The help screen looks similar to this:

```text
usage: > COMMAND ...

interactive commands:
  COMMAND
    \help (\?)           show usage information
    \vars (\v)           print interactive variable definitions
    \edit (\e)           toggle multi-line entry
    \inspect (\i)        inspect memory space
    \load_path (\lp)     print load path
    \pwd                 print working directory
    \load (\l)           load a set of modules, removes interactive variables
    \meta (\m)           print meta data
    \doc (\d)            print doc
    \time (\t)           measure evaluation time
    ... snip ...
```
The commands are listed with a short description of what they do. To get more help for a particular command, just type it in with a `--help` argument.

To get help for the `\load` command:
```text
> \load --help
usage: > \load module [module ...]

positional arguments:
  module                 modules to load, first given module's scope is entered, relative paths are searched in load_path
```

### Evaluating expressions
Just type in an expression to have the REPL evaluate it:

```tweakflow
> 1+2
3

> "Hello".. " " .. "World"
"Hello World"
```

The prompt shows you which module context you are in. You can reference all names visible in the module you are in. By default this is the standard library context, so you can use standard library functions.

```tweakflow
std.tf> data.map([1, 2, 3], (x) -> x*x)
[1, 4, 9]

std.tf> strings.length("foo")
3
```

### Multi-line mode
The REPL accepts single line entry by default. If you want to use an expression that spans multiple lines, open multiline mode using `\e` and close it again using `\e`. The REPL indicates with a `*` in the prompt that it is in multi-line mode.

```tweakflow
std.tf> \e
std.tf* let {
std.tf*   a: 3
std.tf*   b: 4
std.tf* }
std.tf* a+b
std.tf* \e
7
std.tf>
```

### Defining variables
You can define interactive variables using `var_name: expression` syntax. You can then reference your variables in your expressions and definitions.

```tweakflow
> a: 3
3

> b: 4
4

> c: a+b
7
```

All variables are re-evaluated on each command, so changing a variable definition changes all dependent values.

```tweakflow
> x: 3
3

> y: x
3

> x: "foo"
"foo"

> y
"foo"
```

You can view your interactive variable definitions using the `\vars` command.

```tweakflow
> \vars
5 interactive variables defined
a: 3
b: 4
c: a+b
x: "foo"
y: x
```

You can view their current values using the `\inspect` command.

```tweakflow
> \inspect
a: 3
b: 4
c: 7
`$`: "foo"
x: "foo"
y: "foo"
```

The `$` variable is internally maintained by the REPL to hold the last evaluation result. You can only view it using `\inspect`. It cannot be referenced in your expressions.

### Inspecting memory contents
You can inspect the content of any loaded module, library or variable. Just pass a reference to the `\inspect` command. To view the contents of the `strings` library, for example:

```tweakflow
> \i strings
# library
  starts_with?: function
  upper_case: function
  length: function
  replace: function
  concat: function
  ends_with?: function
  substring: function
  index_of: function
  comparator: function
  split: function
  trim: function
  lower_case: function
  join: function
  chars: function
  last_index_of: function
```

Enter `\i --help` for more information about the inspect command.

### Viewing docs
Tweakflow has the ability to annotate all named entities with doc data. You can view any doc data using the `\doc` command. The standard module, libraries and functions are annotated with markdown documentation.

````text
> \doc strings
The strings library contains basic functions for text processing.

> \doc strings.length
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
````

### Loading modules
You can load a different module than the standard library through the `\load` command. Let's say you have a module file on your disk:

```tweakflow
# acme.tf

import data, regex, strings from 'std'

alias strings.replace as replace
alias data.map as map
alias data.slices as slices
alias strings.join as join
alias strings.upper_case as upper_case

library product_codes {

  remove_whitespace:
    regex.replacing('\s+', "") # function that removes any whitespace from a string

  normalize: (string x) ->
    ->> (x)
        # remove whitespace, "f-oo bar" -> "f-oobar"
        (x) -> remove_whitespace(x),

        # remove any dashes # "f-oobar" -> "foobar"
        (x) -> replace(x, "-", ""),

        # split to a list of blocks of up to 4 chars "foobar" -> ["foob", "ar"]
        (x) -> let {
                 # "foobar" -> [["f", "o", "o", "b"], ["a", "r"]]
                 blocks: slices(x as list, 4)
               }
               # [["f", "o", "o", "b"], ["a", "r"]] -> ["foob", "ar"]
               map(blocks, (block) -> join(block)),

        # place dashes between blocks converting to single string ["foob", "ar"] ->   "foob-ar"
        (blocks) -> join(blocks, "-"),

        # upper case all characters "foob-ar" -> "FOOB-AR"
        upper_case
}
```

Load it in the REPL:
```tweakflow
> \load acme.tf
acme.tf>
```
You are now in the context of the acme module, and can reference all names in this module.

```tweakflow
acme.tf> product_codes.normalize("ah-78277-Nz")
"AH78-277N-Z"
```

### Command line arguments
The REPL accepts a number of command line arguments that influence which modules are initially loaded, and which directories are placed on the load path. To learn more, run:

```bash
$ java -jar tweakflow-{{< version >}}.jar repl --help
```

## Runner
The runner loads a module and calls a main function with some arguments you supply on the command line. It prints the return value back to the console.

Run the following command to view the command line options.
```bash
$ java -jar tweakflow-{{< version >}}.jar run --help
```

The module(s) to load are given as positional arguments. You can pass `std` to load the standard module.

The main function to call is given using the `-m` switch in `library_name.var_name` format. It is a reference in module scope of the first passed module.

Arguments are given in order, using a series of `-a` and `-ea` switches. The `-a` switch takes the argument verbatim and turns it into a tweakflow string. The `-ea` switch treats the argument as an expression that is evaluated first, before it is passed on to the function.

### Passing string arguments
The following invocation calls `strings.length("foo")`:
```bash
$ java -jar tweakflow-{{< version >}}.jar run -m strings.length -a foo std
3
```
Since the argument is a string, it is passed using the `-a` switch. Please note that passing the argument using the `-ea` switch would fail, since `foo` would simply be a reference to an undefined variable. If you wish to pass strings using `-ea`, you have to pass a proper literal string as in:

```bash
$ java -jar tweakflow-{{< version >}}.jar run -m strings.length -ea "'foo'" std
3
```
Shell escaping can make it tricky to know exactly what is passed. It is therefore recommended to avoid the complexity and pass strings using `-a`.

### Passing expression arguments
The following invocation calls `data.map([1, 2, 3, 4], (x) -> x*x)`:
```bash
$ java -jar tweakflow-{{< version >}}.jar run -m data.map -ea '[1, 2, 3, 4]' -ea '(x) -> x*x' std
[1, 4, 9, 16]
```
This time both arguments need to be evaluated to a list and function first. Therefore the `-ea` switch is used both times.

### Loading custom modules
You can load any module on disk. Let's say you have the following module available:
```tweakflow
# acme.tf

import data, regex, strings from 'std'

alias strings.replace as replace
alias data.map as map
alias data.slices as slices
alias strings.join as join
alias strings.upper_case as upper_case

library product_codes {

  remove_whitespace:
    regex.replacing('\s+', "") # function that removes any whitespace from a string

  normalize: (string x) ->
    ->> (x)
        # remove whitespace, "f-oo bar" -> "f-oobar"
        (x) -> remove_whitespace(x),

        # remove any dashes # "f-oobar" -> "foobar"
        (x) -> replace(x, "-", ""),

        # split to a list of blocks of up to 4 chars "foobar" -> ["foob", "ar"]
        (x) -> let {
                 # "foobar" -> [["f", "o", "o", "b"], ["a", "r"]]
                 blocks: slices(x as list, 4)
               }
               # [["f", "o", "o", "b"], ["a", "r"]] -> ["foob", "ar"]
               map(blocks, (block) -> join(block)),

        # place dashes between blocks converting to single string ["foob", "ar"] ->   "foob-ar"
        (blocks) -> join(blocks, "-"),

        # upper case all characters "foob-ar" -> "FOOB-AR"
        upper_case
}
```

To load it, and run `product_codes.normalize("ksh765fg25ff")`:

```bash
$ java -jar tweakflow-{{< version >}}.jar run -m product_codes.normalize -a ksh765fg25ff acme.tf
"KSH7-65FG-25FF"
```

## Metadata extraction

The tweakflow language allows annotating modules, libraries, and variables with documentation and custom meta data. The `doc` utility makes it possible to extract that information in a structured way.

Let's work with the following module file:
```tweakflow
# module foo.tf
doc
~~~
This is documentation at the module level.
~~~
meta {
  :title       "foo"
  :description "Description of the module"
  :version     "4.2"
}
module

# library bar
doc 'This is documentation for library bar.'

meta {
  :author "John Doe et al."
  :since  "2.3"
}

library bar {

  # function baz
  doc 'This is documentation for function baz.'

  meta {
    :author "John Doe"
    :date 2017-03-12T
  }

  function baz: (x) -> x*x
}
```

### Command overview
You can view all command line switches by running the following:
```bash
$ java -jar tweakflow-{{< version >}}.jar doc --help
```
The next sections provide a walkthrough for the most common options.

### Printing metadata

The following invokes `doc`, passing in the module to extract metadata from.
```bash
$ java -jar tweakflow-{{< version >}}.jar doc foo.tf
```

By default the `doc` utility prints the structure of available meta data:

```text
{
  :node "module",
  :global false,
  :path "/path/to/foo.tf",
  :meta {
    :version "4.2",
    :title "foo",
    :description "Description of the module"
  },
  :components [{
    :node "library",
    :meta {
      :author "John Doe et al.",
      :since "2.3"
    },
    :name "bar",
    :vars [{
      :node "var",
      :meta {
        :author "John Doe",
        :date 2017-03-12T00:00:00Z@UTC
      },
      :name "baz",
      :doc "This is documentation for function baz.",
      :expression "(x) -> x*x",
      :type "function"
    }],
    :export false,
    :doc "This is documentation for library bar."
  }],
  :doc "This is documentation at the module level.",
  :file "foo.tf"
}
```
Please note that the output is a valid tweakflow dict value.

### Transforming metadata
The `doc` utility can pass the meta data of a module to a sequence of transformer functions. The most common task for a transformer is to generate some human-readable document format from the given structure.

A transformer is a special tweakflow module that has a library named `transform` which in turn has a variable named `transform`. This variable holds a function accepting above structure as its sole argument. The return value of the function is the output of the transformer.

You supply transformers using the `-t` switch. If you supply more than one transformer, they are chained such that the output of the first transformer becomes the argument to the second transformer, and so on.

Let's supply a basic transformer that just generates a one-sentence summary of the module.

```tweakflow
# transformer.tf
import data, strings from "std"

alias data.size as size
alias data.reduce as reduce

library transform {

  transform: (dict x) -> string
    "Module "
      ..x[:path]
      .." contains "
      ..libs_in_module(x)
      .." libraries, and a total of "
      ..vars_in_module(x)
      .." variables."

  libs_in_module: (dict m) -> long
    size(m[:components])

  vars_in_module: (dict m) -> long
    let {
      libs: m[:components]
    }
    reduce(libs, 0, (a, lib) -> a+vars_in_lib(lib))

  vars_in_lib: (dict lib) -> long
    size(lib[:vars])
}
```

Let's run this transformer over the standard library:

```bash
$ java -jar tweakflow-{{< version >}}.jar doc -t transformer.tf std
Module std.tf contains 8 libraries, and a total of 159 variables.
```

A more sophisticated transformer might generate markdown, XML, or HTML documentation for the module.

### Processing more than one module
You can pass more than one module to `doc`, and they are processed in order.

```bash
$ java -jar tweakflow-{{< version >}}.jar doc -t transformer.tf std foo.tf
Module std.tf contains 8 libraries, and a total of 159 variables.
Module /path/to/foo.tf contains 1 libraries, and a total of 1 variables.
```

### Saving the output
If you're supplying relative module paths to `doc`, you can ask to save each module output to a base output directory, preserving the relative paths given. You can also specify a file extension to use, which is handy if your transformer generates a well known file format. The default extension is `txt`.

Assuming foo.tf is in the current directory, run:
```bash
$ java -jar tweakflow-{{< version >}}.jar doc -t transformer.tf --output docs --output-extension md std.tf foo.tf
```

It generates a `docs` directory, placing the following files in there:

`docs/std.tf.md`
```text
Module std.tf contains 8 libraries, and a total of 159 variables.
```

`docs/foo.tf.md`
```text
Module /path/to/foo.tf contains 1 libraries, and a total of 1 variables.
```
