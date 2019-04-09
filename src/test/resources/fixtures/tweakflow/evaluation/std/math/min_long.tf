import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.min_long as min_long;

library min_long_spec {

  is_neg:
    expect(min_long, to.be_less_than(0));

  wrap:
    expect(min_long-1, to.be_greater_than(0));

}

