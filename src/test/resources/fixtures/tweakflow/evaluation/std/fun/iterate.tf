import fun, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias fun.iterate as iterate;

library iterate_spec {

  of_zero_iterate:
    expect(iterate(1, 0, 'foo', (x, i) -> "bar"), to.be("foo"));

  of_iterate_once:
    expect(iterate(1, 1, 0, (_, i) -> i), to.be(1));

  of_iterate_once_at_offset:
    expect(iterate(10, 10, 0, (_, i) -> i), to.be(10));

  of_iterate_100:
    expect(iterate(1, 100, 0, (x, i) -> x+i), to.be(5050));

  of_f_nil:
    expect_error(
      () -> iterate(1, 0, 0, nil),
      to.have_code("NIL_ERROR")
    );

  of_bad_f:
    expect_error(
      () -> iterate(0, 0, 0, () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}