import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.pi as pi;
alias m.sin as sin;

library pi_spec {

  base_e:
    expect(sin(pi), to.be_close_to(0.0));

}

