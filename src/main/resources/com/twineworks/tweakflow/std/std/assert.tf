import core, fun, data, regex, strings, math, time from 'std.tf';

export assert.expect as expect;
export assert.expect_error as expect_error;

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
    try
      (x() || true) && let {
                    # get caller information from forced trace
                    call_loc: try throw "err" catch _, trace trace[:stack, 2];
                    at: util.linkable_trace_line(call_loc);
                 }
                 debug
                   "Assertion Error\n\n"
                   .. "expected error, but no error was thrown\n\n"
                   .. at,
                   false
    catch error
      expect(error, f, _stack_offset: 5)

    ;

  function expect: (x, f, _stack_offset=3) ->
    let {
      result: f(x);
      semantic: result[0];
      success: result[1];
      expected: result[2];
    }
    if !success
      let {
        # get caller information from forced trace
        call_loc: try throw "err" catch _, trace trace[:stack, _stack_offset];
        at: util.linkable_trace_line(call_loc);
      }
      debug
        "Assertion Error\n\n"
        .. "expected: ".. core.inspect(x) .. "\n"
        .. "#{semantic}: " .. core.inspect(expected) .. "\n\n"
        .. at,
        false
    else
      true
    ;

}

export library to {

  have_code: (expected) -> (err) ->
    ["to have code", err[:code] == expected, expected];

  equal: (expected) -> (x) ->
    ["to equal", (x == expected), expected];

  not_equal: (expected) -> (x) ->
    ["to not equal", (x != expected), expected];

  be: (expected) -> (x) ->
    ["to be", x === expected, expected];

  be_permutation_of: (list expected) -> (x) ->
    [
      "to be permutation of",
      x is list && util.permutations?(expected, x),
      expected
    ];

  be_one_of: (list expected) -> (x) ->
    [
      "to be one of",
      data.contains?(expected, x),
      expected
    ];

  not_be: (expected) -> (x) ->
    ["to not be", x !== expected, expected];

  be_nil: () -> (x) ->
    ["to be nil", x === nil, "x === nil"];

  not_be_nil: () -> (x) ->
    ["to not be nil", x !== nil, "x !=== nil"];

  be_true: () -> (x) ->
    ["to be true", x === true, "x === true"];

  be_false: () -> (x) ->
    ["to be false", x === false, "x === false"];

  be_NaN: () -> (x) ->
    ["to be false", math.nan?(x), "math.nan?(x)"];

  be_function: () -> (x) ->
    ["to be function", x is function, "x is function"];

}