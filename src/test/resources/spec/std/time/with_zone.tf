import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_zone as with_zone;

library spec {
  spec:
    describe("with_zone", [


  of_default:
    expect(with_zone(), to.be_nil());

  of_x_nil:
    expect(with_zone(nil, 2020), to.be_nil());

  of_zone_nil:
    expect(with_zone(t.epoch, nil), to.be_nil());

  with_berlin:
    expect(with_zone(t.epoch, 'Europe/Berlin'),
    to.be(1970-01-01T00:00:00+01:00@Europe/Berlin)
  );

  with_new_york:
    expect(with_zone(t.epoch, 'America/New_York'),
    to.be(1970-01-01T00:00:00-05:00@America/New_York)
  );

  with_change_into_dst_gap:
    expect(with_zone(2019-03-31T02:30:00, 'Europe/Berlin'),
    to.be(2019-03-31T03:30:00+02:00@Europe/Berlin)
  );

  with_change_into_dst_overlap:
    expect(with_zone(2019-10-27T02:30:00, 'Europe/Berlin'),
    to.be(2019-10-27T02:30:00+02:00@Europe/Berlin)
  );

  unknown_zone:
    expect_error(
      () -> with_zone(t.epoch, 'foo'),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}