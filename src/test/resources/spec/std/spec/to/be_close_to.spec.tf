import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_close_to", [
      
      subject(to.be_close_to(0)),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),
      
      it ("does not match NaN", (m) ->
        expect(m, to_not_match(NaN))
      ),      

      it ("does not match non-numbers", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, {}, [], true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),
      
      describe("with precision 0.1", [
        
        subject(to.be_close_to(precision=0.1)),
        
        describe("and target 5.0", [
          
          subject(transform: (m) -> m(5.0)),
          
          it("does not match NaN", (m) -> 
            expect(m, to_not_match(NaN))
          ),            
          
          it("does not match nil", (m) -> 
            expect(m, to_not_match(nil))
          ),       
          
          it("does not match -Infinity", (m) -> 
            expect(m, to_not_match(-Infinity))
          ),            
          
          it("does not match 4.80", (m) -> 
            expect(m, to_not_match(4.80))
          ),          
          
          it("matches 4.90", (m) -> 
            expect(m, to_match(4.90))
          ),          

          it("matches 4.95", (m) -> 
            expect(m, to_match(4.95))
          ),
                    
          it("matches 5.00", (m) -> 
            expect(m, to_match(5))
          ),          
          
          it("matches 5.05", (m) -> 
            expect(m, to_match(5.05))
          ),
          
          it("matches 5.10", (m) -> 
            expect(m, to_match(5.1))
          ),          
          
          it("does not match 5.20", (m) -> 
            expect(m, to_not_match(5.20))
          ),       
          
          it("does not match Infinity", (m) -> 
            expect(m, to_not_match(Infinity))
          ),               

        ])
        
        
      ]),
      
      describe("with precision Infinity", [
        
        subject(to.be_close_to(precision=Infinity)),
        
        describe("and target 5.0", [
          
          subject(transform: (m) -> m(5.0)),
          
          it("matches 0", (m) -> 
            expect(m, to_match(0))
          ),          

          it("matches -Infinity", (m) -> 
            expect(m, to_match(-Infinity))
          ),
                    
          it("matches Infinity", (m) -> 
            expect(m, to_match(Infinity))
          ),     
          
          it("does not match NaN", (m) -> 
            expect(m, to_not_match(NaN))
          ),            
          
        ])
        
        
      ]),
      
     describe("with precision Infinity", [
        
        subject(to.be_close_to(precision=Infinity)),
        
        describe("and target Infinity", [
          
          subject(transform: (m) -> m(Infinity)),
          
          it("matches 0", (m) -> 
            expect(m, to_match(0))
          ),          

          it("matches -Infinity", (m) -> 
            expect(m, to_match(-Infinity))
          ),
                    
          it("matches Infinity", (m) -> 
            expect(m, to_match(Infinity))
          ),          
          
          it("does not match NaN", (m) -> 
            expect(m, to_not_match(NaN))
          ),    
          
        ]),
       
        describe("and target NaN", [
          
          subject(transform: (m) -> m(NaN)),
          
          it("does not match 0", (m) -> 
            expect(m, to_not_match(0))
          ),          

          it("does not match -Infinity", (m) -> 
            expect(m, to_not_match(-Infinity))
          ),
                    
          it("does not match Infinity", (m) -> 
            expect(m, to_not_match(Infinity))
          ),          
          
          it("does not match NaN", (m) -> 
            expect(m, to_not_match(NaN))
          ),              
          
        ])         
        
        
      ]),
   
      
    ]);

}