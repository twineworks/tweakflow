import time as t, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.unix_timestamp as unix_timestamp;

library unix_timestamp_spec {

  of_default:
    expect(unix_timestamp(), to.be_nil());

  of_nil:
    expect(unix_timestamp(nil), to.be_nil());

  of_epoch:
    expect(unix_timestamp(t.epoch), to.be(0));

  of_1496933995:
    expect(unix_timestamp(2017-06-08T14:59:55Z@UTC), to.be(1496933995));

  of_neg_1496933995:
    expect(unix_timestamp(1922-07-26T09:00:05Z@UTC), to.be(-1496933995));

}