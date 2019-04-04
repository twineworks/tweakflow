import regex from "std";
import expect, expect_error, to from "std/assert.tf";

alias regex.splitting as splitting;

library p {
  csv: splitting(',\s*');
  csv_tr: splitting(',\s*', -1);
  csv_3: splitting(',\s*', 3);
  group_3: splitting('(?<=\G.{3})'); # zero width lookbehind, matches after 3 chars since last match
}

library splitting_spec {

  csv_line:
    expect(
      p.csv("1, 2, 3,4,"),
      to.be(["1", "2", "3", "4"])
    );

  csv_line_mid_empty:
    expect(
      p.csv("1, 2,,3,4,"),
      to.be(["1", "2", "", "3", "4"])
    );

  csv_line_trailing:
    expect(
      p.csv_tr("1, 2, 3, 4, "),
      to.be(["1", "2", "3", "4", ""])
    );

  csv_line_3:
    expect(
      p.csv_3("1, 2, 3, 4"),
      to.be(["1", "2", "3, 4"])
    );

  non_match:
    expect(
      p.csv("yay"),
      to.be(["yay"])
    );

  group:
    expect(
      p.group_3("abcdefghij"),
      to.be(["abc", "def", "ghi", "j"])
    );

  of_nil:
    expect(
      p.csv(nil),
      to.be(nil)
    );

  nil_pattern:
    expect_error(
      () -> splitting(nil),
      to.have_code("NIL_ERROR")
    );

  invalid_pattern:
    expect_error(
      () -> splitting("[a"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}