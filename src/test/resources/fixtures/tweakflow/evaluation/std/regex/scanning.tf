import regex from "std";
import expect, expect_error, to from "std/assert.tf";

alias regex.scanning as scanning;

library p {
  clock: scanning('(\d{1,2}):(\d{2})( AM| PM)?');
}

library scanning_spec {
  times:
    expect(
      p.clock("It was 9:30 AM when I arrived. And 4:35 PM when I left. At 8:30 next morning, I showed up again."),
      to.be([
        ["9:30 AM", "9", "30", " AM"],
        ["4:35 PM", "4", "35", " PM"],
        ["8:30", "8", "30", nil]
      ])
    );

  non_match:
    expect(
      p.clock("helloween"),
      to.be([])
    );

  of_nil:
    expect(
      p.clock(nil),
      to.be(nil)
    );

  nil_pattern:
    expect_error(
      () -> scanning(nil),
      to.have_code("NIL_ERROR")
    );

  invalid_pattern:
    expect_error(
      () -> scanning("[a"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}