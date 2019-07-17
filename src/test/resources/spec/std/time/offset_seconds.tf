import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.offset_seconds as offset_seconds;

library spec {
  spec:
    describe("offset_seconds", [


  of_default:
    expect(offset_seconds(), to.be_nil());

  of_x_nil:
    expect(offset_seconds(nil), to.be_nil());

  of_epoch:
    expect(offset_seconds(t.epoch), to.be(0));

  of_specific_offset:
    expect(offset_seconds(2010-01-01T00:00:00+02:00), to.be(7200));

  of_neg_specific_offset:
    expect(offset_seconds(2010-01-01T00:00:00-02:00), to.be(-7200));

  of_zone_offset_winter_time:
    expect(
      offset_seconds(t.with_zone(2010-01-01T00:00:00, "Europe/Berlin")),
      to.be(3600)
    );

  of_zone_offset_summer_time:
    expect(
      offset_seconds(t.with_zone(2010-06-01T00:00:00, "Europe/Berlin")),
      to.be(7200)
    );

  ]);
}