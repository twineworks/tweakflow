import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.add_period as add_period;

library spec {
  spec:
    describe("time.add_period", [

      it("of_default", () ->
        expect(add_period(), to.be_nil())
      ),

      it("of_start_nil", () ->
        expect(add_period(nil, 0, 0, 0), to.be_nil())
      ),

      it("of_years_nil", () ->
        expect(add_period(t.epoch, nil, 0, 0), to.be_nil())
      ),

      it("of_months_nil", () ->
        expect(add_period(t.epoch, 0, nil, 0), to.be_nil())
      ),

      it("of_days_nil", () ->
        expect(add_period(t.epoch, 0, 0, nil), to.be_nil())
      ),

      it("nop", () ->
        expect(
          add_period(t.epoch, 0, 0, 0),
          to.be(t.epoch)
        )
      ),

      it("one_day", () ->
        expect(
          add_period(1970-01-01T, days: 1),
          to.be(1970-01-02T)
        )
      ),

      it("one_day_inverse", () ->
        expect(
          add_period(1970-01-01T, days: -1),
          to.be(1969-12-31T)
        )
      ),

      it("thirty_days", () ->
        expect(
          add_period(1970-01-01T, days: 30),
          to.be(1970-01-31T)
        )
      ),

      it("thirty_one_days", () ->
        expect(
          add_period(1970-01-01T, days: 31),
          to.be(1970-02-01T)
        )
      ),

      it("thirty_one_days_inverse", () ->
        expect(
          add_period(1970-01-01T, days: -31),
          to.be(1969-12-01T)
        )
      ),

      it("one_month", () ->
        expect(
          add_period(1970-01-01T, months: 1),
          to.be(1970-02-01T)
        )
      ),

      it("one_mid_month", () ->
        expect(
          add_period(1970-01-12T, months: 1),
          to.be(1970-02-12T)
        )
      ),

      it("one_end_of_month_hit_month", () ->
        expect(
          add_period(1970-01-31T, months: 1),
          to.be(1970-02-28T)
        )
      ),

      it("one_end_of_month_hit_month_leap_year", () ->
        expect(
          add_period(2016-01-31T, months: 1),
          to.be(2016-02-29T)
        )
      ),

      it("one_end_of_month_hit_month_back", () ->
        expect(
          add_period(1970-03-31T, months: -1),
          to.be(1970-02-28T)
        )
      ),

      it("one_end_of_month_hit_inverse", () ->
        expect(
          add_period(1970-02-28T, months: -1),
          to.be(1970-01-28T)
        )
      ),

      it("twelve_months", () ->
        expect(
          add_period(1970-01-01T, months: 12),
          to.be(1971-01-01T)
        )
      ),

      it("one_year", () ->
        expect(
          add_period(1970-01-01T, years: 1),
          to.be(1971-01-01T)
        )
      ),

      it("one_mid_year", () ->
        expect(
          add_period(1970-07-01T, years: 1),
          to.be(1971-07-01T)
        )
      ),

      it("one_leap_year", () ->
        expect(
          add_period(2016-02-29T, years: 1),
          to.be(2017-02-28T)
        )
      ),

  ]);
}