import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.pi as pi;
alias m.sin as sin;

library spec {
  spec:
    describe("pi", [


  base_e:
    expect(sin(pi), to.be_close_to(0.0));

  ]);
}

