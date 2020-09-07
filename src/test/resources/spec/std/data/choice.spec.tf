import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.choice as choice;

library spec {
  spec:
    describe("data.choice", [

      it("empty_list", () ->
        expect(choice([], 123), to.be(nil))
      ),

      it("nil_seed", () ->
        expect(choice([1,2,3,4,5]), to.be(1))
      ),

      it("foo_seed", () ->
        expect(choice([1,2,3,4,5], "foo"), to.be(4))
      ),

      it("foo_seed_alt", () ->
        expect(choice([:a,:b,:c,:d,:e], "foo"), to.be(:d))
      ),

      it("bar_seed", () ->
        expect(choice([1,2,3,4,5], "bar"), to.be(2))
      ),

      it("bar_seed_alt", () ->
        expect(choice([:a,:b,:c,:d,:e], "bar"), to.be(:b))
      ),

      it("qed_seed", () ->
        expect(choice([1,2,3,4,5], "qed"), to.be(3))
      ),

      it("qed_seed_alt", () ->
        expect(choice([:a,:b,:c,:d,:e], "qed"), to.be(:c))
      ),

      it("of_list_nil", () ->
        expect(choice(nil, "seed"), to.be_nil())
      ),

  ]);
}