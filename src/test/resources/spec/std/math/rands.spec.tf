import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.rands as rands;
alias data.unique as unique;

library spec {
  spec:
    describe("math.rands", [

      it("of_nil", () ->
        expect(rands(nil), to.be_nil())
      ),

      it("0_of_foo", () ->
        expect(rands(0, "foo"), to.be([]))
      ),

      it("2_of_foo", () ->
        expect(rands(2, "foo"), to.be([0.7636542620472306, 0.4845448486565105]))
      ),

      it("0_of_bar", () ->
        expect(rands(0, "bar"), to.be([]))
      ),

      it("2_of_bar", () ->
        expect(rands(2, "bar"), to.be([0.8603985347346733, 0.7525012276982017]))
      ),

      it("-1_of_foo", () ->
        expect_error(
          () -> rands(-1, "foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}

