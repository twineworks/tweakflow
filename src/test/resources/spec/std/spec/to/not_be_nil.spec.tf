import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.not_be_nil", [

      subject(to.not_be_nil()),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("matches non-nil", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, {}, [], true, false, 0b00, NaN];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_match(x))
          )
        )
      ),

    ]);

}