# The MIT License (MIT)
#
# Copyright (c) 2019 Twineworks GmbH
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

doc
~~~
The `std/spec` module contains libraries useful for creating test suites.
~~~

module;

import data, math, regex from 'std';

export assert.expect as expect;
export assert.assert as assert;
export assert.expect_error as expect_error;

export nodes.describe as describe;
export nodes.it as it;
export nodes.subject as subject;
export nodes.before as before;
export nodes.after as after;

alias data.size as size;
alias data.delete as delete;
alias data.reduce_until as reduce_until;


doc
~~~
Utilities used internally.
~~~

library util {

doc
~~~
`(list xs, list ys) -> boolean`

Returns `true` if `xs` and `ys` are both non-nil and contain the same items. Item order is not relevant.

Returns `false` otherwise.

```
> util.permutations?([1,2,3], [2,3,1])
true

> util.permutations?([1,2], [1,3])
false

> util.permutations?(nil, nil)
false
```
~~~
  permutations?: (list xs, list ys) -> boolean
    (xs != nil && ys != nil) &&
    size(xs) === size(ys) &&
    let {
      state: {:xs xs, :ys ys};
      check: reduce_until(
        xs,
        state,
        (state) -> state[:abort],
        (state, x, i) ->
          let {
            idx: data.index_of(state[:ys], x);
            abort: idx === -1;
          }
          if abort
            {:abort true}
          else
            {
              :xs delete(state[:xs], i),
              :ys delete(state[:ys], idx)
            }
      );
    }
    !check[:abort] && check[:ys] === [];

}

