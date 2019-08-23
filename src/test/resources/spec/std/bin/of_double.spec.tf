import bin, math from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_double as f;

library spec {

  spec:
    describe("bin.of_double", [

      it("big_endian nil -> error", () ->
        expect_error(
          () -> f(0, nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("on nil -> nil", () ->
        expect(f(nil), to.be_nil())
      ),

      describe("little endian", [

        it("0.0 -> 0b0000000000000000", () ->
          expect(f(0), to.be(0b0000000000000000))
        ),

        it("1.0 -> 0b000000000000f03f", () ->
          expect(f(1.0), to.be(0b000000000000f03f))
        ),

        it("-1.0 -> 0b000000000000f0bf", () ->
          expect(f(-1.0),to.be(0b000000000000f0bf))
        ),

        it("math.pi -> 0b182d4454fb210940", () ->
          expect(f(math.pi), to.be(0b182d4454fb210940)) &&
          expect(bin.double_at(f(math.pi), 0), to.be(math.pi))
        ),

        it("NaN -> 0b000000000000f87f", () ->
          expect(f(NaN),to.be(0b000000000000f87f))
        ),

        it("Infinity -> 0b000000000000f07f", () ->
          expect(f(Infinity),to.be(0b000000000000f07f))
        ),

        it("-Infinity -> 0b000000000000f0ff", () ->
          expect(f(-Infinity),to.be(0b000000000000f0ff))
        ),

      ]),

      describe("big endian", [

        it("0.0 -> 0b0000000000000000", () ->
          expect(f(0, true), to.be(0b0000000000000000))
        ),

        it("1.0 -> 0b3ff0000000000000", () ->
          expect(f(1.0, true), to.be(0b3ff0000000000000))
        ),

        it("-1.0 -> 0bbff0000000000000", () ->
          expect(f(-1.0, true),to.be(0bbff0000000000000))
        ),

        it("math.pi -> 0b400921fb54442d18", () ->
          expect(f(math.pi, true), to.be(0b400921fb54442d18)) &&
          expect(bin.double_at(f(math.pi, true), 0, true), to.be(math.pi))
        ),

        it("NaN -> 0b7ff8000000000000", () ->
          expect(f(NaN, true),to.be(0b7ff8000000000000))
        ),

        it("Infinity -> 0b7ff0000000000000", () ->
          expect(f(Infinity, true),to.be(0b7ff0000000000000))
        ),

        it("-Infinity -> 0bfff0000000000000", () ->
          expect(f(-Infinity, true),to.be(0bfff0000000000000))
        ),
      ])

    ]);

}