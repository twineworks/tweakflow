import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_dword as f;

library spec {

  spec:
    describe("bin.of_dword", [

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

        it("0 -> 0b00000000", () ->
          expect(f(0), to.be(0b00000000))
        ),

        it("1 -> 0b01000000", () ->
          expect(f(1), to.be(0b01000000))
        ),

        it("64 -> 0b40000000", () ->
          expect(f(64), to.be(0b40000000))
        ),

        it("128 -> 0b80000000", () ->
          expect(f(128), to.be(0b80000000))
        ),

        it("192 -> 0bc0000000", () ->
          expect(f(192), to.be(0bc0000000))
        ),

        it("255 -> 0bff000000", () ->
          expect(f(255), to.be(0bff000000))
        ),

        it("305419896 -> 0b78563412", () ->
          expect(f(305419896), to.be(0b78563412))
        ),

        it("4294967295 -> 0bffffffff", () ->
          expect(f(4294967295), to.be(0bffffffff))
        ),

        it("-1 -> error", () ->
          expect_error(
            () -> f(-1),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("4294967296 -> error", () ->
          expect_error(
            () -> f(4294967296),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("unsigned big endian", [

        it("0 -> 0b00000000", () ->
          expect(f(0, big_endian: true), to.be(0b00000000))
        ),

        it("1 -> 0b00000001", () ->
          expect(f(1, big_endian: true), to.be(0b00000001))
        ),

        it("64 -> 0b00000040", () ->
          expect(f(64, big_endian: true), to.be(0b00000040))
        ),

        it("128 -> 0b00000080", () ->
          expect(f(128, big_endian: true), to.be(0b00000080))
        ),

        it("192 -> 0b000000c0", () ->
          expect(f(192, big_endian: true), to.be(0b000000c0))
        ),

        it("255 -> 0b000000ff", () ->
          expect(f(255, big_endian: true), to.be(0b000000ff))
        ),

        it("305419896 -> 0b12345678", () ->
          expect(f(305419896, big_endian: true), to.be(0b12345678))
        ),

        it("4294967295 -> 0bffffffff", () ->
          expect(f(4294967295, big_endian: true), to.be(0bffffffff))
        ),

        it("-1 -> error", () ->
          expect_error(
            () -> f(-1, big_endian: true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("4294967296 -> error", () ->
          expect_error(
            () -> f(4294967296, big_endian: true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),
      ]),

      describe("signed little endian", [

        it("0 -> 0b00000000", () ->
          expect(f(0, true), to.be(0b00000000))
        ),

        it("1 -> 0b01000000", () ->
          expect(f(1, true), to.be(0b01000000))
        ),

        it("64 -> 0b40000000", () ->
          expect(f(64, true), to.be(0b40000000))
        ),

        it("305419896 -> 0b78563412", () ->
          expect(f(305419896, true), to.be(0b78563412))
        ),

        it("-2147483648 -> 0b00000080", () ->
          expect(f(-2147483648, true), to.be(0b00000080))
        ),

        it("2147483647 -> 0bffffff7f", () ->
          expect(f(2147483647, true), to.be(0bffffff7f))
        ),

        it("-1 -> 0bffffffff", () ->
          expect(f(-1, true),to.be(0bffffffff))
        ),

        it("-32 -> 0be0ffffff", () ->
          expect(f(-32, true),to.be(0be0ffffff))
        ),

        it("-64 -> 0bc0ffffff", () ->
          expect(f(-64, true),to.be(0bc0ffffff))
        ),

        it("-128 -> 0b80ffffff", () ->
          expect(f(-128, true),to.be(0b80ffffff))
        ),

        it("−2147483649 -> error", () ->
          expect_error(
            () -> f(-2147483649, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("2147483648 -> error", () ->
          expect_error(
            () -> f(2147483648, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("4294967295 -> error", () ->
          expect_error(
            () -> f(4294967295, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

      ]),

      describe("signed big endian", [

        it("0 -> 0b00000000", () ->
          expect(f(0, true, true), to.be(0b00000000))
        ),

        it("1 -> 0b00000001", () ->
          expect(f(1, true, true), to.be(0b00000001))
        ),

        it("64 -> 0b00000040", () ->
          expect(f(64, true, true), to.be(0b00000040))
        ),

        it("305419896 -> 0b12345678", () ->
          expect(f(305419896, true, true), to.be(0b12345678))
        ),

        it("-2147483648 -> 0b80000000", () ->
          expect(f(-2147483648, true, true), to.be(0b80000000))
        ),

        it("2147483647 -> 0b7fffffff", () ->
          expect(f(2147483647, true, true), to.be(0b7fffffff))
        ),

        it("-1 -> 0bffffffff", () ->
          expect(f(-1, true, true),to.be(0bffffffff))
        ),

        it("-32 -> 0bffffffe0", () ->
          expect(f(-32, true, true),to.be(0bffffffe0))
        ),

        it("-64 -> 0bffffffc0", () ->
          expect(f(-64, true, true),to.be(0bffffffc0))
        ),

        it("-128 -> 0bffffff80", () ->
          expect(f(-128, true, true),to.be(0bffffff80))
        ),

        it("−2147483649 -> error", () ->
          expect_error(
            () -> f(-2147483649, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("2147483648 -> error", () ->
          expect_error(
            () -> f(2147483648, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

        it("4294967295 -> error", () ->
          expect_error(
            () -> f(4294967295, true, true),
            to.have_code("ILLEGAL_ARGUMENT")
          )
        ),

      ])

    ]);

}