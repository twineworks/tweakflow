import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.of_code_points as of_code_points;

library spec {
  spec:
    describe("strings.of_code_points", [

      it("of_default", () ->
        expect(of_code_points(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(of_code_points(nil), to.be_nil())
      ),

      it("simple", () ->
        expect(of_code_points([102, 111, 111]), to.be("foo"))
      ),

      it("simple_cast", () ->
        expect(of_code_points([102.1, 111.2, 111.3]), to.be("foo"))
      ),

      it("invalid_cast", () ->
        expect_error(
          () -> of_code_points([102, {}, 111]),
          to.have_code("CAST_ERROR")
        )
      ),

      it("code_point_out_of_range", () ->
        expect_error(
          () -> of_code_points([102, 0x7FFFFFFFFFFFFFFF, 111]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("invalid_code_point", () ->
        expect_error(
          () -> of_code_points([102, -1, 111]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("nil_code_point", () ->
        expect_error(
          () -> of_code_points([102, nil, 111]),
          to.have_code("NIL_ERROR")
        )
      ),

      it("bmp", () ->
        expect(of_code_points([20320, 22909]), to.be("你好"))
      ),

      it("beyond_bmp", () ->
        expect(of_code_points([0x4F60, 0x597D, 0x01D11E]), to.be("你好𝄞"))
      ),

      it("of_empty", () ->
        expect(of_code_points([]), to.be(""))
      ),

  ]);
}