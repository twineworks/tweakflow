import data from "std.tf";
import expect, expect_error, to from "std/assert.tf";

alias data.reduce_while as reduce_while;

library reduce_while_spec {

  sum:
    expect(reduce_while([1,2,3,4], 0, (a) -> true, (a, x) -> a+x), to.be(10));

  sum_while:
    expect(reduce_while([1,2,3,4], 0, (a) -> a < 6, (a, x) -> a+x), to.be(6));

  initial_value:
    expect(reduce_while([], "foo", (a) -> false, (a, x) -> a .. x), to.be("foo"));

  of_nil:
    expect(reduce_while(nil, 0, (a) -> true, (a, x) -> x), to.be_nil());

  nil_f:
    expect_error(
      () -> reduce_while([0,1], 0, (a) -> true, nil),
       to.have_code("NIL_ERROR")
    );

  nil_p:
    expect_error(
      () -> reduce_while([0,1], 0, nil, (a, x) -> a),
       to.have_code("NIL_ERROR")
    );

  bad_f:
    expect_error(
      () -> reduce_while([0,1], 0, (a) -> true, (x) -> x), # f must accept 2 or more args
       to.have_code("ILLEGAL_ARGUMENT")
    );

  bad_p:
    expect_error(
      () -> reduce_while([0,1], 0, () -> true, (a, x) -> x), # p must accept 1 or more args
       to.have_code("ILLEGAL_ARGUMENT")
    );
}