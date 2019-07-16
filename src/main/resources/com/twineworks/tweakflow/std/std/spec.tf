import core, fun, data, regex, strings, math, time from 'std.tf';

export assert.expect as expect;
export assert.expect_error as expect_error;

export spec.describe as describe;
export spec.it as it;
export spec.subject as subject;
export spec.before as before;
export spec.after as after;

alias data.size as size;
alias data.delete as delete;
alias data.reduce_until as reduce_until;
alias core.hash as hash;

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

  path_sep: "/";

  path_line_char:
    regex.splitting(":");

  path_elements:
    regex.splitting(path_sep);

  linkable_trace_line: (t) ->
    let {
      parts: data.zip_dict(["path", "line", "char"], path_line_char(t));
      elements: path_elements(parts[:path]);
      line: parts[:line];
      dir_path: strings.join(data.init(elements), path_sep);
      filename: data.last(elements);
    }
    "  at . #{dir_path}#{path_sep}(#{filename}:#{line})";

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
      result: f(x);
      semantic: if result is list then result[0] else result[:semantic];
      success: if result is list then result[1] else result[:success];
      expected: if result is list then result[2] else result[:expected];
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

}

library spec {

  function before: (any effect, string name) -> {
    :type "before",
    :name name default "before",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1])
  };

  function after: (any effect, string name) -> {
    :type "after",
    :name name default "after",
    :effect effect,
    :at (try throw "err" catch _, trace trace[:stack, 1])
  };

  function describe: (string name, any spec) -> {
    :type 'describe',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1])
  };

  function it: (string name, any spec) -> {
    :type 'it',
    :name name,
    :spec spec,
    :at (try throw "err" catch _, trace trace[:stack, 1])
  };

  function subject: (any data, any transform, any effect) ->

    if (transform != nil)
      {
        :type 'subject_transform',
        :transform transform,
      }
    if (effect != nil)
      {
        :type 'subject_effect',
        :effect effect,
      }
    else
      {
        :type 'subject',
        :data data,
      }
  ;

}

export library to {

  have_code: (expected) -> (err) ->
    ["to have code", err is dict && err[:code] == expected, expected];

  equal: (expected) -> (x) ->
    ["to equal", (x == expected), expected];

  not_equal: (expected) -> (x) ->
    ["to not equal", (x != expected), expected];

  be: (expected) -> (x) ->
    ["to be", x === expected, expected];

  be_greater_than: (expected) -> (x) ->
    ["to be greater than", x > expected, "x > #{expected}"];

  be_less_than: (expected) -> (x) ->
    ["to be less than", x < expected, "x < #{expected}"];

  be_between: (expected_low, expected_high) -> (x) ->
    ["to be between", expected_low <= x && x < expected_high, "#{expected_low} <= x < #{expected_high}"];

  be_close_to: (double expected, double precision=1e-15) -> (x) ->
    ["to be close to", math.abs(expected-x) <= precision, "abs(#{expected} - x) <= #{precision}"];

  be_permutation_of: (list expected) -> (x) ->
    [
      "to be permutation of",
      x is list && util.permutations?(expected, x),
      expected
    ];

  be_superset_of: (dict expected) -> (x) ->
    [
      "to be superset of",
      x is dict &&
        data.all?(
          data.keys(expected),
          (k) -> data.has?(x, k) && x[k] === expected[k]
        ),
      expected
    ];

  be_one_of: (list expected) -> (x) ->
    [
      "to be one of",
      data.contains?(expected, x),
      expected
    ];

  contain: (expected) -> (x) ->
    ["to contain", (x is list || x is dict) && data.contains?(expected, x), expected];

  contain_all: (list expected) -> (xs) ->
    [
      "to contain all",
      (xs is list || xs is dict) &&
        data.all?(
          expected,
          (e) -> data.contains?(xs, e)
        ),
      expected
    ];

  not_be: (expected) -> (x) ->
    ["to not be", x !== expected, expected];

  be_nil: () -> (x) ->
    ["to be nil", x === nil, "x === nil"];

  not_be_nil: () -> (x) ->
    ["to not be nil", x !== nil, "x !== nil"];

  be_true: () -> (x) -> let {
      success: x === true;
    }
    {
      :semantic "to be",
      :expected true,
      :success success
    };

  be_false: () -> (x) ->
    ["to be false", x === false, "x === false"];

  be_NaN: () -> (x) ->
    ["to be NaN", math.NaN?(x), "math.NaN?(x)"];

  be_function: () -> (x) ->
    {
      :semantic "to be",
      :expected "a function",
      :success x is function
    };


}