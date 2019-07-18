import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.join as join;

library spec {
  spec:
    describe("strings.join", [

      it("of_default", () ->
        expect(join(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(join(nil), to.be_nil())
      ),

      it("simple", () ->
        expect(join(["foo", "bar"]), to.be("foobar"))
      ),

      it("with_nil", () ->
        expect(join(["foo", nil, "bar"], "-"), to.be("foo-nil-bar"))
      ),

      it("with_cast", () ->
        expect(join(["foo", true, "bar"], "-"), to.be("foo-true-bar"))
      ),

      it("with_bad_cast", () ->
        expect_error(
          () -> join(["foo", {}, "bar"], "-"),
          to.have_code("CAST_ERROR")
        )
      ),

      it("single", () ->
        expect(join(["foo"], "-"), to.be("foo"))
      ),

      it("empty", () ->
        expect(join([], "-"), to.be("")) &&
        expect(join([], ""), to.be(""))
      ),

      it("separator_nil", () ->
        expect(join(["foo"], nil), to.be_nil())
      ),

      it("with_separator", () ->
        expect(join(["foo", "bar"], ", "), to.be("foo, bar")) &&
        expect(join([1, 2, 3], "-"), to.be("1-2-3"))
      ),

  ]);
}