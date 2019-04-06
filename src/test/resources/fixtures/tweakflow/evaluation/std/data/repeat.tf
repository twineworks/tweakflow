import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.repeat as repeat;


library repeat_spec {

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
}