import core, data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias core.hash as hash;

library spec {

  spec: describe("core.hash", [

    subject(core.hash),

    it("is a function", (f) ->
      expect(f, to.be_function())
    ),

    it("hash(nil) == 0", (f) ->
      expect(f(nil), to.be(0))
    ),

    it("hash(true) == 1", (f) ->
      expect(f(true), to.be(1))
    ),

    it("hash(false) == 0", (f) ->
      expect(f(false), to.be(0))
    ),

    it("hash(0) == 0", (f) ->
      expect(f(0), to.be(0))
    ),

    it("hash(1) == 1072693248", (f) ->
      expect(f(1), to.be(1072693248))
    ),

    it("hash('a') == 97", (f) ->
      expect(f("a"), to.be(97))
    ),

    it("hash(0d) == 0", (f) ->
      expect(f(0d), to.be(0))
    ),

    it("hash(0b) == 1", (f) ->
      expect(f(0b), to.be(1))
    ),

    it("hash([]) == 1", (f) ->
      expect(f([]), to.be(1))
    ),

    it("hash({}) == 0", (f) ->
      expect(f({}), to.be(0))
    ),

    describe("hashes of equal numbers", [

      it("hash(1.0) == hash(1) == hash(1d)", (f) ->
        expect(f(1), to.be(f(1.0))) &&
        expect(f(1), to.be(f(1d)))
      ),

      it("hash(-1.0) == hash(-1) == hash(-1d)", (f) ->
        expect(f(-1), to.be(f(-1.0))) &&
        expect(f(-1), to.be(f(-1d)))
      ),

      it("hash(999.0) == hash(999) == hash(999d)", (f) ->
        expect(f(999), to.be(f(999.0))) &&
        expect(f(999), to.be(f(999d)))
      ),

    ]),

    describe("hashes of equal lists", [
      it("hash([1, 2, 3]) == hash([1.0, 2.0, 3d])", (f) ->
        expect(f([1, 2, 3]), to.be(f([1.0, 2.0, 3d])))
      ),

      it("hash([1, 2d, NaN]) == hash([1d, 2.0, NaN])", (f) ->
        expect(f([1, 2d, NaN]), to.be(f([1d, 2.0, NaN])))
      ),
    ]),

    describe("hashes of equal dicts", [
      it("hash({:a 1, :b 2, :c 3}) == hash({:a 1.0, :b 2d, :c 3.0})", (f) ->
        expect(f({:a 1, :b 2, :c 3}), to.be(f({:a 1.0, :b 2d, :c 3.0})))
      ),
      it("hash({:a 1, :b 2, :c NaN}) == hash({:a 1.0, :b 2d, :c NaN})", (f) ->
        expect(f({:a 1, :b 2, :c NaN}), to.be(f({:a 1.0, :b 2d, :c NaN})))
      ),
    ]),

  ]);
}