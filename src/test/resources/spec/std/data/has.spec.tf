import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.has? as has?;


library spec {
  spec:
    describe("has?", [

      it("missing_list", () ->
        expect(has?([0, nil, 3], 3), to.be_false())
      ),

      it("simple_list", () ->
        expect(has?([1,nil,3], 0), to.be_true())
      ),

      it("nil_entry_list", () ->
        expect(has?([1,nil,3], 1), to.be_true())
      ),

      it("missing_dict", () ->
        expect(has?({:a 1, :b 2}, :c), to.be_false())
      ),

      it("simple_dict", () ->
        expect(has?({:a 1, :b 2}, :b), to.be_true())
      ),

      it("nil_entry_dict", () ->
        expect(has?({:a 1, :b nil}, :b), to.be_true())
      ),

      it("of_nil", () ->
        expect(has?(nil, :a), to.be_nil())
      ),

      it("invalid_xs_type", () ->
        expect_error(
          () -> has?("foo", 0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("invalid_key_type_list", () ->
        expect_error(
          () -> has?([], "foo"),
          to.have_code("CAST_ERROR")
        )
      ),

      it("invalid_key_type_dict", () ->
        expect_error(
          () -> has?({}, []),
          to.have_code("CAST_ERROR")
        )
      ),
    ]);
  }