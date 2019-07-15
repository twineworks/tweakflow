import data from "std";
import expect, expect_error, to, describe, it from "std/spec.tf";

alias data.zip as zip;

library spec {

  spec: describe ("data.zip", [

    it ("zips empty lists", () ->
      expect(zip([], []), to.be([]))
    ),

    it ("zips same length lists", () ->
      expect(
        zip([1, 2, 3], [:a, :b, :c]),
        to.be([[1, :a], [2, :b], [3, :c]])
      )
    ),

    describe ("different length lists", [
      it ("xs longer than ys", () ->
        expect(
          zip([1,2,3], [:a, :b]),
          to.be([[1, :a], [2, :b], [3, nil]])
        )
      ),

      it ("ys longer than xs", () ->
        expect(
          zip([1,2,3], [:a, :b, :c, :d]),
          to.be([[1, :a], [2, :b], [3, :c]])
        )
      )
    ]),

    describe ("nil cases", [

      it ("xs are nil", () ->
        expect(
          zip(nil, []),
          to.be_nil()
        )
      ),

      it ("ys are nil", () ->
        expect(
          zip([], nil),
          to.be_nil()
        )
      )

    ]),

    describe("failing test", [
      it ("foo = bar", () ->
        expect("foo", to.equal("bar"))
      )
    ])


  ]);

}