import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.tail as tail;

library spec {
  spec:
    describe("tail", [


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
  ]);
}