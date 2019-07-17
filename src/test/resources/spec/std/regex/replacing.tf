import regex from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias regex.replacing as replacing;

library p {
  world_greeting:   replacing('hello (\w+)', "hello world");
  greetings:        replacing('hello (\w+)', "greetings $1");
  bad_index:        replacing('hello (\w+)', "greetings $2");
  dollar:           replacing('dollar', '\$1');
  ]);
}

library spec {
  spec:
    describe("replacing", [


  hello_world:
    expect(p.world_greeting("hello you"), to.be("hello world"));

  hello_twice:
    expect(p.world_greeting("hello a, hello b"), to.be("hello world, hello world"));

  greetings:
    expect(p.greetings("hello joe"), to.be("greetings joe"));

  greetings_twice:
    expect(p.greetings("hello joe, hello sue"), to.be("greetings joe, greetings sue"));

  bad_index:
    expect_error(
      () -> p.bad_index("hello joe"),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  dollar:
    expect(p.dollar('for a fistful of dollars'), to.be("for a fistful of $1s"));

  of_nil:
    expect(
      p.greetings(nil),
      to.be(nil)
    );

  nil_pattern:
    expect_error(
      () -> replacing(nil),
      to.have_code("NIL_ERROR")
    );

  invalid_pattern:
    expect_error(
      () -> replacing("[a", ""),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}