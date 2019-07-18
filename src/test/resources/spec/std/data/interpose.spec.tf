import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.interpose as interpose;

library spec {
  spec:
    describe("data.interpose", [

      it("empty", () ->
        expect(interpose([], "a"), to.be([]))
      ),

      it("singleton", () ->
        expect(interpose([1], 0), to.be([1]))
      ),

      it("some", () ->
        expect(interpose([1,2,3], 0), to.be([1, 0, 2, 0, 3]))
      ),

      it("of_nil", () ->
        expect(interpose(nil, 1), to.be_nil())
      ),

  ]);
}