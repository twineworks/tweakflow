import data, math, regex from 'std';

export assert.expect as expect;
export assert.assert as assert;
export assert.expect_error as expect_error;

export spec.describe as describe;
export spec.it as it;
export spec.subject as subject;
export spec.before as before;
export spec.after as after;

alias data.size as size;
alias data.delete as delete;
alias data.reduce_until as reduce_until;

library util {

  permutations?: (list xs, list ys) ->
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

library assert {

  function expect_error: (x, f) ->
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

  function expect: (x, f, _stack_offset=3) ->
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

  function assert: expect;

}

library spec {

  function before: (any effect, string name, any context) -> {
    :type "before",
    :name name default "before",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context
  };

  function after: (any effect, string name, any context) -> {
    :type "after",
    :name name default "after",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context
  };

  function describe: (string name, any spec, any context, any tags) -> {
    :type 'describe',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context,
    :tags tags
  };

  function it: (string name, any spec, any context, any tags) -> {
    :type 'it',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1]),
    :context context,
    :tags tags
  };

  function subject: (any data, any transform, any effect, any context) ->

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

export library to {

  have_code: (expected) -> (err) ->
    {
      :semantic "to have code",
      :expected expected,
      :success err is dict && err[:code] === expected
    };

  equal: (expected) -> (x) ->
    {
      :semantic "to equal",
      :expected expected,
      :success x == expected
    };

  not_equal: (expected) -> (x) ->
    {
      :semantic "to not equal",
      :expected expected,
      :success x != expected
    };

  be: (expected) -> (x) ->
    {
      :semantic "to be",
      :expected expected,
      :success x === expected
    };

  match_regex: (string expected) -> (x) ->
    {
      :semantic "to match regular expression",
      :expected expected,
      :success x is string && regex.matching(expected)(x)
    };

  be_like: match_regex;

  be_greater_than: (expected) -> (x) ->
    {
      :semantic "to be greater than",
      :expected expected,
      :success (x is double || x is long) && (expected is double || expected is long) && x > expected
    };

  be_less_than: (expected) -> (x) ->
    {
      :semantic "to be less than",
      :expected expected,
      :success (x is double || x is long) && (expected is double || expected is long)  && x < expected
    };

  be_between: (expected_low, expected_high, boolean low_inclusive=true, boolean high_inclusive=true) -> (x) ->
    {
      :semantic "to be between",
      :expected [expected_low, expected_high],
      :success (expected_low is long || expected_low is double) &&
               (expected_high is long || expected_high is double) &&
               (x is long || x is double) &&
               (if low_inclusive (expected_low <= x) else (expected_low < x)) &&
               (if high_inclusive (x <= expected_high) else (x < expected_high))
    };

  be_close_to: (double expected, double precision=1e-15) -> (x) ->
    {
      :semantic "to be within #{precision} of",
      :expected expected,
      :success
        (x is long || x is double) &&
        (expected is long || expected is double) &&
        (!math.NaN?(expected)) &&
        (!math.NaN?(x)) &&
        (!math.NaN?(precision)) &&
        (
          (precision == Infinity) ||
          (math.abs(expected-x) <= precision)
        )
    };

  be_permutation_of: (list expected) -> (x) ->
    {
      :semantic "to be a permutation of",
      :expected expected,
      :success x is list && util.permutations?(expected, x),
    };

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

  be_one_of: (list expected) -> (x) ->
    {
      :semantic "to be one of",
      :expected expected,
      :success data.contains?(expected, x),
    };

  contain: (expected) -> (x) ->
    {
      :semantic "to contain",
      :expected expected,
      :success
        (x is list || x is dict) && data.contains?(x, expected)
    };

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

  not_be: (expected) -> (x) ->
    {
      :semantic "to not be",
      :expected expected,
      :success x !== expected
    };

  be_nil: () -> (x) ->
    {
      :semantic "to be",
      :expected nil,
      :success x === nil
    };

  not_be_nil: () -> (x) ->
    {
      :semantic "to not be",
      :expected nil,
      :success x !== nil
    };

  be_true: () -> (x) ->
    {
      :semantic "to be",
      :expected true,
      :success x === true
    };

  be_false: () -> (x) ->
    {
      :semantic "to be",
      :expected false,
      :success x === false
    };

  be_NaN: () -> (x) ->
    {
      :semantic "to be",
      :expected NaN,
      :success math.NaN?(x)
    };

  be_function: () -> (x) ->
    {
      :semantic "to be",
      :expected "a function",
      :success x is function
    };

  be_numeric: () -> (x) ->
    {
      :semantic "to be",
      :expected "a long or double",
      :success x is long || x is double
    };

  be_long: () -> (x) ->
    {
      :semantic "to be",
      :expected "a long",
      :success x is long
    };

  be_double: () -> (x) ->
    {
      :semantic "to be",
      :expected "a double",
      :success x is double
    };

  be_string: () -> (x) ->
    {
      :semantic "to be",
      :expected "a string",
      :success x is string
    };

  be_binary: () -> (x) ->
    {
      :semantic "to be",
      :expected "a binary",
      :success x is binary
    };

  be_boolean: () -> (x) ->
    {
      :semantic "to be",
      :expected "a boolean",
      :success x is boolean
    };

  be_dict: () -> (x) ->
    {
      :semantic "to be",
      :expected "a dict",
      :success x is dict
    };

  be_list: () -> (x) ->
    {
      :semantic "to be",
      :expected "a list",
      :success x is list
    };

  be_datetime: () -> (x) ->
    {
      :semantic "to be",
      :expected "a datetime",
      :success x is datetime
    };

}