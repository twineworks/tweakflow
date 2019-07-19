import core, strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

   describe("to.be_one_of", [
     
     describe("of [1, nil, NaN, core.id, []]", [
       
       subject(to.be_one_of([1, nil, NaN, core.id, []])),

         it ("matches 1", (m) ->
           expect(m, to_match(1))
         ),
       
         it ("matches nil", (m) ->
           expect(m, to_match(nil))
         ),       
       
         it ("matches []", (m) ->
           expect(m, to_match([]))
         ),           

         it ("does not match 2", (m) ->
           expect(m, to_not_match(2))
         ),        
       
         it ("does not match NaN (NaN is non-comparable)", (m) ->
           expect(m, to_not_match(NaN))
         ),    
       
         it ("does not match core.id (functions are non-comparable)", (m) ->
           expect(m, to_not_match(core.id))
         ),         
       
     ])

   ]);

}