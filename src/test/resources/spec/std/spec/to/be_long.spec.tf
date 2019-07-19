import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_long", [

      subject(to.be_long()),

      it ("matches long", (m) ->
        expect(m, to_match(2))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-longs", (m) ->
        let {
          xs: [() -> true, "hello", 1.0, time.epoch, 0b01, [], {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}