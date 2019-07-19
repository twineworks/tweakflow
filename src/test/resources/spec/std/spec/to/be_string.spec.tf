import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_string", [

      subject(to.be_string()),

      it ("matches string", (m) ->
        expect(m, to_match("hello"))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-strings", (m) ->
        let {
          xs: [() -> true, 1, 1.0, time.epoch, 0b01, [], {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}