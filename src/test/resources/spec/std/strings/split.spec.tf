import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.split as split;

library spec {
  spec:
    describe("strings.split", [

      it("of_default", () ->
        expect(split(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(split(nil), to.be_nil())
      ),

      it("of_nil_splitter", () ->
        expect(split("foo bar", nil), to.be(nil))
      ),

      it("simple", () ->
        expect(split("foo bar"), to.be(["foo", "bar"]))
      ),

      it("splitter", () ->
        expect(split("foo,bar", ","), to.be(["foo", "bar"]))
      ),

      it("code_point_splitter", () ->
        expect(split("好你好你好", "你"), to.be(["好", "好", "好"]))
      ),

      it("code_point_beyond_bmp_splitter", () ->
        expect(split("好你𝄞好你𝄞好你", "𝄞"), to.be(["好你", "好你", "好你"]))
      ),

      it("multi_char_splitter", () ->
        expect(split("foo, bar", ", "), to.be(["foo", "bar"]))
      ),

      it("non_regex_splitter", () ->
        expect(split("foo.bar", "."), to.be(["foo", "bar"]))
      ),

      it("leading_splitter", () ->
        expect(split(",foo,bar", ","), to.be(["", "foo", "bar"]))
      ),

      it("trailing_splitter", () ->
        expect(split("foo,bar,", ","), to.be(["foo", "bar", ""]))
      ),

      it("consecutive_splitter", () ->
        expect(split(",,foo,,,,bar,,", ","), to.be(["", "", "foo", "", "", "", "bar", "", ""]))
      ),

      it("of_single", () ->
        expect(split("foo"), to.be(["foo"]))
      ),

      it("of_empty", () ->
        expect(split(""), to.be([""]))
      ),

  ]);
}