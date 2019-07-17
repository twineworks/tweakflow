import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.repeat as repeat;


library spec {
  spec:
    describe("repeat", [


  empty:
    expect(repeat(0, "a"), to.be([]));

  singleton:
    expect(repeat(1, "a"), to.be(["a"]));

  some:
    expect(repeat(5, "a"), to.be(["a", "a", "a", "a", "a"]));

  of_default:
    expect(repeat(), to.be_nil());

  of_nil:
    expect(repeat(nil, "a"), to.be_nil());

  invalid_type:
    expect_error(
      () -> repeat(-1, "foo"),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );
  ]);
}