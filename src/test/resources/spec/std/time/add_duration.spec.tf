import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.add_duration as add_duration;

library spec {
  spec:
    describe("time.add_duration", [

      it("of_default", () ->
        expect(add_duration(), to.be_nil())
      ),

      it("of_start_nil", () ->
        expect(add_duration(nil, 0, 0), to.be_nil())
      ),

      it("of_seconds_nil", () ->
        expect(add_duration(t.epoch, nil, 0), to.be_nil())
      ),

      it("of_nano_of_second_nil", () ->
        expect(add_duration(t.epoch, 0, nil), to.be_nil())
      ),

      it("nop", () ->
        expect(
          add_duration(t.epoch, 0, 0),
          to.be(t.epoch)
        )
      ),

      it("add_one_nano", () ->
        expect(
          add_duration(t.epoch, 0, 1),
          to.be(1970-01-01T00:00:00.000000001)
        )
      ),

      it("add_half_sec", () ->
        expect(
          add_duration(t.epoch, 0, 500000000),
          to.be(1970-01-01T00:00:00.5)
        )
      ),

      it("sub_one_nano", () ->
        expect(
          add_duration(t.epoch, 0, -1),
          to.be(1969-12-31T23:59:59.999999999)
        )
      ),

      it("sub_half_sec", () ->
        expect(
          add_duration(t.epoch, 0, -500000000),
          to.be(1969-12-31T23:59:59.5)
        )
      ),

      it("add_one_hour", () ->
        expect(
          add_duration(t.epoch, 60*60, 0),
          to.be(1970-01-01T01:00:00)
        )
      ),

      it("add_one_hour_one_minute_one_sec_one_nano", () ->
        expect(
          add_duration(t.epoch, 60*60+60+1, 1),
          to.be(1970-01-01T01:01:01.000000001)
        )
      ),

      it("add_one_hour_one_minute_one_sec_sub_one_nano", () ->
        expect(
          add_duration(t.epoch, 60*60+60+1, -1),
          to.be(1970-01-01T01:01:00.999999999)
        )
      ),

      it("sub_one_hour_one_minute_one_sec_one_nano", () ->
        expect(
          add_duration(t.epoch, -(60*60+60+1), -1),
          to.be(1969-12-31T22:58:58.999999999)
        )
      ),

  ]);
}