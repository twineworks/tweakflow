import core, strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

   describe("to.be_subset_of", [

     subject(to.be_subset_of({})),
     
     it ("does not match non-dicts", (m) ->
       let {
         xs: [() -> true, 0, 0.0, time.epoch, 0b, "", [], true, false];
       }
       assert(
         data.all?(xs, (x) ->
            expect(m, to_not_match(x))
         )
       )
     ),     
     
     describe("of {}", [
     
       subject(to.be_subset_of({})),

       it ("matches {}", (m) ->
         expect(m, to_match({}))
       ),
              
       it ("does not match {:a 1}", (m) ->
         expect(m, to_not_match({:a 1}))
       ),       
       
       it ("does not match boolean false", (m) ->
         expect(m, to_not_match(false))
       ),

       it ("does not match nil", (m) ->
         expect(m, to_not_match(nil))
       ),
       
     ]),
     
     describe("of {:a 1, :b NaN}", [
     
       subject(to.be_subset_of({:a 1, :b NaN})),

       it ("does not match {:a 1, :b NaN} (NaN is non-comparable)", (m) ->
         expect(m, to_not_match({:a 1, :b NaN}))
       ),
       
       it ("matches {:a 1}", (m) ->
         expect(m, to_match({:a 1}))
       ),       
       
     ]),     

     describe("of {:a 1, :b core.id}", [
     
       subject(to.be_subset_of({:a 1, :b core.id})),

       it ("does not match {:a 1, :b core.id} (functions are non-comparable)", (m) ->
         expect(m, to_not_match({:a 1, :b core.id}))
       ),
       
       it ("matches {:a 1}", (m) ->
         expect(m, to_match({:a 1}))
       ),       
       
     ]),     

     
     describe("of {:a 1, :b 2, :c 3}", [
     
       subject(to.be_subset_of({:a 1, :b 2, :c 3})),

       it ("matches{:a 1, :b 2, :c 3}", (m) ->
         expect(m, to_match({:a 1, :b 2, :c 3}))
       ),
       
       it ("does not match {:a 1.0, :b 2.0, :c 3.0}", (m) ->
         expect(m, to_not_match({:a 1.0, :b 2.0, :c 3.0}))
       ),         
       
       it ("does not match {:a 1, :b 2, :c 3, :d 4}", (m) ->
         expect(m, to_not_match({:a 1, :b 2, :c 3, :d 4}))
       ),       
       
       it ("matches {}", (m) ->
         expect(m, to_match({}))
       ),         

       it ("matches {:a 1}", (m) ->
         expect(m, to_match({:a 1}))
       ),         

       it ("matches {:a 1, :b 2}", (m) ->
         expect(m, to_match({:a 1, :b 2}))
       ),    
       
     ]),     

   ]);

}