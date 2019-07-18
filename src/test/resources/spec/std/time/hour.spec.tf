import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.hour as hour;

library spec {
  spec:
    describe("time.hour", [

      it("of_default", () ->
        expect(hour(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(hour(nil), to.be_nil())
      ),

      it("of_epoch", () ->
        expect(hour(t.epoch), to.be(0))
      ),

      it("of_0", () ->
        expect(hour(2019-03-12T00:12:03), to.be(0))
      ),

      it("of_1", () ->
        expect(hour(2019-04-23T01:12:03), to.be(1))
      ),

      it("of_23", () ->
        expect(hour(2019-12-31T23:12:03), to.be(23))
      ),

  ]);
}