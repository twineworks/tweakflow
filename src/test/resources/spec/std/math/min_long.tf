import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.min_long as min_long;

library spec {
  spec:
    describe("min_long", [


  is_neg:
    expect(min_long, to.be_less_than(0));

  wrap:
    expect(min_long-1, to.be_greater_than(0));

  ]);
}

