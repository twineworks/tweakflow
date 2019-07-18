import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_year as with_year;

library spec {
  spec:
    describe("time.with_year", [

      it("of_default", () ->
        expect(with_year(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(with_year(nil, 2020), to.be_nil())
      ),

      it("of_year_nil", () ->
        expect(with_year(t.epoch, nil), to.be_nil())
      ),

      it("with_2010", () ->
        expect(with_year(t.epoch, 2010), to.be(2010-01-01T))
      ),

      it("with_0001", () ->
        expect(with_year(t.epoch, 0001), to.be(0001-01-01T))
      ),

      it("with_change_from_leap_day", () ->
        expect(with_year(2016-02-29T, 2019), to.be(2019-02-28T))
      ),

      it("out_of_range", () ->
        expect_error(
          () -> with_year(t.epoch, 1000000000),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}