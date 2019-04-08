import time as t, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.compare as compare;

library compare_spec {

  of_default:
    expect(compare(), to.be(0));

  of_same:
    expect(compare(t.epoch, t.epoch), to.be(0));

  of_nil_some:
    expect(compare(nil, t.epoch), to.be(-1));

  of_some_nil:
    expect(compare(t.epoch, nil), to.be(1));

  of_a_lt_b:
    expect(compare(t.add_duration(t.epoch, -1), t.epoch), to.be(-1));

  of_a_gt_b:
    expect(compare(t.add_duration(t.epoch, 1), t.epoch), to.be(1));

  of_same_in_different_tz:
    expect(
      compare(
        t.same_instant_at_zone(t.epoch, "America/New_York"),
        t.same_instant_at_zone(t.epoch, "Europe/Berlin")
      ),
      to.be(0)
    );

}