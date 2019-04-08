import data, math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.compare as compare;
alias data.sort as sort;

library compare_spec {

  of_default:
    expect(compare(), to.be(0));

  of_same:
    expect(compare(12345, 12345), to.be(0));

  of_same_mixed:
    expect(compare(12345.0, 12345), to.be(0));

  of_a_lt_b:
    expect(compare(1, 2), to.be(-1));

  of_a_gt_b:
    expect(compare(2.0, 1.0), to.be(1));

  non_finite_sequencing:
    let {
      sorted: sort([Infinity, 2.5, 1, 0.2, -Infinity, NaN, nil], compare);
    }
    expect(sorted[0], to.be_nil()) &&
    expect(sorted[1], to.be_NaN()) &&
    expect(sorted[2], to.be(-Infinity)) &&
    expect(sorted[3], to.be(0.2)) &&
    expect(sorted[4], to.be(1)) &&
    expect(sorted[5], to.be(2.5)) &&
    expect(sorted[6], to.be(Infinity));

  of_non_numeric_a:
    expect_error(
      () -> compare("2.0", 1.0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_non_numeric_b:
    expect_error(
      () -> compare(2.0, "1.0"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}
