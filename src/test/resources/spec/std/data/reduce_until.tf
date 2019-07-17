import data from "std.tf";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.reduce_until as reduce_until;

library spec {
  spec:
    describe("reduce_until", [


  sum:
    expect(reduce_until([1,2,3,4], 0, (a) -> false, (a, x) -> a+x), to.be(10));

  sum_until:
    expect(reduce_until([1,2,3,4], 0, (a) -> a >= 6, (a, x) -> a+x), to.be(6));

  initial_value:
    expect(reduce_until([], "foo", (a) -> true, (a, x) -> a .. x), to.be("foo"));

  of_nil:
    expect(reduce_until(nil, 0, (a) -> false, (a, x) -> x), to.be_nil());

  nil_f:
    expect_error(
      () -> reduce_until([0,1], 0, (a) -> false, nil),
       to.have_code("NIL_ERROR")
    );

  nil_p:
    expect_error(
      () -> reduce_until([0,1], 0, nil, (a, x) -> a),
       to.have_code("NIL_ERROR")
    );

  bad_f:
    expect_error(
      () -> reduce_until([0,1], 0, (a) -> false, (x) -> x), # f must accept 2 or more args
       to.have_code("ILLEGAL_ARGUMENT")
    );

  bad_p:
    expect_error(
      () -> reduce_until([0,1], 0, () -> false, (a, x) -> x), # p must accept 1 or more args
       to.have_code("ILLEGAL_ARGUMENT")
    );
  ]);
}