import regex from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias regex.scanning as scanning;

library p {
  clock: scanning('(\d{1,2}):(\d{2})( AM| PM)?');
}

library spec {
  spec:
    describe("regex.scanning", [

      it("times", () ->
        expect(
          p.clock("It was 9:30 AM when I arrived. And 4:35 PM when I left. At 8:30 next morning, I showed up again."),
          to.be([
            ["9:30 AM", "9", "30", " AM"],
            ["4:35 PM", "4", "35", " PM"],
            ["8:30", "8", "30", nil]
          ])
        )
      ),

      it("non_match", () ->
        expect(
          p.clock("helloween"),
          to.be([])
        )
      ),

      it("of_nil", () ->
        expect(
          p.clock(nil),
          to.be(nil)
        )
      ),

      it("nil_pattern", () ->
        expect_error(
          () -> scanning(nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("invalid_pattern", () ->
        expect_error(
          () -> scanning("[a"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}