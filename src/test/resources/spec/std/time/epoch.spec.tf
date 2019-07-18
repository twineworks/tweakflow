import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.epoch as epoch;

library spec {
  spec:
    describe("time.epoch", [

    it("is_unix_epoch", () ->
      let {
        e: epoch as dict;
      }
      expect(e[:year], to.be(1970)) &&
      expect(e[:month], to.be(1)) &&
      expect(e[:day_of_month], to.be(1)) &&
      expect(e[:hour], to.be(0)) &&
      expect(e[:minute], to.be(0)) &&
      expect(e[:second], to.be(0)) &&
      expect(e[:nano_of_second], to.be(0)) &&
      expect(e[:day_of_year], to.be(1)) &&
      expect(e[:zone], to.be("UTC"))
    ),

  ]);
}