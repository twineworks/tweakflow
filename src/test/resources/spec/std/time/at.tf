import time as t, math from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.at as at;

library spec {
  spec:
    describe("at", [


  at_default:
    expect(at(), to.be(nil));

  at_only_date:
    expect(
      at(t.epoch),
      to.be(t.epoch)
    );

  at_datetime:
    expect(
      at(2019-04-10T23:11:32),
      to.be(2019-04-10T00:00:00)
    );

  at_date:
    expect(
      at(2019-04-10T, 23, 11, 32),
      to.be(2019-04-10T23:11:32)
    );

  at_ns:
    expect(
      at(2019-04-10T, 23, 11, 32, 999000000),
      to.be(2019-04-10T23:11:32.999)
    );

  in_dst_gap:
    expect(
      at(2019-03-31T00:00:00+01:00@Europe/Berlin, 2, 30),
      to.be(2019-03-31T03:30:00+02:00@Europe/Berlin)
    );

  in_dst_overlap:
    expect(
      at(2019-10-27T00:00:00+02:00@Europe/Berlin, 2, 30),
      to.be(2019-10-27T02:30:00+02:00@Europe/Berlin)
    ) &&
    expect(
      t.add_duration(at(2019-10-27T00:00:00+02:00@Europe/Berlin, 2, 30), 3600),
      to.be(2019-10-27T02:30:00+01:00@Europe/Berlin)
    );

  at_nil_hour:
    expect(at(t.epoch, hour: nil), to.be_nil());

  at_nil_minute:
    expect(at(t.epoch, minute: nil), to.be_nil());

  at_nil_second:
    expect(at(t.epoch, second: nil), to.be_nil());

  at_nil_nano_of_second:
    expect(at(t.epoch, nano_of_second: nil), to.be_nil());


  at_bad_hour:
    expect_error(
      () -> at(t.epoch, hour: 25),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  at_bad_minute:
    expect_error(
      () -> at(t.epoch, minute: 60),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  at_bad_second:
    expect_error(
      () -> at(t.epoch, second: 60),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  at_bad_nano_of_second:
    expect_error(
      () -> at(t.epoch, nano_of_second: -1),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}