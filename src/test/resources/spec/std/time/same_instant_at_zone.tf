import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.same_instant_at_zone as same_instant_at_zone;

library spec {
  spec:
    describe("same_instant_at_zone", [


  of_default:
    expect(same_instant_at_zone(), to.be_nil());

  of_x_nil:
    expect(same_instant_at_zone(nil, "UTC"), to.be_nil());

  of_zone_nil:
    expect(same_instant_at_zone(t.epoch, nil), to.be_nil());

  epoch_new_york:
    expect(
      same_instant_at_zone(t.epoch, "America/New_York"),
      to.be(1969-12-31T19:00:00-05:00@America/New_York)
    );

  epoch_berlin:
    expect(
      same_instant_at_zone(t.epoch, "Europe/Berlin"),
      to.be(1970-01-01T01:00:00+01:00@Europe/Berlin)
    );

  unknown_zone:
    expect_error(
      () -> same_instant_at_zone(t.epoch, 'foo'),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
  ]);
}