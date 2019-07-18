import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.compose as compose;

library spec {
  spec:
    describe("fun.compose", [

      it("of_empty", () ->
        let {
          f: compose([]);
        }
        expect(f, to.be_function()) &&
        expect(f("foo"), to.be("foo"))
      ),

      it("of_one_f", () ->
        expect(compose([(x) -> x+1])(0), to.be(1))
      ),

      it("of_three_f", () ->
        expect(
          compose(
            [
              (x) -> x+1,
              (x) -> x*5,
              (x) -> x+4
            ]
          )(2),
          to.be(31) # (2+4)*5+1
        )
      ),

      it("of_f_nil", () ->
        expect_error(
          () -> compose(nil),
          to.have_code("NIL_ERROR")
        )
      ),
  ]);
}