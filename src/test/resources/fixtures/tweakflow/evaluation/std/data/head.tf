import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.head as head;

library head_spec {

  singleton:
    expect(head([1]), to.be(1));

  simple_list:
    expect(head([1, 2, 3]), to.be(1));

  of_nil:
    expect(head(nil), to.be_nil());

  empty:
    expect_error(
      () -> head([]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}