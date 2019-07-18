import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.nano_of_second as nano_of_second;

library spec {
  spec:
    describe("time.nano_o_second", [

      it("of_default", () -> 
        expect(nano_of_second(), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(nano_of_second(nil), to.be_nil())
      ),
    
      it("of_epoch", () -> 
        expect(nano_of_second(t.epoch), to.be(0))
      ),
    
      it("of_0", () -> 
        expect(nano_of_second(2019-03-12T12:33:00.0), to.be(0))
      ),
    
      it("of_1", () -> 
        expect(nano_of_second(2019-04-23T12:23:01.000000001), to.be(1))
      ),
    
      it("of_half_second", () -> 
        expect(nano_of_second(2019-12-31T13:33:23.5), to.be(500000000))
      ),
    
      it("of_tenth_second", () -> 
        expect(nano_of_second(2019-12-31T12:23:59.1), to.be(100000000))
      ),

  ]);
}