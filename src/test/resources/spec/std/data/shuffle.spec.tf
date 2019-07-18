import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.shuffle as shuffle;

library spec {
  spec:
    describe("data.shuffle", [

      it("empty_list", () ->
        expect(shuffle([], 123), to.be([]))
      ),

      it("nil_shuffle", () ->
        expect(shuffle([1,2,3]), to.be([1,3,2]))
      ),

      it("foo_shuffle", () ->
        expect(shuffle([1,2,3,4,5], "foo"), to.be([2, 4, 3, 5, 1]))
      ),

      it("foo_shuffle_alt", () ->
        expect(shuffle([:a, :b, :c, :d, :e], "foo"), to.be([:b, :d, :c, :e, :a]))
      ),

      it("bar_shuffle", () ->
        expect(shuffle([1,2,3,4,5], "bar"), to.be([2, 3, 1, 5, 4]))
      ),

      it("bar_shuffle_alt", () ->
        expect(shuffle([:a, :b, :c, :d, :e], "bar"), to.be([:b, :c, :a, :e, :d]))
      ),

      it("of_nil", () ->
        expect(shuffle(nil), to.be_nil())
      ),

  ]);
}