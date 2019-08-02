import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.omit as omit;

library spec {
  spec:
    describe("data.omit", [

      it("omits some keys", () ->
        expect(
          omit({:a 1, :b 2, :c 3}, ["a", "c"]),
          to.be({:b 2})
        )
      ),

      it("omits all keys", () ->
        expect(
          omit({:a 1, :b 2, :c 3}, ["a", "b", "c"]),
          to.be({})
        )
      ),

      it("omits empty keys", () ->
        expect(
          omit({:a 1, :b 2, :c 3}, []),
          to.be({:a 1, :b 2, :c 3})
        )
      ),

      it("omits not-found keys", () ->
        expect(
          omit({:a 1, :b 2, :c 3}, ["e", "f", "g", nil]),
          to.be({:a 1, :b 2, :c 3})
        )
      ),

      it("omits from empty", () ->
        expect(
          omit({}, ["e", "f", "g"]),
          to.be({})
        )
      ),

      it("omits from nil", () ->
        expect(
          omit(nil, []),
          to.be(nil)
        )
      ),

      it("omits using nil key list", () ->
        expect(
          omit({}, nil),
          to.be(nil)
        )
      ),

      it("non-castable key", () ->
        expect_error(
          () -> omit({:a1 1}, [()->true]),
          to.have_code("CAST_ERROR")
        )
      )

  ]);
}