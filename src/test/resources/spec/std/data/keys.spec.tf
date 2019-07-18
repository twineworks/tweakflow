import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.keys as keys;


library spec {
  spec:
    describe("data.keys", [

      it("empty_list", () ->
        expect(keys([]), to.be([]))
      ),

      it("simple_list", () ->
        expect(keys([1,nil,3]), to.be([0, 1, 2]))
      ),

      it("empty_dict", () ->
        expect(keys({}), to.be([]))
      ),

      it("simple_dict", () ->
        expect(keys({:a 1, :b 2}), to.be_permutation_of([:a, :b]))
      ),

      it("medium_dict", () ->
        expect(
          keys({:alpha 1, :beta 2, :gamma 3, :delta 4, :epsilon 5, :eta 6, :theta 7}),
          to.be_permutation_of([:alpha, :beta, :gamma, :delta, :epsilon, :eta, :theta])
        )
      ),

      it("of_nil", () ->
        expect(keys(nil), to.be_nil())
      ),

      it("invalid_type", () ->
        expect_error(
          () -> keys("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}