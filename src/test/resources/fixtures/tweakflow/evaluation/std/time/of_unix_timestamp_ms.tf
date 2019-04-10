import time as t, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.of_unix_timestamp_ms as of_unix_timestamp_ms;

library of_unix_timestamp_ms_spec {

  of_default:
    expect(of_unix_timestamp_ms(), to.be_nil());

  of_nil:
    expect(of_unix_timestamp_ms(nil), to.be_nil());

  of_epoch:
    expect(of_unix_timestamp_ms(0), to.be(t.epoch));

  of_1496933995736:
    expect(of_unix_timestamp_ms(1496933995736), to.be(2017-06-08T14:59:55.736Z@UTC));

  of_neg_1496933995123:
    expect(of_unix_timestamp_ms(-1496933995123), to.be(1922-07-26T09:00:04.877Z@UTC));

  of_max_long:
    expect(of_unix_timestamp_ms(math.max_long), to.be(292278994-08-17T07:12:55.807Z@UTC));

  of_min_long:
    expect(of_unix_timestamp_ms(math.min_long), to.be(-292275055-05-16T16:47:04.192Z@UTC));

}