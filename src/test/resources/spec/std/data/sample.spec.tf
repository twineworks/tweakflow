import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.sample as sample;

library spec {
  spec:
    describe("data.sample", [

      it("empty_list", () ->
        expect(sample([], 10, true, 123), to.be([]))
      ),

      it("nil_seed_no_return", () ->
        expect(sample([1,2,3,4,5], 5, false), to.be([5, 3, 2, 4, 1]))
      ),

      it("nil_seed_no_return_oversized", () ->
        expect(sample([1,2,3,4,5], 10, false), to.be([5, 3, 2, 4, 1]))
      ),

      it("nil_seed_with_return", () ->
        expect(sample([1,2,3,4,5], 5, true), to.be([1, 4, 5, 3, 1]))
      ),

      it("nil_seed_with_return_oversized", () ->
        expect(sample([1,2,3,4,5], 10, true), to.be([1, 4, 5, 3, 1, 4, 2, 2, 5, 5]))
      ),

      it("foo_seed_no_return", () ->
        expect(sample([1,2,3,4,5], 4, false, "foo"), to.be([2, 5, 3, 1]))
      ),

      it("foo_seed_no_return_alt", () ->
        expect(sample([:a, :b, :c, :d, :e], 4, false, "foo"), to.be([:b, :e, :c, :a]))
      ),

      it("bar_seed_no_return", () ->
        expect(sample([1,2,3,4,5], 4, false, "bar"), to.be([1, 4, 3, 5]))
      ),

      it("bar_seed__no_return_alt", () ->
        expect(sample([:a, :b, :c, :d, :e], 4, false, "bar"), to.be([:a, :d, :c, :e]))
      ),

      it("of_list_nil", () ->
        expect(sample(nil, 2, true), to.be_nil())
      ),

      it("of_count_nil", () ->
        expect(sample([1,2,3], nil, true), to.be_nil())
      ),

      it("of_with_return_nil", () ->
        expect(sample([1,2,3], 1, nil), to.be_nil())
      ),

  ]);
}