import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.days_between as days_between;

library spec {
  spec:
    describe("time.days_between", [
    
      it("of_default", () -> 
        expect(days_between(), to.be_nil())
      ),
    
      it("of_start_nil", () -> 
        expect(days_between(nil, t.epoch), to.be_nil())
      ),
    
      it("of_end_nil", () -> 
        expect(days_between(t.epoch, nil), to.be_nil())
      ),
    
      it("same", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
          to.be(0)
        )
      ),
    
      it("one_sec", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
          to.be(0)
        )
      ),
    
      it("one_min", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
          to.be(0)
        )
      ),
    
      it("one_hour", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
          to.be(0)
        )
      ),
    
      it("one_day", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
          to.be(1)
        )
      ),
    
      it("one_year", () -> 
        expect(
          days_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
          to.be(365)
        )
      ),
    
      it("one_year_inverse", () -> 
        expect(
          days_between(1971-01-01T00:00:00, 1970-01-01T00:00:00),
          to.be(-365)
        )
      ),

  ]);
}