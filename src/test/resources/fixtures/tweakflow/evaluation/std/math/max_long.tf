import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.max_long as max_long;

library max_long_spec {

  is_pos:
    expect(max_long, to.be_greater_than(0));

  wrap:
    expect(max_long+1, to.be_less_than(0));

}

