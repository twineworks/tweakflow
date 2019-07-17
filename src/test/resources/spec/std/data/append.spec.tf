import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.append as append;

library spec {
  spec:
    describe("data.append", [

      it("empty_list", () ->
        expect(append([], "x"), to.be(["x"]))
      ),

      it("simple_list", () ->
        expect(append([1, nil ,3], "x"), to.be([1, nil, 3, "x"]))
      ),

      it("nil_entry", () ->
        expect(append([1, nil ,3], nil), to.be([1, nil, 3, nil]))
      ),

      it("of_nil", () ->
        expect(append(nil, 1), to.be_nil())
      ),

    ]);
}