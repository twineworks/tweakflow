import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.seconds_between as seconds_between;

library spec {
  spec:
    describe("time.seconds_between", [

      it("of_default", () ->
        expect(seconds_between(), to.be_nil())
      ),

      it("of_start_nil", () ->
        expect(seconds_between(nil, t.epoch), to.be_nil())
      ),

      it("of_end_nil", () ->
        expect(seconds_between(t.epoch, nil), to.be_nil())
      ),

      it("same", () ->
        expect(
          seconds_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
          to.be(0)
        )
      ),

      it("one_sec", () ->
        expect(
          seconds_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
          to.be(1)
        )
      ),

      it("cross_zones", () ->
        expect(
          seconds_between(1970-01-01T04:00:00+04:00, 1970-01-01T00:00:00),
          to.be(0)
        )
      ),

      it("one_min", () ->
        expect(
          seconds_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
          to.be(60)
        )
      ),

      it("one_sec_inverse", () ->
        expect(
          seconds_between(1970-01-01T00:00:01, 1970-01-01T00:00:00),
          to.be(-1)
        )
      ),

      it("one_min_inverse", () ->
        expect(
          seconds_between(1970-01-01T00:01:00, 1970-01-01T00:00:00),
          to.be(-60)
        )
      ),

  ]);
}