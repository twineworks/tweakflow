import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_NaN", [

      subject(to.be_NaN()),

      it ("matches NaN", (m) ->
        expect(m, to_match(NaN))
      ),
      
      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),      
      
      it ("does not match false", (m) ->
        expect(m, to_not_match(false))
      ),        

      it ("does not match non-NaN", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 0.0, 0, {}, [], true, 0b00];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}