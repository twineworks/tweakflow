import data, time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.zones as zones;
alias data.contains? as contains?;

library zones_spec {

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

}