import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_month as with_month;

library spec {
  spec:
    describe("time.with_month", [

      it("of_default", () ->
        expect(with_month(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(with_month(nil, 12), to.be_nil())
      ),

      it("of_month_nil", () ->
        expect(with_month(t.epoch, nil), to.be_nil())
      ),

      it("with_12", () ->
        expect(with_month(t.epoch, 12), to.be(1970-12-01T))
      ),

      it("with_03", () ->
        expect(with_month(t.epoch, 3), to.be(1970-03-01T))
      ),

      it("with_change_from_longer_month", () ->
        expect(with_month(2016-03-31T, 2), to.be(2016-02-29T))
      ),

      it("out_of_range", () ->
        expect_error(
          () -> with_month(t.epoch, 13),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}