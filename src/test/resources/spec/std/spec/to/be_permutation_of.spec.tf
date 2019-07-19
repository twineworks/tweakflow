import core, strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

   describe("to.be_permutation_of", [

     subject(to.be_permutation_of([])),
     
     it ("does not match non-lists", (m) ->
       let {
         xs: [() -> true, 0, 0.0, time.epoch, 0b, "", {}, true, false];
       }
       assert(
         data.all?(xs, (x) ->
            expect(m, to_not_match(x))
         )
       )
     ),     
     
     describe("of []", [
     
       subject(to.be_permutation_of([])),

       it ("matches []", (m) ->
         expect(m, to_match([]))
       ),
       
       it ("does not match {}", (m) ->
         expect(m, to_not_match({}))
       ),          
       
       it ("does not match [nil]", (m) ->
         expect(m, to_not_match([nil]))
       ),       

       it ("does not match boolean false", (m) ->
         expect(m, to_not_match(false))
       ),

       it ("does not match nil", (m) ->
         expect(m, to_not_match(nil))
       ),
       
     ]),
     
     describe("of [1, NaN, 3]", [
     
       subject(to.be_permutation_of([1, NaN, 3])),

       it ("does not match [1, NaN, 3] (NaN is non-comparable)", (m) ->
         expect(m, to_not_match([1, NaN, 3]))
       ),
       
       it ("does not match [1, 3]", (m) ->
         expect(m, to_not_match([1, 3]))
       ),       
       
     ]),     

     describe("of [1, core.id, 3]", [
     
       subject(to.be_permutation_of([1, core.id, 3])),

       it ("does not match [1, core.id, 3] (functions are non-comparable)", (m) ->
         expect(m, to_not_match([1, core.id, 3]))
       ),
       
       it ("does not match [1, 3]", (m) ->
         expect(m, to_not_match([1, 3]))
       ),       
       
     ]),     

     
     describe("of [1, 2, 3]", [
     
       subject(to.be_permutation_of([1, 2, 3])),

       it ("matches [1, 2, 3]", (m) ->
         expect(m, to_match([1, 2, 3]))
       ),
       
       it ("does not match [1.0, 2.0, 3.0]", (m) ->
         expect(m, to_not_match([1.0, 2.0, 3.0]))
       ),         
       
       it ("matches [1, 3, 2]", (m) ->
         expect(m, to_match([1, 3, 2]))
       ),       

       it ("matches [2, 1, 3]", (m) ->
         expect(m, to_match([2, 1, 3]))
       ),
       
       it ("matches [2, 3, 1]", (m) ->
         expect(m, to_match([2, 3, 1]))
       ),       

       it ("matches [3, 1, 2]", (m) ->
         expect(m, to_match([3, 1, 2]))
       ),         
       
       it ("matches [3, 2, 1]", (m) ->
         expect(m, to_match([3, 2, 1]))
       ),          
       
       it ("does not match []", (m) ->
         expect(m, to_not_match([]))
       ),         

       it ("does not match [1]", (m) ->
         expect(m, to_not_match([1]))
       ),         

       it ("does not match [1, 2]", (m) ->
         expect(m, to_not_match([1, 2]))
       ),    
       
       it ("does not match [1, 2, 3, 4]", (m) ->
         expect(m, to_not_match([1, 2, 3, 4]))
       ),          

     ]),     

   ]);

}