import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.duration_between as duration_between;

library duration {
  zero: {:seconds 0, :nano_seconds 0};
  of: (long seconds=0, long nano_seconds=0) ->
    {
      :seconds seconds,
      :nano_seconds nano_seconds
    };
}

library spec {
  spec:
    describe("time.duration_between", [

      it("of_default", () ->
        expect(duration_between(), to.be_nil())
      ),

      it("of_start_nil", () ->
        expect(duration_between(nil, t.epoch), to.be_nil())
      ),

      it("of_end_nil", () ->
        expect(duration_between(t.epoch, nil), to.be_nil())
      ),

      it("same", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
          to.be(duration.zero)
        )
      ),

      it("one_min", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
          to.be(duration.of(seconds: 60))
        )
      ),

      it("one_hour", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
          to.be(duration.of(seconds: 60*60))
        )
      ),

      it("one_hour_inverse", () ->
        expect(
          duration_between(1970-01-01T01:00:00, 1970-01-01T00:00:00),
          to.be(duration.of(seconds: -60*60))
        )
      ),

      it("one_day", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
          to.be(duration.of(seconds: 1*24*60*60))
        )
      ),

      it("one_month", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-02-01T00:00:00),
          to.be(duration.of(seconds: 1*24*60*60*31))
        )
      ),

      it("one_year", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
          to.be(duration.of(seconds: 1*24*60*60*365))
        )
      ),

      it("one_year_one_day_one_hour_one_minute_one_second", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1971-01-02T01:01:01),
          to.be(duration.of(
            seconds:
              1*24*60*60*365 +
              1*24*60*60 +
              1*60*60 +
              1*60 +
              1
          ))
        )
      ),

      it("one_nano", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1970-01-01T00:00:00.000000001),
          to.be(duration.of(nano_seconds: 1))
        )
      ),

      it("one_nano_inverse", () ->
        expect(
          duration_between(1970-01-01T00:00:00.000000001, 1970-01-01T00:00:00),
          to.be(duration.of(seconds: -1, nano_seconds: 999999999))
        )
      ),

      it("one_year_one_day_one_hour_one_minute_one_second_one_nano", () ->
        expect(
          duration_between(1970-01-01T00:00:00, 1971-01-02T01:01:01.000000001),
          to.be(duration.of(
            seconds:
              1*24*60*60*365 +
              1*24*60*60 +
              1*60*60 +
              1*60 +
              1,
            nano_seconds:
              1
          ))
        )
      ),

      it("one_year_one_day_one_hour_one_minute_one_second_one_nano_inverse", () ->
        expect(
          duration_between(1971-01-02T01:01:01.000000001, 1970-01-01T00:00:00),
          to.be(duration.of(
            seconds:
              - (
                1*24*60*60*365 +
                1*24*60*60 +
                1*60*60 +
                1*60 +
                1 +
                1
                ),
            nano_seconds:
              999999999
          ))
        )
      ),

  ]);
}