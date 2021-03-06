import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.key_of as key_of;


library spec {
  spec:
    describe("data.key_of", [

      it("empty", () ->
        expect(key_of({}, 1), to.be_nil())
      ),

      it("found", () ->
        expect(key_of({:a 1, :b 2}, 1), to.be(:a))
      ),

      it("not_found", () ->
        expect(key_of({:a 1, :b 2}, 4), to.be_nil())
      ),

      it("function_not_found", () ->
        let {
          f: () -> true;
        }
        expect(key_of({:a f, :b f}, f), to.be_nil())
      ),

      it("nan_not_found", () ->
        expect(key_of({:a NaN, :b NaN}, NaN), to.be_nil())
      ),

      it("equivalent_not_found", () ->
        expect(key_of({:a 1, :b 2}, 1.0), to.be_nil())
      ),

      it("found_any", () ->
        expect(key_of({:alpha 1, :beta 1, :gamma 1}, 1), to.be_one_of([:alpha, :beta, :gamma]))
      ),

      it("of_nil", () ->
        expect(key_of(nil, 1), to.be_nil())
      ),

    ]);
}