import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.week_of_year as week_of_year;

library spec {
  spec:
    describe("time.week_of_year", [

      it("of_default", () -> 
        expect(week_of_year(), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(week_of_year(nil), to.be_nil())
      ),
    
      it("of_epoch", () -> 
        expect(week_of_year(t.epoch), to.be(1))
      ),
    
      it("of_52nd_week", () -> 
        expect(week_of_year(2011-01-01T), to.be(52))
      ),
    
      it("of_53rd_week", () -> 
        expect(week_of_year(2010-01-01T), to.be(53))
      ),
    
      it("of_1st_week", () -> 
        expect(week_of_year(2013-01-01T), to.be(1))
      ),
    
      it("of_2nd_week", () -> 
        expect(week_of_year(2013-01-08T), to.be(2))
      ),
    
      it("of_8th_week", () -> 
        expect(week_of_year(2017-02-21T), to.be(8))
      ),

  ]);
}