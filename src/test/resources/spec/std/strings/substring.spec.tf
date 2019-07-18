import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.substring as substring;

library spec {
  spec:
    describe("strings.substring", [

      it("of_default", () ->
        substring() === nil
      ),

      it("of_empty", () ->
        expect(substring(""), to.be(""))
      ),

      it("simple", () ->
        expect(substring("hello world", 6), to.be("world"))
      ),

      it("all", () ->
        expect(substring("hello world", 0), to.be("hello world"))
      ),

      it("beyond", () ->
        expect(substring("hello world", 0, 99), to.be("hello world"))
      ),

      it("recess", () ->
        expect(substring("hello world", 0, -1), to.be(""))
      ),

      it("excess", () ->
        expect(substring("hello world", 10, 1), to.be(""))
      ),

      it("zero_len", () ->
        expect(substring("hello world", 0, 0), to.be(""))
      ),

      it("zero_len_offset", () ->
        expect(substring("hello world", 2, 2), to.be(""))
      ),

      it("zero_len_beyond", () ->
        expect(substring("hello world", 99, 99), to.be(""))
      ),

      it("respects_code_point_1", () ->
        expect(substring("你好", 0, 1), to.be("你"))
      ),

      it("respects_code_point_2", () ->
        expect(substring("你好", 1, 2), to.be("好"))
      ),

      it("respects_code_point_beyond_bmp", () ->
        expect(substring("你好 𝄞 你好", 1, 6), to.be("好 𝄞 你"))
      ),

      it("with_start_sub_zero", () ->
        expect_error(
          () -> substring("foo", -1),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),

      it("with_start_nil", () ->
        expect_error(
          () -> substring("foo", nil),
          to.have_code("NIL_ERROR")
        )
      ),

  ]);
}