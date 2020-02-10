import data as d, strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.compare as compare;

library spec {
  spec:
    describe("strings.compare", [

      it("of_default", () ->
        expect(compare(), to.be(0))
      ),

      it("A < a", () ->
        expect(compare("A", "a"), to.be(-1))
      ),

      it("foo > bar", () ->
        expect(compare("foo", "bar"), to.be(1))
      ),

      it("foo < foos", () ->
        expect(compare("foo", "foos"), to.be(-1))
      ),

      it("baba = baba", () ->
        expect(compare("baba", "baba"), to.be(0))
      ),

      it("nil < a", () ->
        expect(compare(nil, "a"), to.be(-1))
      ),

      it("a > nil", () ->
        expect(compare("a", nil), to.be(1))
      ),

      it("nil = nil", () ->
        expect(compare(nil, nil), to.be(0))
      ),

  ]);
}