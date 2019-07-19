import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_list", [

      subject(to.be_list()),

      it ("matches empty list", (m) ->
        expect(m, to_match([]))
      ),

      it ("matches simple list", (m) ->
        expect(m, to_match([1,2,3]))
      ),

      it ("matches list with non-comparables", (m) ->
        expect(m, to_match([NaN, NaN]))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-lists", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, 0b01, {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}