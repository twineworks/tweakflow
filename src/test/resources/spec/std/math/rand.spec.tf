import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.rand as rand;
alias data.unique as unique;

library spec {
  spec:
    describe("math.rand", [

      it("of_default", () ->
        expect(rand(), to.be_close_to(0.730967787376657))
      ),

      it("of_nil", () ->
        expect(rand(nil), to.be_close_to(0.730967787376657))
      ),

      it("of_foo", () ->
        expect(rand("foo"), to.be_between(0, 1))
      ),

      it("of_zero", () ->
        expect(rand(0), to.be_between(0, 1))
      ),

      it("distinct_seeds_distinct_outs", () ->
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
        expect(unique([a, b, c, d]), to.be([a, b, c, d]))
      ),

  ]);
}

