import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.second as second;

library spec {
  spec:
    describe("time.second", [
    
      it("of_default", () -> 
        expect(second(), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(second(nil), to.be_nil())
      ),
    
      it("of_epoch", () -> 
        expect(second(t.epoch), to.be(0))
      ),
    
      it("of_0", () -> 
        expect(second(2019-03-12T12:33:00), to.be(0))
      ),
    
      it("of_1", () -> 
        expect(second(2019-04-23T12:23:01), to.be(1))
      ),
    
      it("of_23", () -> 
        expect(second(2019-12-31T13:33:23), to.be(23))
      ),
    
      it("of_59", () -> 
        expect(second(2019-12-31T12:23:59), to.be(59))
      ),

  ]);
}