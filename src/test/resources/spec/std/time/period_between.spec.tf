import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.period_between as period_between;

library period {
  zero: {:years 0, :months 0, :days 0};
}

library spec {
  spec:
    describe("time.period_between", [

      it("of_default", () ->
        expect(period_between(), to.be_nil())
      ),

      it("of_start_nil", () ->
        expect(period_between(nil, t.epoch), to.be_nil())
      ),

      it("of_end_nil", () ->
        expect(period_between(t.epoch, nil), to.be_nil())
      ),

      it("same", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
          to.be(period.zero)
        )
      ),

      it("one_min", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
          to.be(period.zero)
        )
      ),

      it("one_hour", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
          to.be(period.zero)
        )
      ),

      it("one_hour_inverse", () ->
        expect(
          period_between(1970-01-01T01:00:00, 1970-01-01T00:00:00),
          to.be(period.zero)
        )
      ),

      it("one_day", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
          to.be({:years 0, :months 0, :days 1})
        )
      ),

      it("one_month", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1970-02-01T00:00:00),
          to.be({:years 0, :months 1, :days 0})
        )
      ),

      it("one_year", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
          to.be({:years 1, :months 0, :days 0})
        )
      ),

      it("five_years_ten_months_eleven_days", () ->
        expect(
          period_between(1970-01-01T00:00:00, 1975-11-12T00:00:00),
          to.be({:years 5, :months 10, :days 11})
        )
      ),

      it("five_years_ten_months_eleven_days_inverse", () ->
        expect(
          period_between(1975-11-12T00:00:00, 1970-01-01T00:00:00),
          to.be({:years -5, :months -10, :days -11})
        )
      ),

  ]);
}