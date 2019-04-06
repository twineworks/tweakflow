import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.last as last;

library last_spec {

  singleton:
    expect(last([1]), to.be(1));

  simple_list:
    expect(last([1, 2, 3]), to.be(3));

  of_nil:
    expect(last(nil), to.be_nil());

  empty:
    expect_error(
      () -> last([]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}