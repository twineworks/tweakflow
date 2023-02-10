import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.quarter as quarter;

library spec {
  spec:
    describe("time.quarter", [

      it("of_default", () ->
        expect(quarter(), to.be_nil())
      ),

      it("of_x_nil", () ->
        expect(quarter(nil), to.be_nil())
      ),

      it("of_epoch", () ->
        expect(quarter(t.epoch), to.be(1))
      ),

      it("of_1", () ->
        expect(quarter(0001-01-01T), to.be(1))
      ),

      it("of_2", () ->
        expect(quarter(1981-02-01T), to.be(1))
      ),

      it("of_3", () ->
        expect(quarter(2022-03-01T), to.be(1))
      ),

      it("of_4", () ->
        expect(quarter(2022-04-01T), to.be(2))
      ),

      it("of_5", () ->
        expect(quarter(2022-05-01T), to.be(2))
      ),

      it("of_6", () ->
        expect(quarter(2022-06-01T), to.be(2))
      ),

      it("of_7", () ->
        expect(quarter(2022-07-01T), to.be(3))
      ),

      it("of_8", () ->
        expect(quarter(2022-08-01T), to.be(3))
      ),

      it("of_9", () ->
        expect(quarter(2022-09-01T), to.be(3))
      ),

      it("of_10", () ->
        expect(quarter(2022-10-01T), to.be(4))
      ),

      it("of_11", () ->
        expect(quarter(2022-11-01T), to.be(4))
      ),

      it("of_12", () ->
        expect(quarter(2019-12-01T), to.be(4))
      ),

  ]);
}