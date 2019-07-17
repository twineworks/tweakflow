import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.times as times;

library spec {
  spec:
    describe("times", [

  of_n_nil:
    expect(times(nil, 'foo', (x) -> "bar"), to.be_nil());

  of_n_zero:
    expect(times(0, 'foo', (x) -> "bar"), to.be("foo"));

  of_n_1:
    expect(times(1, 0, (x) -> x+1), to.be(1));

  of_n_100:
    expect(times(100, 0, (x) -> x+1), to.be(100));

  of_f_nil:
    expect_error(
      () -> times(1, 0, nil),
      to.have_code("NIL_ERROR")
    );

  of_neg_n:
    expect_error(
      () -> times(-1, 0, (x) -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_bad_f:
    expect_error(
      () -> times(1, 0, () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_all_nil:
    expect_error(
      () -> times(),
      to.have_code("NIL_ERROR")
    );

  ]);
}