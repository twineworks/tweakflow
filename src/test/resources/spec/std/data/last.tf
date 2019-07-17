import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.last as last;

library spec {
  spec:
    describe("last", [


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
  ]);
}