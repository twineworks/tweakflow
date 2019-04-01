import data as d, strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.comparator as comparator;

library comparator_spec {

  of_default:
    let {
      c: comparator();
    }
    expect(c, to.be_function()) &&
    expect(c("a", "b"), to.be(-1)) &&
    expect(c("b", "a"), to.be(1)) &&
    expect(c("a", "a"), to.be(0));

  of_nil:
    expect_error(
      () -> comparator(nil),
      to.have_code("NIL_ERROR")
    );

  case_sensitive:
    let {
      c: comparator();
    }
    expect(c("A", "a"), to.be(1));

  case_insensitive:
    let {
      c: comparator(case_sensitive: false);
    }
    expect(c("A", "a"), to.be(0));

  localized:
    let {
      c_de: comparator("de-DE");  # german, sorts ä after a
      c_sv: comparator("sv-SE");  # swedish, sorts ä at end of alphabet
    }
    expect(c_de("bb", "bä"), to.be(1)) &&
    expect(c_sv("bb", "bä"), to.be(-1));

  localized_case_sensitive:
    let {
      c_de: comparator("de-DE", true);  # german, sorts ä after a
      c_sv: comparator("sv-SE", true);  # swedish, sorts ä at end of alphabet
      data: ["A", "Ab", "Äb", "Ba", "ba", "ab", "äb", "a", "ä"];
      data_de: d.sort(data, c_de);
      data_sv: d.sort(data, c_sv);
    }
    expect(data_de, to.be(["a", "A", "ä", "ab", "Ab", "äb", "Äb", "ba", "Ba"])) &&
    expect(data_sv, to.be(["a", "A", "ab", "Ab", "ba", "Ba", "ä", "äb", "Äb"]));

  localized_case_insensitive:
    let {
      c_de: comparator("de-DE", false);  # german, sorts ä after a
      c_sv: comparator("sv-SE", false);  # swedish, sorts ä at end of alphabet
      data: ["A", "Ab", "Äb", "Ba", "ba", "ab", "äb", "a", "ä"];
      data_de: d.sort(data, c_de);
      data_sv: d.sort(data, c_sv);
    }
    # sort is stable, so uppercase/lowercase equivalents are sorted in order of
    # appearance in original data
    expect(data_de, to.be(["A", "a", "ä", "Ab", "ab", "Äb", "äb", "Ba", "ba"])) &&
    expect(data_sv, to.be(["A", "a", "Ab", "ab", "Ba", "ba", "ä", "Äb", "äb"]));

}