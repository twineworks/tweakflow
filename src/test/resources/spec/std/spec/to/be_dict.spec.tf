import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_dict", [

      subject(to.be_dict()),

      it ("matches empty dict", (m) ->
        expect(m, to_match({}))
      ),

      it ("matches simple dict", (m) ->
        expect(m, to_match({:a 1, :b 2}))
      ),

      it ("matches dict with non-comparables", (m) ->
        expect(m, to_match({:a NaN, :b NaN}))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-dicts", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, 0b01, [], true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}