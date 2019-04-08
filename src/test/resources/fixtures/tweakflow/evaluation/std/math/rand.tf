import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.rand as rand;
alias data.unique as unique;

library rand_spec {

  of_default:
    expect(rand(), to.be_close_to(0.730967787376657));

  of_nil:
    expect(rand(nil), to.be_close_to(0.730967787376657));

  of_foo:
    expect(rand("foo"), to.be_between(0, 1));

  of_zero:
    expect(rand(0), to.be_between(0, 1));

  distinct_seeds_distinct_outs:
    let {
      a: rand("a");
      b: rand("b");
      c: rand("c");
      d: rand("d");
    }
    expect(a, to.be_between(0, 1)) &&
    expect(b, to.be_between(0, 1)) &&
    expect(c, to.be_between(0, 1)) &&
    expect(d, to.be_between(0, 1)) &&
    expect(unique([a, b, c, d]), to.be([a, b, c, d]));

}

