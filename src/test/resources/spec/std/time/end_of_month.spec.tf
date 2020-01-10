import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.end_of_month as end_of_month;

library spec {
  spec:
    describe("time.end_of_month", [

      it("of_default", () ->
        expect(end_of_month(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(end_of_month(nil), to.be_nil())
      ),

      it("of_epoch", () ->
        expect(end_of_month(t.epoch), to.be(1970-01-31T))
      ),

      it("of_epoch_with_time", () ->
        expect(end_of_month(1970-01-01T16:45:12), to.be(1970-01-31T16:45:12))
      ),

      it("of_1", () ->
        expect(end_of_month(0001-01-01T), to.be(0001-01-31T))
      ),

      it("of_31", () ->
        expect(end_of_month(2019-12-31T), to.be(2019-12-31T))
      ),

      it("of_step_year_february", () ->
        expect(end_of_month(2004-02-12T), to.be(2004-02-29T))
      ),

      describe("with offset nil", [
        it("of_epoch", () ->
          expect(end_of_month(t.epoch, nil), to.be_nil())
        ),
      ]),

      describe("with offset -1", [

        it("of_x_nil", () ->
          expect(end_of_month(nil, -1), to.be_nil())
        ),

        it("of_epoch", () ->
          expect(end_of_month(t.epoch, -1), to.be(1969-12-31T))
        ),

        it("of_epoch_with_time", () ->
          expect(end_of_month(1970-01-01T16:45:12, -1), to.be(1969-12-31T16:45:12))
        ),

        it("of_1", () ->
          expect(end_of_month(0001-01-01T, -1), to.be(0000-12-31T))
        ),

        it("of_31", () ->
          expect(end_of_month(2019-12-31T, -1), to.be(2019-11-30T))
        ),

        it("of_from_step_year_february", () ->
          expect(end_of_month(2004-02-29T, -1), to.be(2004-01-31T))
        ),

        it("of_into_step_year_february", () ->
          expect(end_of_month(2004-03-12T, -1), to.be(2004-02-29T))
        ),

      ]),

      describe("with offset 12", [

        it("of_x_nil", () ->
          expect(end_of_month(nil, -1), to.be_nil())
        ),

        it("of_epoch", () ->
          expect(end_of_month(t.epoch, 12), to.be(1971-01-31T))
        ),

        it("of_epoch_with_time", () ->
          expect(end_of_month(1970-01-01T16:45:12, 12), to.be(1971-01-31T16:45:12))
        ),

        it("of_1", () ->
          expect(end_of_month(0001-01-01T, 12), to.be(0002-01-31T))
        ),

        it("of_31", () ->
          expect(end_of_month(2019-12-31T, 12), to.be(2020-12-31T))
        ),

        it("of_from_step_year_february", () ->
          expect(end_of_month(2004-02-29T, 12), to.be(2005-02-28T))
        ),

        it("of_into_step_year_february", () ->
          expect(end_of_month(2003-02-12T, 12), to.be(2004-02-29T))
        ),

      ])

    ]);
}