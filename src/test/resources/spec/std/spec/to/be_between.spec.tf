import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_between", [
      
      subject(to.be_between(-9999, 9999)),

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
      
      describe("with [-Infinity, Infinity]", [
        
        subject(to.be_between(-Infinity, Infinity)),
      
        it ("matches 0", (m) ->
          expect(m, to_match(0))
        ),
        
        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),
        
        it ("matches Infinity", (m) ->
          expect(m, to_match(Infinity))
        ),        

        it ("matches -Infinity", (m) ->
          expect(m, to_match(-Infinity))
        ),        

        it ("does not match NaN", (m) ->
          expect(m, to_not_match(NaN))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),              
        
      ]),
      
      describe("with (-Infinity, Infinity)", [
        
        subject(to.be_between(-Infinity, Infinity, false, false)),
      
        it ("matches 0", (m) ->
          expect(m, to_match(0))
        ),
        
        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),
        
        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),        

        it ("does not match -Infinity", (m) ->
          expect(m, to_not_match(-Infinity))
        ),        

        it ("does not match NaN", (m) ->
          expect(m, to_not_match(NaN))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),        
        
      ]),      
      
      describe("with [0, 10]", [
      
        subject(to.be_between(0, 10)),

        it ("matches 0", (m) ->
          expect(m, to_match(0))
        ),

        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),

        it ("matches 10", (m) ->
          expect(m, to_match(10))
        ),

        it ("does not match match -20", (m) ->
          expect(m, to_not_match(-20))
        ),

        it ("does not match match 20", (m) ->
          expect(m, to_not_match(20))
        ),        
        
        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),

        it ("does not match -Infinity", (m) ->
          expect(m, to_not_match(-Infinity))
        ),      
        
      ]), 

      
      describe("with (0, 10]", [
      
        subject(to.be_between(0, 10, low_inclusive: false)),

        it ("does not match 0", (m) ->
          expect(m, to_not_match(0))
        ),

        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),

        it ("matches 10", (m) ->
          expect(m, to_match(10))
        ),

        it ("does not match match -20", (m) ->
          expect(m, to_not_match(-20))
        ),

        it ("does not match match 20", (m) ->
          expect(m, to_not_match(20))
        ),        
        
        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),

        it ("does not match -Infinity", (m) ->
          expect(m, to_not_match(-Infinity))
        ),      
        
      ]),  
      
      describe("with [0, 10)", [
      
        subject(to.be_between(0, 10, high_inclusive: false)),

        it ("matches 0", (m) ->
          expect(m, to_match(0))
        ),

        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),

        it ("does not match 10", (m) ->
          expect(m, to_not_match(10))
        ),

        it ("does not match match -20", (m) ->
          expect(m, to_not_match(-20))
        ),

        it ("does not match match 20", (m) ->
          expect(m, to_not_match(20))
        ),        
        
        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),

        it ("does not match -Infinity", (m) ->
          expect(m, to_not_match(-Infinity))
        ),      
        
      ]),        
      
      describe("with (0, 10)", [
      
        subject(to.be_between(0, 10, low_inclusive: false, high_inclusive: false)),

        it ("does not match 0", (m) ->
          expect(m, to_not_match(0))
        ),

        it ("matches 5", (m) ->
          expect(m, to_match(5))
        ),

        it ("does not match 10", (m) ->
          expect(m, to_not_match(10))
        ),

        it ("does not match match -20", (m) ->
          expect(m, to_not_match(-20))
        ),

        it ("does not match match 20", (m) ->
          expect(m, to_not_match(20))
        ),        
        
        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),

        it ("does not match -Infinity", (m) ->
          expect(m, to_not_match(-Infinity))
        ),      
        
      ]),          

    ]);

}