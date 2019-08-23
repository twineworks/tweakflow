import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.of_long as f;

library spec {

  spec:
    describe("bin.of_long", [

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

        it("0 -> 0b0000000000000000", () ->
          expect(f(0), to.be(0b0000000000000000))
        ),

        it("1 -> 0b0100000000000000", () ->
          expect(f(1), to.be(0b0100000000000000))
        ),

        it("64 -> 0b4000000000000000", () ->
          expect(f(64), to.be(0b4000000000000000))
        ),

        it("305419896 -> 0b7856341200000000", () ->
          expect(f(305419896), to.be(0b7856341200000000))
        ),

        it("-2147483648 -> 0b00000080ffffffff", () ->
          expect(f(-2147483648), to.be(0b00000080ffffffff))
        ),

        it("2147483647 -> 0bffffff7f00000000", () ->
          expect(f(2147483647), to.be(0bffffff7f00000000))
        ),

        it("1311768467463790320 -> 0bF0DEBC9A78563412", () ->
          expect(f(1311768467463790320), to.be(0bF0DEBC9A78563412))
        ),

        it("-9223372036854775808 -> 0b0000000000000080", () ->
          expect(f(-9223372036854775808), to.be(0b0000000000000080))
        ),

        it("9223372036854775807 -> 0bffffffffffffff7f", () ->
          expect(f(9223372036854775807), to.be(0bffffffffffffff7f))
        ),

        it("-1 -> 0bffffffffffffffff", () ->
          expect(f(-1),to.be(0bffffffffffffffff))
        ),

        it("-32 -> 0be0ffffffffffffff", () ->
          expect(f(-32),to.be(0be0ffffffffffffff))
        ),

        it("-64 -> 0bc0ffffffffffffff", () ->
          expect(f(-64),to.be(0bc0ffffffffffffff))
        ),

        it("-128 -> 0b80ffffffffffffff", () ->
          expect(f(-128),to.be(0b80ffffffffffffff))
        ),

        it("-1311768467463790320 -> 0b1021436587a9cbed", () ->
          expect(f(-1311768467463790320), to.be(0b1021436587a9cbed))
        ),

      ]),

      describe("big endian", [

        it("0 -> 0b0000000000000000", () ->
          expect(f(0, true), to.be(0b0000000000000000))
        ),

        it("1 -> 0b0000000000000001", () ->
          expect(f(1, true), to.be(0b0000000000000001))
        ),

        it("64 -> 0b0000000000000040", () ->
          expect(f(64, true), to.be(0b0000000000000040))
        ),

        it("305419896 -> 0b0000000012345678", () ->
          expect(f(305419896, true), to.be(0b0000000012345678))
        ),

        it("-2147483648 -> 0bffffffff80000000", () ->
          expect(f(-2147483648, true), to.be(0bffffffff80000000))
        ),

        it("2147483647 -> 0b000000007fffffff", () ->
          expect(f(2147483647, true), to.be(0b000000007fffffff))
        ),

        it("1311768467463790320 -> 0b123456789abcdef0", () ->
          expect(f(1311768467463790320, true), to.be(0b123456789abcdef0))
        ),

        it("-9223372036854775808 -> 0b8000000000000000", () ->
          expect(f(-9223372036854775808, true), to.be(0b8000000000000000))
        ),

        it("9223372036854775807 -> 0b7fffffffffffffff", () ->
          expect(f(9223372036854775807, true), to.be(0b7fffffffffffffff))
        ),

        it("-1 -> 0bffffffffffffffff", () ->
          expect(f(-1, true),to.be(0bffffffffffffffff))
        ),

        it("-32 -> 0bffffffffffffffe0", () ->
          expect(f(-32, true),to.be(0bffffffffffffffe0))
        ),

        it("-64 -> 0bffffffffffffffc0", () ->
          expect(f(-64, true),to.be(0bffffffffffffffc0))
        ),

        it("-128 -> 0bffffffffffffff80", () ->
          expect(f(-128, true),to.be(0bffffffffffffff80))
        ),

        it("-1311768467463790320 -> 0bedcba98765432110", () ->
          expect(f(-1311768467463790320, true), to.be(0bedcba98765432110))
        ),

      ])

    ]);

}