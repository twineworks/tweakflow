import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.minute as minute;

library spec {
  spec:
    describe("time.minute", [

      it("of_default", () -> 
        expect(minute(), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(minute(nil), to.be_nil())
      ),
    
      it("of_epoch", () -> 
        expect(minute(t.epoch), to.be(0))
      ),
    
      it("of_0", () -> 
        expect(minute(2019-03-12T12:00:03), to.be(0))
      ),
    
      it("of_1", () -> 
        expect(minute(2019-04-23T12:01:03), to.be(1))
      ),
    
      it("of_23", () -> 
        expect(minute(2019-12-31T13:23:23), to.be(23))
      ),
    
      it("of_59", () -> 
        expect(minute(2019-12-31T12:59:33), to.be(59))
      ),

  ]);
}