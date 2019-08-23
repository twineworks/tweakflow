import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_byte as f;

library spec {

  spec:
    describe("bin.of_byte", [

      it("signed nil -> error", () ->
        expect_error(
          () -> f(0, nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("on nil -> nil", () ->
        expect(f(nil), to.be_nil())
      ),

      describe("unsigned", [

        it("0 -> 0b00", () ->
          expect(f(0), to.be(0b00))
        ),

        it("1 -> 0b01", () ->
          expect(f(1), to.be(0b01))
        ),

        it("64 -> 0b40", () ->
          expect(f(64), to.be(0b40))
        ),

        it("128 -> 0b80", () ->
          expect(f(128), to.be(0b80))
        ),

        it("192 -> 0bc0", () ->
          expect(f(192), to.be(0bc0))
        ),

        it("255 -> 0bff", () ->
          expect(f(255), to.be(0bff))
        ),

        it("-1 -> error", () ->
          expect_error(
            () -> f(-1),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("256 -> error", () ->
          expect_error(
            () -> f(256),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("signed", [

        it("0 -> 0b00", () ->
          expect(f(0, true), to.be(0b00))
        ),

        it("1 -> 0b01", () ->
          expect(f(1, true), to.be(0b01))
        ),

        it("64 -> 0b40", () ->
          expect(f(64, true), to.be(0b40))
        ),

        it("128 -> error", () ->
          expect_error(
            () -> f(128, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("255 -> error", () ->
          expect_error(
            () -> f(255, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("-1 -> 0bff", () ->
          expect(f(-1, true),to.be(0bff))
        ),

        it("-32 -> 0be0", () ->
          expect(f(-32, true),to.be(0be0))
        ),

        it("-64 -> 0bc0", () ->
          expect(f(-64, true),to.be(0bc0))
        ),

        it("-128 -> 0bc0", () ->
          expect(f(-128, true),to.be(0b80))
        ),

        it("-129 -> error", () ->
          expect_error(
            () -> f(-129, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("256 -> error", () ->
          expect_error(
            () -> f(256, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ])

    ]);

}