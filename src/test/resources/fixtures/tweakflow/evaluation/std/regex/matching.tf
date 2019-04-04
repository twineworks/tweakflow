import regex from "std"

import expect, expect_error, to from "std/assert.tf";

library p {
  hello?: regex.matching("hello .*");
  empty?: regex.matching("");
  digits?: regex.matching('\d+');
}

library matching_spec {
  hello_world:  p.hello?("hello world");
  helloween:    p.hello?("helloween")      === false;
  empty:        p.empty?("");
  a:            p.empty?("a")              === false;
  digits:       p.digits?("12345");
  non_digits:   p.digits?("123-45")        === false;
  of_nil:       p.digits?(nil)             === nil;

  nil_pattern:
    expect_error(
      () -> regex.matching(nil),
      to.have_code("NIL_ERROR")
    );

  invalid_pattern:
    expect_error(
      () -> regex.matching("[a"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}