doc
~~~
The library contains functions useful for defining assertions.
~~~
library assert {

doc
~~~
`(function x, function f) -> boolean`

A function that helps asserting facts about thrown errors.

If `f` is `nil`, `to.be_true()` is used implicitly.

It calls `x()`, expects it to throw, and expects the thrown value to satisfy matcher function `f`.

Returns `true` if `x()` throws and the thrown value satisfies matcher function `f`, throws otherwise.

```
> assert.expect_error(() -> 1//0, to.have_code("DIVISION_BY_ZERO"))
true

# x() does not throw
> assert.expect_error(() -> nil, to.be_nil())
ERROR:
  ...

# thown value is does not satisfy matcher
> assert.expect_error(() -> throw "foo", to.be("bar"))
ERROR:
  ...
```
~~~
  function expect_error: (function x, function f) ->
    if ((try
          if x() then "not_thrown" else "not_thrown"
        catch error
          expect(error, f, _stack_offset: 5))
      === "not_thrown") then
      let {
        # get caller information from forced trace
        call_loc: try throw "err" catch _, trace trace[:stack, 2];
      }
      throw {
        :code "ERROR_EXPECTED",
        :location call_loc
      }
    else
      true;

doc
~~~
`(x, function f) -> boolean`

If `f` is `nil`, `to.be_true()` is used implicitly.

Returns `true` if `x` satisfies matcher function `f`, throws otherwise.

```
> assert.expect(true, to.be_true())
true

> assert.expect(false, to.be_true())
ERROR:
  ...
```
~~~

  function expect: (x, function f, _stack_offset=3) ->
    let {
      result: (f default to.be_true())(x);
      semantic: result[:semantic];
      success: result[:success];
      expected: result[:expected];
    }
    if !success
      let {
        # get caller information from forced trace
        call_loc: try throw "err" catch _, trace trace[:stack, _stack_offset];
      }
      throw {
        :code "ASSERTION_ERROR",
        :x x,
        :semantic semantic,
        :expected expected,
        :location call_loc
      }
    else
      true
    ;

doc
~~~
Alias for [expect](#assert-expect) that might be more readable in cases where the matcher is omitted and `to.be_true()` is used implicitly.

```
> assert.assert(1.0 == 1)
true

> assert.assert(1.0 === 1)
ERROR:
  ...
```
~~~
  function assert: expect;

}

doc
~~~
Functions that help construct spec nodes.
~~~
library nodes {

doc
~~~
`(dict effect, string name, any context) -> dict`

Returns a `before` spec node with the given attributes.
~~~
  function before: (dict effect, string name, any context) -> {
    :type "before",
    :name name default "before",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context
  };

doc
~~~
`(dict effect, string name, any context) -> dict`

Returns an `after` spec node with the given attributes.
~~~

  function after: (any effect, string name, any context) -> {
    :type "after",
    :name name default "after",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context
  };

doc
~~~
`(string name, list spec, any context, any tags) -> dict`

Returns a `describe` spec node with the given attributes.

If `tags` is a list, it is used directly.\
If `tags` is a dict, keys containing values casting to boolean `true` are used.
~~~
  function describe: (string name, list spec, any context, any tags) -> {
    :type 'describe',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context,
    :tags tags
  };

doc
~~~
`(string name, function spec, any context, any tags) -> dict`

Returns an `it` spec node with the given attributes.

If `tags` is a list, it is used directly.\
If `tags` is a dict, keys containing values casting to boolean `true` are used.
~~~
  function it: (string name, function spec, any context, any tags) -> {
    :type 'it',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context,
    :tags tags
  };

doc
~~~
`(any data, function transform, dict effect, any context) -> dict`

Returns a subject spec node with the given attributes.

Specify non-nil `transform` to create a `subject_transform` node.

Specify non-nil `effect` to create a `subject_effect` node.

If neither `transform`, nor `effect` are specified, a regular `subject` node is created.
~~~
  function subject: (any data, function transform, dict effect, any context) ->

    if (transform != nil)
      {
        :type 'subject_transform',
        :transform transform,
        :at (try throw "err" catch _, trace trace[:stack, 1]),
        :context context
      }
    if (effect != nil)
      {
        :type 'subject_effect',
        :effect effect,
        :at (try throw "err" catch _, trace trace[:stack, 1]),
        :context context
      }
    else
      {
        :type 'subject',
        :data data,
        :at (try throw "err" catch _, trace trace[:stack, 1]),
        :context context
      }
  ;

}

doc
~~~
The `to` library contains matcher functions useful in conjunction with the [expect](#assert-expect) function.
~~~
export library to {

doc
~~~
`(expected) -> (err) -> boolean`

Matcher function that is satisfied if the given `err` is a dict with the expected value at key `:code`.

```
> assert.expect_error(() -> 1//0, to.have_code("FOO"))
ERROR:
  ...
```

~~~
  have_code: (expected) -> (err) ->
    {
      :semantic "to have code",
      :expected expected,
      :success err is dict && err[:code] === expected
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is equal to `expected` using the `==` comparison operator.

```
> assert.expect(1, to.equal(1.0))
true

> assert.expect(1, to.equal(2.0))
ERROR:
  ...
```
~~~
  equal: (expected) -> (x) ->
    {
      :semantic "to equal",
      :expected expected,
      :success x == expected
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is not equal to `expected` using the `!=` comparison operator.

```
> assert.expect(1, to.not_equal(2.0))
true

> assert.expect(1, to.not_equal(1.0))
ERROR:
  ...
```
~~~
  not_equal: (expected) -> (x) ->
    {
      :semantic "to not equal",
      :expected expected,
      :success x != expected
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is equal to `expected` using the `===` comparison operator.

```
> assert.expect(1, to.be(1))
true

> assert.expect(1, to.be(1.0))
ERROR:
  ...
```
~~~
  be: (expected) -> (x) ->
    {
      :semantic "to be",
      :expected expected,
      :success x === expected
    };



doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is not equal to `expected` using the `!==` comparison operator.

```
> assert.expect(1, to.not_be(1.0))
true

> assert.expect(1, to.not_be(1))
ERROR:
  ...
```
~~~
  not_be: (expected) -> (x) ->
    {
      :semantic "to not be",
      :expected expected,
      :success x !== expected
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a string and matches the regular expression `expected` in its entirety.

```
> assert.expect("12345", to.match_regex('\d+'))
true

> assert.expect("-12345", to.match_regex('\d+'))
ERROR:
  ...
```
~~~
  match_regex: (string expected) -> (x) ->
    {
      :semantic "to match regular expression",
      :expected expected,
      :success x is string && regex.matching(expected)(x)
    };

doc
~~~
Alias for [match_regex](#to-match_regex)
~~~
  be_like: match_regex;

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` and `expected` are both numeric and `x` > `expected` holds true.

```
> assert.expect(10, to.be_greater_than(5))
true

> assert.expect(10, to.be_greater_than(20))
ERROR:
  ...
```
~~~
  be_greater_than: (expected) -> (x) ->
    {
      :semantic "to be greater than",
      :expected expected,
      :success (x is double || x is long || x is decimal) && (expected is double || expected is long || expected is decimal) && x > expected
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` and `expected` are both numeric and `x` < `expected` holds true.

```
> assert.expect(10, to.be_less_than(20))
true

> assert.expect(10, to.be_less_than(5))
ERROR:
  ...
```
~~~
  be_less_than: (expected) -> (x) ->
    {
      :semantic "to be less than",
      :expected expected,
      :success (x is double || x is long || x is decimal) && (expected is double || expected is long || expected is decimal)  && x < expected
    };

doc
~~~
```
(
  expected_low,
  expected_high,
  boolean low_inclusive=true,
  boolean high_inclusive=true
) -> (x) -> boolean
```

Matcher function that is satisfied if `x`, `expected_low` and `expected_high` are all numeric, and depending on
inclusion options the corresponding condition holds:

| low_inclusive | high_inclusive | condition
|---------------|----------------|-------------------------------------
| `true`        | `true`         | `expected_low <= x <= expected_high`
| `true`        | `false`        | `expected_low <= x <  expected_high`
| `false`       | `true`         | `expected_low  < x <= expected_high`
| `false`       | `false`        | `expected_low  < x <  expected_high`

```
> assert.expect(10, to.be_between(0, 20))
true

> assert.expect(10, to.be_between(10, 20))
true

> assert.expect(10, to.be_between(10, 20, low_inclusive: false))
ERROR:
  ...

> assert.expect(5, to.be_between(10, 20))
ERROR:
  ...
```

~~~
  be_between: (expected_low, expected_high, boolean low_inclusive=true, boolean high_inclusive=true) -> (x) ->
    {
      :semantic "to be between",
      :expected [expected_low, expected_high],
      :success (expected_low is long || expected_low is double || expected_low is decimal) &&
               (expected_high is long || expected_high is double || expected_high is decimal) &&
               (x is long || x is double || x is decimal) &&
               (if low_inclusive (expected_low <= x) else (expected_low < x)) &&
               (if high_inclusive (x <= expected_high) else (x < expected_high))
    };


doc
~~~
`(double expected, double precision=1e-15) -> (x) -> boolean`

Matcher function that is satisfied if `x` is numeric and within `precision` distance of `expected`.

```
> assert.expect(3.1415, to.be_close_to(math.pi, 0.001))
true

> assert.expect(3.1415, to.be_close_to(math.pi, 0.0000001))
ERROR:
  ...
```

~~~
  be_close_to: (double expected, double precision=1e-15) -> (x) ->
    {
      :semantic "to be within #{precision} of",
      :expected expected,
      :success
        (x is long || x is double || x is decimal) &&
        (!math.NaN?(expected)) &&
        (!math.NaN?(x)) &&
        (!math.NaN?(precision)) &&
        (
          (precision == Infinity) ||
          (math.abs(expected-x) <= precision)
        )
    };

doc
~~~
`(list expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a non-nil list containing the same items as `expected`. Order does not matter.

```
> assert.expect([1,2,3], to.be_permutation_of([2,3,1]))
true

> assert.expect([1,2,3], to.be_permutation_of([2,3,2]))
ERROR:
  ...
```
~~~
  be_permutation_of: (list expected) -> (x) ->
    {
      :semantic "to be a permutation of",
      :expected expected,
      :success x is list && util.permutations?(expected, x),
    };

doc
~~~
`(dict expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a non-nil dict that has all keys and associated values of `expected`.

Additional keys may be present in `x`.
```
> assert.expect({:a 1, :b 2}, to.be_superset_of({:a 1, :b 2}))
true

> assert.expect({:a 1, :b 2}, to.be_superset_of({:a 1}))
true

> assert.expect({:a 1, :b 2}, to.be_superset_of({:a 2}))
ERROR:
  ...
```
~~~
  be_superset_of: (dict expected) -> (x) ->
    {
      :semantic "to be a superset of",
      :expected expected,
      :success
        x is dict &&
          data.all?(
            data.keys(expected),
            (k) -> data.has?(x, k) && x[k] === expected[k]
          )
    };

doc
~~~
`(dict expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a non-nil dict whose keys and associated values all appear in `expected`.

`expected` may have keys and values that are not in `x`.
```
> assert.expect({:a 1, :b 2}, to.be_subset_of({:a 1, :b 2}))
true

> assert.expect({:a 1}, to.be_subset_of({:a 1, :b 2}))
true

> assert.expect({:a 1}, to.be_subset_of({:a 2, :b 2}))
ERROR:
  ...
```
~~~

  be_subset_of: (dict expected) -> (x) ->
    {
      :semantic "to be a subset of",
      :expected expected,
      :success
        x is dict &&
          data.all?(
            data.keys(x),
            (k) -> data.has?(expected, k) && x[k] === expected[k]
          )
    };

doc
~~~
`(list expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is one of the values in `expected`.

```
> assert.expect(2, to.be_one_of([1,2,3]))
true

> assert.expect(2, to.be_one_of([0,1]))
ERROR:
  ...
```
~~~

  be_one_of: (list expected) -> (x) ->
    {
      :semantic "to be one of",
      :expected expected,
      :success data.contains?(expected, x),
    };

doc
~~~
`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a list or dict containing the value `expected`.

```
> assert.expect([1,2,3], to.contain(2))
true

> assert.expect({:a 1, :b 2, :c 3}, to.contain(2))
true

> assert.expect([], to.contain(2))
ERROR:
  ...
```
~~~
  contain: (expected) -> (x) ->
    {
      :semantic "to contain",
      :expected expected,
      :success
        (x is list || x is dict) && data.contains?(x, expected)
    };

doc
~~~
`(list expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a list or dict containing all values in `expected`.

```
> assert.expect([1,2,3], to.contain_all([1,3]))
true

> assert.expect({:a 1, :b 2, :c 3}, to.contain_all([1,2,3]))
true

> assert.expect([], to.contain_all([]))
true

> assert.expect({:a 1, :b 2, :c 3}, to.contain_all([1,4]))
ERROR:
  ...
```
~~~
  contain_all: (list expected) -> (xs) ->
    {
      :semantic "to contain all",
      :expected expected,
      :success
        (xs is list || xs is dict) &&
          data.all?(
            expected,
            (e) -> data.contains?(xs, e)
          )
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is `nil`.

```
> assert.expect(nil, to.be_nil())
true

> assert.expect(0, to.be_nil())
ERROR
  ...
```
~~~
  be_nil: () -> (x) ->
    {
      :semantic "to be",
      :expected nil,
      :success x === nil
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is not `nil`.

```
> assert.expect(0, to.not_be_nil())
true

> assert.expect(nil, to.not_be_nil())
ERROR
  ...
```
~~~
  not_be_nil: () -> (x) ->
    {
      :semantic "to not be",
      :expected nil,
      :success x !== nil
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is boolean `true`.

```
> assert.expect(true, to.be_true())
true

> assert.expect(1, to.be_true())
ERROR
  ...
```
~~~
  be_true: () -> (x) ->
    {
      :semantic "to be",
      :expected true,
      :success x === true
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is boolean `false`.

```
> assert.expect(false, to.be_false())
true

> assert.expect(nil, to.be_false())
ERROR
  ...
```
~~~
  be_false: () -> (x) ->
    {
      :semantic "to be",
      :expected false,
      :success x === false
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is `NaN`.

```
> assert.expect(NaN, to.be_NaN())
true

> assert.expect("foo", to.be_NaN())
ERROR
  ...
```
~~~
  be_NaN: () -> (x) ->
    {
      :semantic "to be",
      :expected NaN,
      :success math.NaN?(x)
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `function`.

```
> assert.expect(() -> nil, to.be_function())
true

> assert.expect("foo", to.be_function())
ERROR
  ...
```
~~~
  be_function: () -> (x) ->
    {
      :semantic "to be",
      :expected "a function",
      :success x is function
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `long`, `double`, or `decimal`.

```
> assert.expect(1, to.be_numeric())
true

> assert.expect(Infinity, to.be_numeric())
true

> assert.expect("foo", to.be_numeric())
ERROR
  ...
```
~~~
  be_numeric: () -> (x) ->
    {
      :semantic "to be",
      :expected "a long, double, or decimal",
      :success x is long || x is double || x is decimal
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `long`.

```
> assert.expect(1, to.be_long())
true

> assert.expect(1.0, to.be_long())
ERROR
  ...
```
~~~
  be_long: () -> (x) ->
    {
      :semantic "to be",
      :expected "a long",
      :success x is long
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `double`.

```
> assert.expect(1.0, to.be_double())
true

> assert.expect(1, to.be_double())
ERROR
  ...
```
~~~
  be_double: () -> (x) ->
    {
      :semantic "to be",
      :expected "a double",
      :success x is double
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `decimal`.

```
> assert.expect(1.0d, to.be_decimal())
true

> assert.expect(1, to.be_decimal())
ERROR
  ...
```
~~~
  be_decimal: () -> (x) ->
    {
      :semantic "to be",
      :expected "a decimal",
      :success x is decimal
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `string`.

```
> assert.expect("foo", to.be_string())
true

> assert.expect(1, to.be_string())
ERROR
  ...
```
~~~
  be_string: () -> (x) ->
    {
      :semantic "to be",
      :expected "a string",
      :success x is string
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `binary`.

```
> assert.expect(0b00, to.be_binary())
true

> assert.expect(1, to.be_binary())
ERROR
  ...
```
~~~
  be_binary: () -> (x) ->
    {
      :semantic "to be",
      :expected "a binary",
      :success x is binary
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `boolean`.

```
> assert.expect(true, to.be_boolean())
true

> assert.expect(1, to.be_boolean())
ERROR
  ...
```
~~~
  be_boolean: () -> (x) ->
    {
      :semantic "to be",
      :expected "a boolean",
      :success x is boolean
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `dict`.

```
> assert.expect({:a 1}, to.be_dict())
true

> assert.expect(1, to.be_dict())
ERROR
  ...
```
~~~
  be_dict: () -> (x) ->
    {
      :semantic "to be",
      :expected "a dict",
      :success x is dict
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `list`.

```
> assert.expect([1,2,3], to.be_list())
true

> assert.expect(1, to.be_list())
ERROR
  ...
```
~~~
  be_list: () -> (x) ->
    {
      :semantic "to be",
      :expected "a list",
      :success x is list
    };

doc
~~~
`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `datetime`.

```
> assert.expect(2019-08-16T, to.be_datetime())
true

> assert.expect(1, to.be_datetime())
ERROR
  ...
```
~~~
  be_datetime: () -> (x) ->
    {
      :semantic "to be",
      :expected "a datetime",
      :success x is datetime
    };

}