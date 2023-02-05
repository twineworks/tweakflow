---
title: spec.tf
---

# module spec.tf{#spec}

The `std/spec` module contains libraries useful for creating test suites.



<div
      data-meta='true'
      data-meta-id='spec'
      data-meta-type='module'
      data-meta-name='spec.tf'
	    data-meta-tags=''
    ></div>

## library util{#util}

Utilities used internally.



<div
      data-meta='true'
      data-meta-id='util'
      data-meta-type='library'
      data-meta-name='util'
	    data-meta-tags=''
    ></div>

### permutations?{#util-permutations}

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



<div
      data-meta='true'
      data-meta-id='util-permutations'
      data-meta-type='var'
      data-meta-name='permutations?'
	    data-meta-tags='util'
    ></div>

## library assertions{#assertions}

The library contains functions useful for defining assertions.



<div
      data-meta='true'
      data-meta-id='assertions'
      data-meta-type='library'
      data-meta-name='assertions'
	    data-meta-tags=''
    ></div>

### expect_error{#assertions-expect_error}

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



<div
      data-meta='true'
      data-meta-id='assertions-expect_error'
      data-meta-type='var'
      data-meta-name='expect_error'
	    data-meta-tags='assertions'
    ></div>

### expect{#assertions-expect}

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



<div
      data-meta='true'
      data-meta-id='assertions-expect'
      data-meta-type='var'
      data-meta-name='expect'
	    data-meta-tags='assertions'
    ></div>

### assert{#assertions-assert}

