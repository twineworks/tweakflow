import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.prepend as prepend;

library spec {
  spec:
    describe("data.prepend", [

      it("empty_list", () ->
        expect(prepend("x", []), to.be(["x"]))
      ),

      it("simple_list", () ->
        expect(prepend("x", [1, nil ,3]), to.be(["x", 1, nil, 3]))
      ),

      it("nil_entry", () ->
        expect(prepend(nil, [1, nil ,3]), to.be([nil, 1, nil, 3]))
      ),

      it("of_nil", () ->
        expect(prepend(1, nil), to.be_nil())
      ),

  ]);

}