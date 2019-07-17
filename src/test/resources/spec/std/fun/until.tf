import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.until as until;

library spec {
  spec:
    describe("until", [


  of_0_times:
    let {
      p: (_) -> true;
      x: 0;
      f: (x) -> x+1;
    }
    expect(until(p, x, f), to.be(0));

  of_1_times:
    let {
      p: (x) -> x >= 1;
      x: 0;
      f: (x) -> x+1;
    }
    expect(until(p, x, f), to.be(1));

  of_1000_times_with_cast:
    let {
      p: (x) -> if x >= 1000 then true else nil;
      x: 0;
      f: (x) -> x+1;
    }
    expect(until(p, x, f), to.be(1000));

  of_f_nil:
    expect_error(
      () -> until((_) -> true, 0, nil),
      to.have_code("NIL_ERROR")
    );

  of_bad_f:
    expect_error(
      () -> until((_) -> true, 0, () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_p_nil:
    expect_error(
      () -> until(nil, 0, (_) -> true),
      to.have_code("NIL_ERROR")
    );

  of_bad_p:
    expect_error(
      () -> until(() -> true, 0, (_) -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}