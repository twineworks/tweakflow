import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.year as year;

library spec {
  spec:
    describe("time.year", [

      it("of_default", () ->
        expect(year(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(year(nil), to.be_nil())
      ),

      it("of_epoch", () ->
        expect(year(t.epoch), to.be(1970))
      ),

      it("of_1", () ->
        expect(year(0001-01-01T), to.be(1))
      ),

      it("of_2019", () ->
        expect(year(2019-01-01T), to.be(2019))
      ),

      it("of_2999", () ->
        expect(year(2999-01-01T), to.be(2999))
      ),
  ]);
}