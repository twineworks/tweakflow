import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.tail as tail;

library tail_spec {

  singleton:
    expect(tail([1]), to.be([]));

  simple_list:
    expect(tail([1, 2, 3]), to.be([2, 3]));

  of_nil:
    expect(tail(nil), to.be_nil());

  empty:
    expect_error(
      () -> tail([]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}