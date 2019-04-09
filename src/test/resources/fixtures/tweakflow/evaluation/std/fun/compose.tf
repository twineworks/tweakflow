import fun, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias fun.compose as compose;

library compose_spec {

  of_empty:
    let {
      f: compose([]);
    }
    expect(f, to.be_function()) &&
    expect(f("foo"), to.be("foo"));

  of_one_f:
    expect(compose([(x) -> x+1])(0), to.be(1));

  of_three_f:
    expect(
      compose(
        [
          (x) -> x+1,
          (x) -> x*5,
          (x) -> x+4
        ]
      )(2),
      to.be(31) # (2+4)*5+1
    );

  of_f_nil:
    expect_error(
      () -> compose(nil),
      to.have_code("NIL_ERROR")
    );


}