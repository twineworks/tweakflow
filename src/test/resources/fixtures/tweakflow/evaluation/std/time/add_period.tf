import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.add_period as add_period;

library add_period_spec {

  of_default:
    expect(add_period(), to.be_nil());

  of_start_nil:
    expect(add_period(nil, 0, 0, 0), to.be_nil());

  of_years_nil:
    expect(add_period(t.epoch, nil, 0, 0), to.be_nil());

  of_months_nil:
    expect(add_period(t.epoch, 0, nil, 0), to.be_nil());

  of_days_nil:
    expect(add_period(t.epoch, 0, 0, nil), to.be_nil());

  nop:
    expect(
      add_period(t.epoch, 0, 0, 0),
      to.be(t.epoch)
    );

  one_day:
    expect(
      add_period(1970-01-01T, days: 1),
      to.be(1970-01-02T)
    );

  one_day_inverse:
    expect(
      add_period(1970-01-01T, days: -1),
      to.be(1969-12-31T)
    );

  thirty_days:
    expect(
      add_period(1970-01-01T, days: 30),
      to.be(1970-01-31T)
    );

  thirty_one_days:
    expect(
      add_period(1970-01-01T, days: 31),
      to.be(1970-02-01T)
    );

  thirty_one_days_inverse:
    expect(
      add_period(1970-01-01T, days: -31),
      to.be(1969-12-01T)
    );

  one_month:
    expect(
      add_period(1970-01-01T, months: 1),
      to.be(1970-02-01T)
    );

  one_mid_month:
    expect(
      add_period(1970-01-12T, months: 1),
      to.be(1970-02-12T)
    );

  one_end_of_month_hit_month:
    expect(
      add_period(1970-01-31T, months: 1),
      to.be(1970-02-28T)
    );

  one_end_of_month_hit_month_leap_year:
    expect(
      add_period(2016-01-31T, months: 1),
      to.be(2016-02-29T)
    );

  one_end_of_month_hit_month_back:
    expect(
      add_period(1970-03-31T, months: -1),
      to.be(1970-02-28T)
    );

  one_end_of_month_hit_inverse:
    expect(
      add_period(1970-02-28T, months: -1),
      to.be(1970-01-28T)
    );

  twelve_months:
    expect(
      add_period(1970-01-01T, months: 12),
      to.be(1971-01-01T)
    );

  one_year:
    expect(
      add_period(1970-01-01T, years: 1),
      to.be(1971-01-01T)
    );

  one_mid_year:
    expect(
      add_period(1970-07-01T, years: 1),
      to.be(1971-07-01T)
    );

  one_leap_year:
    expect(
      add_period(2016-02-29T, years: 1),
      to.be(2017-02-28T)
    );

}