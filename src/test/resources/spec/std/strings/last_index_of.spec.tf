import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.last_index_of as last_index_of;

library spec {
  spec:
    describe("strings.last_index_of", [

      it("of_default", () ->
        expect(last_index_of(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(last_index_of(nil, nil, nil), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(last_index_of(nil, "o"), to.be_nil())
      ),

      it("of_sub_nil", () ->
        expect(last_index_of("foo", nil), to.be_nil())
      ),

      it("of_index_nil", () ->
        expect(last_index_of("foo", "o", nil), to.be(2))
      ),

      it("simple_found", () ->
        expect(last_index_of("foo", "o"), to.be(2))
      ),

      it("simple_found_start", () ->
        expect(last_index_of("foo", "fo"), to.be(0))
      ),

      it("simple_not_found", () ->
        expect(last_index_of("foo", "b"), to.be(-1))
      ),

      it("empty_found", () ->
        expect(last_index_of("foo", ""), to.be(3))
      ),

      it("empty_found_in_empty", () ->
        expect(last_index_of("", ""), to.be(0))
      ),

      it("found_at_index", () ->
        expect(last_index_of("for", "o", 1), to.be(1))
      ),

      it("found_at_end_index_low", () ->
        expect(last_index_of("fof", "f", 0), to.be(0))
      ),

      it("found_at_end_index_high", () ->
        expect(last_index_of("fof", "f", 2), to.be(2))
      ),

      it("found_before_index", () ->
        expect(last_index_of("hello world", "o", 5), to.be(4))
      ),

      it("not_found_at_sub_zero_index", () ->
        expect(last_index_of("for", "o", -99), to.be(-1))
      ),

      it("found_at_past_end_index", () ->
        expect(last_index_of("for", "o", 99), to.be(1))
      ),

      it("found_at_last_index", () ->
        expect(last_index_of("for", "r", 2), to.be(2))
      ),

  ]);
}