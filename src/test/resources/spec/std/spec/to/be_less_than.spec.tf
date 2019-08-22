import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_less_than", [

      describe("with number 10", [
      
        subject(to.be_less_than(10)),

        it ("does not match 20", (m) ->
          expect(m, to_not_match(20))
        ),
        
        it ("does not match 20.0", (m) ->
          expect(m, to_not_match(20.0))
        ),

        it ("does not match 20d", (m) ->
          expect(m, to_not_match(20d))
        ),

        it ("does not match Infinity", (m) ->
          expect(m, to_not_match(Infinity))
        ),

        it ("does not match 10", (m) ->
          expect(m, to_not_match(10))
        ),

        it ("does not match 10.0", (m) ->
          expect(m, to_not_match(10.0))
        ),

        it ("does not match 10d", (m) ->
          expect(m, to_not_match(10d))
        ),
        
        it ("matches -20", (m) ->
          expect(m, to_match(-20))
        ),

        it ("matches -20.0", (m) ->
          expect(m, to_match(-20.0))
        ),

        it ("matches -20d", (m) ->
          expect(m, to_match(-20d))
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
        
      ]), 


    ]);

}