import core, strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

   describe("to.contain_all", [
     
     describe("of [1, nil]", [
       
       subject(to.contain_all([1, nil])),

         it ("matches [1, nil]", (m) ->
           expect(m, to_match([1, nil]))
         ),
         
         it ("matches [1, nil, NaN]", (m) ->
           expect(m, to_match([1, nil, NaN]))
         ),
       
         it ("does not match []", (m) ->
           expect(m, to_not_match([]))
         ),       
       
         it ("does not match [1]", (m) ->
           expect(m, to_not_match([1]))
         ),            
       
         it ("matches {:a 1, :b nil}", (m) ->
           expect(m, to_match({:a 1, :b nil}))
         ),
         
         it ("matches {:a 1, :b nil, :c NaN}", (m) ->
           expect(m, to_match({:a 1, :b nil, :c NaN}))
         ),
       
         it ("does not match {}", (m) ->
           expect(m, to_not_match({}))
         ),            
       
     ]),
     
     describe("of [NaN] (non-comparable)", [
       
       subject(to.contain_all([NaN])),

         it ("does not match [NaN]", (m) ->
           expect(m, to_not_match([NaN]))
         ),       

         it ("does not match []", (m) ->
           expect(m, to_not_match([]))
         ),       
              
         it ("does not match {}", (m) ->
           expect(m, to_not_match({}))
         ),    
       
         it ("does not match {:a NaN}", (m) ->
           expect(m, to_not_match({:a NaN}))
         ),        
       
     ]),
     
     describe("of [core.id] (functions are non-comparable)", [
       
       subject(to.contain_all([core.id])),

         it ("does not match [core.id]", (m) ->
           expect(m, to_not_match([core.id]))
         ),       

         it ("does not match []", (m) ->
           expect(m, to_not_match([]))
         ),       
              
         it ("does not match {}", (m) ->
           expect(m, to_not_match({}))
         ),    
       
         it ("does not match {:a core.id}", (m) ->
           expect(m, to_not_match({:a core.id}))
         ),        
       
     ])       

   ]);

}