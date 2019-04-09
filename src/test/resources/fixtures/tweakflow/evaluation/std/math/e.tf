import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.e as e;
alias m.log as log;

library e_spec {

  base_e:
    expect(log(e), to.be_close_to(1.0));

}

