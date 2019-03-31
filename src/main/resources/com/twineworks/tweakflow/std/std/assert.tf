import core, fun, data, regex, strings from 'std.tf';

export assert.expect as expect;
export assert.expect_error as expect_error;

library util {

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
      x() && let {
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
      expect(error, f)

  function expect: (x, f) ->
    let {
      result: f(x);
      semantic: result[0];
      success: result[1];
      expected: result[2];
    }
    if !success
      let {
        # get caller information from forced trace
        call_loc: try throw "err" catch _, trace trace[:stack, 3];
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

}

export library to {

  have_code: (expected) -> (err) ->
    ["to have code", err[:code] == expected, expected]

  equal: (expected) -> (x) ->
    ["to equal", (x == expected), expected]

  not_equal: (expected) -> (x) ->
    ["to not equal", (x != expected), expected]

  be: (expected) -> (x) ->
    ["to be", x === expected, expected]

  not_be: (expected) -> (x) ->
    ["to not be", x !== expected, expected]

  be_nil: () -> (x) ->
    ["to be nil", x === nil, nil]

  not_be_nil: () -> (x) ->
    ["to not be nil", x !== nil, nil]

  be_true: () -> (x) ->
    ["to be true", x === true, true]

  be_false: () -> (x) ->
    ["to be false", x === false, false]


}