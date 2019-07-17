import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.reverse as reverse;

library spec {
  spec:
    describe("reverse", [


  empty_list:
    expect(reverse([]), to.be([]));

  simple_list:
    expect(reverse([1,2,3]), to.be([3,2,1]));

  of_nil:
    expect(reverse(nil), to.be_nil());

  ]);
}