import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.month as month;

library spec {
  spec:
    describe("time.month", [

      it("of_default", () ->
        expect(month(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(month(nil), to.be_nil())
      ),

      it("of_epoch", () ->
        expect(month(t.epoch), to.be(1))
      ),

      it("of_1", () ->
        expect(month(0001-01-01T), to.be(1))
      ),

      it("of_12", () ->
        expect(month(2019-12-01T), to.be(12))
      ),

  ]);
}