Alias for [expect](#assertions-expect) that might be more readable in cases where the matcher is omitted and `to.be_true()` is used implicitly.

```
> assert.assert(1.0 == 1)
true

> assert.assert(1.0 === 1)
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='assertions-assert'
      data-meta-type='var'
      data-meta-name='assert'
	    data-meta-tags='assertions'
    ></div>

## library nodes{#nodes}

Functions that help construct spec nodes.



<div
      data-meta='true'
      data-meta-id='nodes'
      data-meta-type='library'
      data-meta-name='nodes'
	    data-meta-tags=''
    ></div>

### before{#nodes-before}

`(dict effect, string name, any context) -> dict`

Returns a `before` spec node with the given attributes.



<div
      data-meta='true'
      data-meta-id='nodes-before'
      data-meta-type='var'
      data-meta-name='before'
	    data-meta-tags='nodes'
    ></div>

### after{#nodes-after}

`(dict effect, string name, any context) -> dict`

Returns an `after` spec node with the given attributes.



<div
      data-meta='true'
      data-meta-id='nodes-after'
      data-meta-type='var'
      data-meta-name='after'
	    data-meta-tags='nodes'
    ></div>

### describe{#nodes-describe}

`(string name, list spec, any context, any tags) -> dict`

Returns a `describe` spec node with the given attributes.

If `tags` is a list, it is used directly.\
If `tags` is a dict, keys containing values casting to boolean `true` are used.



<div
      data-meta='true'
      data-meta-id='nodes-describe'
      data-meta-type='var'
      data-meta-name='describe'
	    data-meta-tags='nodes'
    ></div>

### it{#nodes-it}

`(string name, function spec, any context, any tags) -> dict`

Returns an `it` spec node with the given attributes.

If `tags` is a list, it is used directly.\
If `tags` is a dict, keys containing values casting to boolean `true` are used.



<div
      data-meta='true'
      data-meta-id='nodes-it'
      data-meta-type='var'
      data-meta-name='it'
	    data-meta-tags='nodes'
    ></div>

### subject{#nodes-subject}

`(any data, function transform, dict effect, any context) -> dict`

Returns a subject spec node with the given attributes.

Specify non-nil `transform` to create a `subject_transform` node.

Specify non-nil `effect` to create a `subject_effect` node.

If neither `transform`, nor `effect` are specified, a regular `subject` node is created.



<div
      data-meta='true'
      data-meta-id='nodes-subject'
      data-meta-type='var'
      data-meta-name='subject'
	    data-meta-tags='nodes'
    ></div>

## library to{#to}

The `to` library contains matcher functions useful in conjunction with the [expect](#assertions-expect) function.



<div
      data-meta='true'
      data-meta-id='to'
      data-meta-type='library'
      data-meta-name='to'
	    data-meta-tags=''
    ></div>

### have_code{#to-have_code}

`(expected) -> (err) -> boolean`

Matcher function that is satisfied if the given `err` is a dict with the expected value at key `:code`.

```
> assert.expect_error(() -> 1//0, to.have_code("FOO"))
ERROR:
  ...
```




<div
      data-meta='true'
      data-meta-id='to-have_code'
      data-meta-type='var'
      data-meta-name='have_code'
	    data-meta-tags='to'
    ></div>

### equal{#to-equal}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is equal to `expected` using the `==` comparison operator.

```
> assert.expect(1, to.equal(1.0))
true

> assert.expect(1, to.equal(2.0))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-equal'
      data-meta-type='var'
      data-meta-name='equal'
	    data-meta-tags='to'
    ></div>

### not_equal{#to-not_equal}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is not equal to `expected` using the `!=` comparison operator.

```
> assert.expect(1, to.not_equal(2.0))
true

> assert.expect(1, to.not_equal(1.0))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-not_equal'
      data-meta-type='var'
      data-meta-name='not_equal'
	    data-meta-tags='to'
    ></div>

### be{#to-be}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is equal to `expected` using the `===` comparison operator.

```
> assert.expect(1, to.be(1))
true

> assert.expect(1, to.be(1.0))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be'
      data-meta-type='var'
      data-meta-name='be'
	    data-meta-tags='to'
    ></div>

### not_be{#to-not_be}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is not equal to `expected` using the `!==` comparison operator.

```
> assert.expect(1, to.not_be(1.0))
true

> assert.expect(1, to.not_be(1))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-not_be'
      data-meta-type='var'
      data-meta-name='not_be'
	    data-meta-tags='to'
    ></div>

### match_regex{#to-match_regex}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a string and matches the regular expression `expected` in its entirety.

```
> assert.expect("12345", to.match_regex('\d+'))
true

> assert.expect("-12345", to.match_regex('\d+'))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-match_regex'
      data-meta-type='var'
      data-meta-name='match_regex'
	    data-meta-tags='to'
    ></div>

### be_like{#to-be_like}

Alias for [match_regex](#to-match_regex)



<div
      data-meta='true'
      data-meta-id='to-be_like'
      data-meta-type='var'
      data-meta-name='be_like'
	    data-meta-tags='to'
    ></div>

### be_greater_than{#to-be_greater_than}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` and `expected` are both numeric and `x` > `expected` holds true.

```
> assert.expect(10, to.be_greater_than(5))
true

> assert.expect(10, to.be_greater_than(20))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_greater_than'
      data-meta-type='var'
      data-meta-name='be_greater_than'
	    data-meta-tags='to'
    ></div>

### be_less_than{#to-be_less_than}

`(expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` and `expected` are both numeric and `x` < `expected` holds true.

```
> assert.expect(10, to.be_less_than(20))
true

> assert.expect(10, to.be_less_than(5))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_less_than'
      data-meta-type='var'
      data-meta-name='be_less_than'
	    data-meta-tags='to'
    ></div>

### be_between{#to-be_between}

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




<div
      data-meta='true'
      data-meta-id='to-be_between'
      data-meta-type='var'
      data-meta-name='be_between'
	    data-meta-tags='to'
    ></div>

### be_close_to{#to-be_close_to}

`(double expected, double precision=1e-15) -> (x) -> boolean`

Matcher function that is satisfied if `x` is numeric and within `precision` distance of `expected`.

```
> assert.expect(3.1415, to.be_close_to(math.pi, 0.001))
true

> assert.expect(3.1415, to.be_close_to(math.pi, 0.0000001))
ERROR:
  ...
```




<div
      data-meta='true'
      data-meta-id='to-be_close_to'
      data-meta-type='var'
      data-meta-name='be_close_to'
	    data-meta-tags='to'
    ></div>

### be_permutation_of{#to-be_permutation_of}

`(list expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is a non-nil list containing the same items as `expected`. Order does not matter.

```
> assert.expect([1,2,3], to.be_permutation_of([2,3,1]))
true

> assert.expect([1,2,3], to.be_permutation_of([2,3,2]))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_permutation_of'
      data-meta-type='var'
      data-meta-name='be_permutation_of'
	    data-meta-tags='to'
    ></div>

### be_superset_of{#to-be_superset_of}

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



<div
      data-meta='true'
      data-meta-id='to-be_superset_of'
      data-meta-type='var'
      data-meta-name='be_superset_of'
	    data-meta-tags='to'
    ></div>

### be_subset_of{#to-be_subset_of}

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



<div
      data-meta='true'
      data-meta-id='to-be_subset_of'
      data-meta-type='var'
      data-meta-name='be_subset_of'
	    data-meta-tags='to'
    ></div>

### be_one_of{#to-be_one_of}

`(list expected) -> (x) -> boolean`

Matcher function that is satisfied if `x` is one of the values in `expected`.

```
> assert.expect(2, to.be_one_of([1,2,3]))
true

> assert.expect(2, to.be_one_of([0,1]))
ERROR:
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_one_of'
      data-meta-type='var'
      data-meta-name='be_one_of'
	    data-meta-tags='to'
    ></div>

### contain{#to-contain}

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



<div
      data-meta='true'
      data-meta-id='to-contain'
      data-meta-type='var'
      data-meta-name='contain'
	    data-meta-tags='to'
    ></div>

### contain_all{#to-contain_all}

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



<div
      data-meta='true'
      data-meta-id='to-contain_all'
      data-meta-type='var'
      data-meta-name='contain_all'
	    data-meta-tags='to'
    ></div>

### be_nil{#to-be_nil}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is `nil`.

```
> assert.expect(nil, to.be_nil())
true

> assert.expect(0, to.be_nil())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_nil'
      data-meta-type='var'
      data-meta-name='be_nil'
	    data-meta-tags='to'
    ></div>

### not_be_nil{#to-not_be_nil}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is not `nil`.

```
> assert.expect(0, to.not_be_nil())
true

> assert.expect(nil, to.not_be_nil())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-not_be_nil'
      data-meta-type='var'
      data-meta-name='not_be_nil'
	    data-meta-tags='to'
    ></div>

### be_true{#to-be_true}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is boolean `true`.

```
> assert.expect(true, to.be_true())
true

> assert.expect(1, to.be_true())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_true'
      data-meta-type='var'
      data-meta-name='be_true'
	    data-meta-tags='to'
    ></div>

### be_false{#to-be_false}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is boolean `false`.

```
> assert.expect(false, to.be_false())
true

> assert.expect(nil, to.be_false())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_false'
      data-meta-type='var'
      data-meta-name='be_false'
	    data-meta-tags='to'
    ></div>

### be_NaN{#to-be_NaN}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is `NaN`.

```
> assert.expect(NaN, to.be_NaN())
true

> assert.expect("foo", to.be_NaN())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_NaN'
      data-meta-type='var'
      data-meta-name='be_NaN'
	    data-meta-tags='to'
    ></div>

### be_function{#to-be_function}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `function`.

```
> assert.expect(() -> nil, to.be_function())
true

> assert.expect("foo", to.be_function())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_function'
      data-meta-type='var'
      data-meta-name='be_function'
	    data-meta-tags='to'
    ></div>

### be_numeric{#to-be_numeric}

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



<div
      data-meta='true'
      data-meta-id='to-be_numeric'
      data-meta-type='var'
      data-meta-name='be_numeric'
	    data-meta-tags='to'
    ></div>

### be_long{#to-be_long}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `long`.

```
> assert.expect(1, to.be_long())
true

> assert.expect(1.0, to.be_long())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_long'
      data-meta-type='var'
      data-meta-name='be_long'
	    data-meta-tags='to'
    ></div>

### be_double{#to-be_double}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `double`.

```
> assert.expect(1.0, to.be_double())
true

> assert.expect(1, to.be_double())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_double'
      data-meta-type='var'
      data-meta-name='be_double'
	    data-meta-tags='to'
    ></div>

### be_decimal{#to-be_decimal}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `decimal`.

```
> assert.expect(1.0d, to.be_decimal())
true

> assert.expect(1, to.be_decimal())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_decimal'
      data-meta-type='var'
      data-meta-name='be_decimal'
	    data-meta-tags='to'
    ></div>

### be_string{#to-be_string}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `string`.

```
> assert.expect("foo", to.be_string())
true

> assert.expect(1, to.be_string())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_string'
      data-meta-type='var'
      data-meta-name='be_string'
	    data-meta-tags='to'
    ></div>

### be_binary{#to-be_binary}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `binary`.

```
> assert.expect(0b00, to.be_binary())
true

> assert.expect(1, to.be_binary())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_binary'
      data-meta-type='var'
      data-meta-name='be_binary'
	    data-meta-tags='to'
    ></div>

### be_boolean{#to-be_boolean}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `boolean`.

```
> assert.expect(true, to.be_boolean())
true

> assert.expect(1, to.be_boolean())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_boolean'
      data-meta-type='var'
      data-meta-name='be_boolean'
	    data-meta-tags='to'
    ></div>

### be_dict{#to-be_dict}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `dict`.

```
> assert.expect({:a 1}, to.be_dict())
true

> assert.expect(1, to.be_dict())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_dict'
      data-meta-type='var'
      data-meta-name='be_dict'
	    data-meta-tags='to'
    ></div>

### be_list{#to-be_list}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `list`.

```
> assert.expect([1,2,3], to.be_list())
true

> assert.expect(1, to.be_list())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_list'
      data-meta-type='var'
      data-meta-name='be_list'
	    data-meta-tags='to'
    ></div>

### be_datetime{#to-be_datetime}

`() -> (x) -> boolean`

Matcher function that is satisfied if `x` is of type `datetime`.

```
> assert.expect(2019-08-16T, to.be_datetime())
true

> assert.expect(1, to.be_datetime())
ERROR
  ...
```



<div
      data-meta='true'
      data-meta-id='to-be_datetime'
      data-meta-type='var'
      data-meta-name='be_datetime'
	    data-meta-tags='to'
    ></div>

