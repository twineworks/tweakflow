import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_word as f;

library spec {

  spec:
    describe("bin.of_word", [

      it("signed nil -> error", () ->
        expect_error(
          () -> f(0, nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("big_endian nil -> error", () ->
        expect_error(
          () -> f(0, true, nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("on nil -> nil", () ->
        expect(f(nil), to.be_nil())
      ),

      describe("unsigned little endian", [

        it("0 -> 0b0000", () ->
          expect(f(0), to.be(0b0000))
        ),

        it("1 -> 0b0100", () ->
          expect(f(1), to.be(0b0100))
        ),

        it("64 -> 0b4000", () ->
          expect(f(64), to.be(0b4000))
        ),

        it("128 -> 0b8000", () ->
          expect(f(128), to.be(0b8000))
        ),

        it("192 -> 0bc000", () ->
          expect(f(192), to.be(0bc000))
        ),

        it("255 -> 0bff00", () ->
          expect(f(255), to.be(0bff00))
        ),

        it("4660 -> 0b3412", () ->
          expect(f(4660), to.be(0b3412))
        ),

        it("52719 -> 0befcd", () ->
          expect(f(52719), to.be(0befcd))
        ),

        it("65280 -> 0b00ff", () ->
          expect(f(65280), to.be(0b00ff))
        ),

        it("65535 -> 0bffff", () ->
          expect(f(65535), to.be(0bffff))
        ),

        it("-1 -> error", () ->
          expect_error(
            () -> f(-1),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65536 -> error", () ->
          expect_error(
            () -> f(65536),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("unsigned big endian", [

        it("0 -> 0b0000", () ->
          expect(f(0, big_endian: true), to.be(0b0000))
        ),

        it("1 -> 0b0001", () ->
          expect(f(1, big_endian: true), to.be(0b0001))
        ),

        it("64 -> 0b0040", () ->
          expect(f(64, big_endian: true), to.be(0b0040))
        ),

        it("128 -> 0b0080", () ->
          expect(f(128, big_endian: true), to.be(0b0080))
        ),

        it("192 -> 0b00c0", () ->
          expect(f(192, big_endian: true), to.be(0b00c0))
        ),

        it("255 -> 0b00ff", () ->
          expect(f(255, big_endian: true), to.be(0b00ff))
        ),

        it("4660 -> 0b1234", () ->
          expect(f(4660, big_endian: true), to.be(0b1234))
        ),

        it("52719 -> 0bcdef", () ->
          expect(f(52719, big_endian: true), to.be(0bcdef))
        ),

        it("65280 -> 0bff00", () ->
          expect(f(65280, big_endian: true), to.be(0bff00))
        ),

        it("65535 -> 0bffff", () ->
          expect(f(65535, big_endian: true), to.be(0bffff))
        ),

        it("-1 -> error", () ->
          expect_error(
            () -> f(-1, big_endian: true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65536 -> error", () ->
          expect_error(
            () -> f(65536, big_endian: true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("signed little endian", [

        it("0 -> 0b0000", () ->
          expect(f(0, true), to.be(0b0000))
        ),

        it("1 -> 0b0100", () ->
          expect(f(1, true), to.be(0b0100))
        ),

        it("64 -> 0b4000", () ->
          expect(f(64, true), to.be(0b4000))
        ),

        it("4660 -> 0b3412", () ->
          expect(f(4660, true), to.be(0b3412))
        ),

        it("-32768 -> 0b0080", () ->
          expect(f(-32768, true), to.be(0b0080))
        ),

        it("32767 -> 0bff7f", () ->
          expect(f(32767, true), to.be(0bff7f))
        ),

        it("32768 -> error", () ->
          expect_error(
            () -> f(32768, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65535 -> error", () ->
          expect_error(
            () -> f(65535, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("-1 -> 0bffff", () ->
          expect(f(-1, true),to.be(0bffff))
        ),

        it("-32 -> 0be0ff", () ->
          expect(f(-32, true),to.be(0be0ff))
        ),

        it("-64 -> 0bc0ff", () ->
          expect(f(-64, true),to.be(0bc0ff))
        ),

        it("-128 -> 0b80ff", () ->
          expect(f(-128, true),to.be(0b80ff))
        ),

        it("-32769 -> error", () ->
          expect_error(
            () -> f(-32769, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65536 -> error", () ->
          expect_error(
            () -> f(65536, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("signed big endian", [

        it("0 -> 0b0000", () ->
          expect(f(0, true, true), to.be(0b0000))
        ),

        it("1 -> 0b0001", () ->
          expect(f(1, true, true), to.be(0b0001))
        ),

        it("64 -> 0b0004", () ->
          expect(f(64, true, true), to.be(0b0040))
        ),

        it("-32768 -> 0b8000", () ->
          expect(f(-32768, true, true), to.be(0b8000))
        ),

        it("32767 -> 0b7fff", () ->
          expect(f(32767, true, true), to.be(0b7fff))
        ),

        it("32768 -> error", () ->
          expect_error(
            () -> f(32768, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65535 -> error", () ->
          expect_error(
            () -> f(65535, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("-1 -> 0bffff", () ->
          expect(f(-1, true, true),to.be(0bffff))
        ),

        it("-32 -> 0bffe0", () ->
          expect(f(-32, true, true),to.be(0bffe0))
        ),

        it("-64 -> 0bffc0", () ->
          expect(f(-64, true, true),to.be(0bffc0))
        ),

        it("-128 -> 0bff80", () ->
          expect(f(-128, true, true),to.be(0bff80))
        ),

        it("-32769 -> error", () ->
          expect_error(
            () -> f(-32769, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("65536 -> error", () ->
          expect_error(
            () -> f(65536, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ])

    ]);

}