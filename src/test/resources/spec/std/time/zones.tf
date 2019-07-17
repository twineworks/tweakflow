import data, time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.zones as zones;
alias data.contains? as contains?;

library spec {
  spec:
    describe("zones", [


  has_expected_zones:
    let {
      z: zones();
      has?: (x) -> contains?(z, x);
    }
    expect(z is list, to.be_true()) &&
    expect(has?("UTC"), to.be_true()) &&
    expect(has?("Europe/Athens"), to.be_true()) &&
    expect(has?("Europe/Berlin"), to.be_true()) &&
    expect(has?("America/New_York"), to.be_true()) &&
    expect(has?("US/Alaska"), to.be_true()) &&
    expect(has?("Africa/Cairo"), to.be_true()) &&
    expect(has?("Asia/Tokyo"), to.be_true());

  ]);
}