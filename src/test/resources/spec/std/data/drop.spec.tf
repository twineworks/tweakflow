import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.drop as drop;

library spec {
  spec:
    describe("data.drop", [

      it("drop_0_of_empty", () ->
        expect(drop(0, []), to.be([]))
      ),

      it("drop_1_of_empty", () ->
        expect(drop(1, []), to.be([]))
      ),

      it("drop_2_of_empty", () ->
        expect(drop(2, []), to.be([]))
      ),

      it("drop_neg_1_of_empty", () ->
        expect(drop(-1, []), to.be([]))
      ),

      it("drop_0_of_one", () ->
        expect(drop(0, [1]), to.be([1]))
      ),

      it("drop_1_of_one", () ->
        expect(drop(1, [1]), to.be([]))
      ),

      it("drop_2_of_one", () ->
        expect(drop(2, [1]), to.be([]))
      ),

      it("drop_neg_1_of_one", () ->
        expect(drop(-1, [1]), to.be([1]))
      ),

      it("drop_0_of_some", () ->
        expect(drop(0, [1, 2, 3]), to.be([1, 2, 3]))
      ),

      it("drop_1_of_some", () ->
        expect(drop(1, [1, 2, 3]), to.be([2, 3]))
      ),

      it("drop_2_of_some", () ->
        expect(drop(2, [1, 2, 3]), to.be([3]))
      ),

      it("drop_neg_1_of_some", () ->
        expect(drop(-1, [1, 2, 3]), to.be([1, 2, 3]))
      ),

      it("of_default", () ->
        expect(drop(), to.be_nil())
      ),

      it("nil_items", () ->
        expect(drop(nil, []), to.be_nil())
      ),

      it("from_nil", () ->
        expect(drop(0, nil), to.be_nil())
      ),

    ]);
}