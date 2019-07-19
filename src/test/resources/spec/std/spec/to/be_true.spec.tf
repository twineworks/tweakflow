import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_true", [

      subject(to.be_true()),

      it ("matches true", (m) ->
        expect(m, to_match(true))
      ),

      it ("does not match non-true", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, {}, [], false, 0b00, NaN];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}