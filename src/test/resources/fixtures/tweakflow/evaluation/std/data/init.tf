import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.init as init;

library init_spec {

  singleton:
    expect(init([1]), to.be([]));

  simple_list:
    expect(init([1, 2, 3]), to.be([1, 2]));

  of_nil:
    expect(init(nil), to.be_nil());

  empty:
    expect_error(
      () -> init([]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}