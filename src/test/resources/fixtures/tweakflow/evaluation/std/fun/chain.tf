import fun, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias fun.chain as chain;

library chain_spec {

  of_empty:
    let {
      f: chain([]);
    }
    expect(f, to.be_function()) &&
    expect(f("foo"), to.be("foo"));

  of_one_f:
    expect(chain([(x) -> x+1])(0), to.be(1));

  of_three_f:
    expect(
      chain(
        [
          (x) -> x+1,
          (x) -> x*5,
          (x) -> x+4
        ]
      )(2),
      to.be(19) # (2+1)*5+4
    );

  of_f_nil:
    expect_error(
      () -> chain(nil),
      to.have_code("NIL_ERROR")
    );


}