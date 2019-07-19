import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {
    
  spec:

   describe("to.match_regex", [
     
     describe('with .*', [
       
       subject(to.match_regex('.*')),       
       
       it("matches ''", (m) -> 
         expect(m, to_match(''))
       ),
       
       it("matches 'foo'", (m) -> 
         expect(m, to_match('foo'))
       ),
       
       it ("does not match non-strings", (m) ->
         let {
           xs: [() -> true, 1, 1.0, time.epoch, 0b01, [], {}, nil];
         }
         assert(
           data.all?(xs, (x) ->
              expect(m, to_not_match(x))
           )
         )
       ),
     
     ]),     
     
     describe('with \d+', [

       subject(to.match_regex('\d+')),

       it ("matches '123'", (m) ->
         expect(m, to_match('123'))
       ),

       it ("does not match '123+'", (m) ->
         expect(m, to_not_match('123+'))
       ),
       
       it ("does not match '-123'", (m) ->
         expect(m, to_not_match('-123'))
       ),       

       it ("does not match nil", (m) ->
         expect(m, to_not_match(nil))
       ),

     ]),
     

     

   ]);

}