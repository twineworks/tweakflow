import time as t, math from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.unix_timestamp_ms as unix_timestamp_ms;

library spec {
  spec:
    describe("unix_timestamp_ms", [


  of_default:
    expect(unix_timestamp_ms(), to.be_nil());

  of_nil:
    expect(unix_timestamp_ms(nil), to.be_nil());

  of_epoch:
    expect(unix_timestamp_ms(t.epoch), to.be(0));

  of_1496933995123:
    expect(unix_timestamp_ms(2017-06-08T14:59:55.123Z@UTC), to.be(1496933995123));

  of_neg_1496933995123:
    expect(unix_timestamp_ms(1922-07-26T09:00:04.877Z@UTC), to.be(-1496933995123));

  ]);
  ]);
}