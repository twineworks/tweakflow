import bin, math from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_float as f;

library spec {

  spec:
    describe("bin.of_float", [

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

        it("0.0 -> 0b00000000", () ->
          expect(f(0), to.be(0b00000000))
        ),

        it("1.0 -> 0b0000803f", () ->
          expect(f(1.0), to.be(0b0000803f))
        ),

        it("-1.0 -> 0b000080bf", () ->
          expect(f(-1.0),to.be(0b000080bf))
        ),

        it("math.pi -> 0bdb0f4940", () ->
          expect(f(math.pi), to.be(0bdb0f4940)) &&
          expect(bin.float_at(f(math.pi), 0), to.be_close_to(math.pi, 1e-7))
        ),

        it("NaN -> 0b0000c07f", () ->
          expect(f(NaN),to.be(0b0000c07f))
        ),

        it("Infinity -> 0b0000807f", () ->
          expect(f(Infinity),to.be(0b0000807f))
        ),

        it("-Infinity -> 0b000080ff", () ->
          expect(f(-Infinity),to.be(0b000080ff))
        ),

      ]),

      describe("big endian", [

        it("0.0 -> 0b00000000", () ->
          expect(f(0, true), to.be(0b00000000))
        ),

        it("1.0 -> 0b3f800000", () ->
          expect(f(1.0, true), to.be(0b3f800000))
        ),

        it("-1.0 -> 0bbf800000", () ->
          expect(f(-1.0, true),to.be(0bbf800000))
        ),

        it("math.pi -> 0b40490fdb", () ->
          expect(f(math.pi, true), to.be(0b40490fdb)) &&
          expect(bin.float_at(f(math.pi, true), 0, true), to.be_close_to(math.pi, 1e-7))
        ),

        it("NaN -> 0b7fc00000", () ->
          expect(f(NaN, true),to.be(0b7fc00000))
        ),

        it("Infinity -> 0b7f800000", () ->
          expect(f(Infinity, true),to.be(0b7f800000))
        ),

        it("-Infinity -> 0bff800000", () ->
          expect(f(-Infinity, true),to.be(0bff800000))
        ),
      ])

    ]);

}