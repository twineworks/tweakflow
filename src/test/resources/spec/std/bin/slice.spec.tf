import bin from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.slice as slice;

library spec {

  spec:
    describe("bin.slice", [

      it("empty_slice_of_empty", () ->
        expect(slice(0b, 0, 0), to.be(0b))
      ),

      it("empty_slice_of_some", () ->
        expect(slice(0b0001, 0, 0), to.be(0b))
      ),

      it("empty_slice_of_some_more", () ->
        expect(slice(0b0001, 1, 0), to.be(0b))
      ),

      it("whole_slice", () ->
        expect(slice(0b000102, 0), to.be(0b000102))
      ),

      it("head_slice", () ->
        expect(slice(0b000102, 0, 1), to.be(0b00))
      ),

      it("init_slice", () ->
        expect(slice(0b000102, 0, 2), to.be(0b0001))
      ),

      it("mid_slice", () ->
        expect(slice(0b00010203, 1, 3), to.be(0b0102))
      ),

      it("mid_empty_slice", () ->
        expect(slice(0b000102, 1, 1), to.be(0b))
      ),

      it("tail_slice", () ->
        expect(slice(0b000102, 1, 3), to.be(0b0102))
      ),

      it("last_slice", () ->
        expect(slice(0b000102, 2, nil), to.be(0b02))
      ),

      it("overextended_slice", () ->
        expect(slice(0b000102, 1, 100), to.be(0b0102))
      ),

      it("end_before_start_slice", () ->
        expect(slice(0b000102, 2, 1), to.be(0b))
      ),

      it("start_post_size_slice", () ->
        expect(slice(0b000102, 4), to.be(0b))
      ),

      it("of_default", () ->
        expect(slice(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(slice(nil, nil, nil), to.be_nil())
      ),

      it("of_nil_end", () ->
        expect(slice(nil, 0, nil), to.be_nil())
      ),

      it("of_nil_start", () ->
        expect_error(
          () -> slice(0b, nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_neg_start", () ->
        expect_error(
          () -> slice(0b, -1),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    ]);
}