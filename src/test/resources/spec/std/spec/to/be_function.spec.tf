import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_function", [

      it ("matches function", () ->
        expect(to.be_function(), to_match((x) -> x))
      ),

      it ("does not match nil", () ->
        expect(to.be_function(), to_not_match(nil))
      ),

      it ("does not match non-functions", () ->
        let {
          matcher: to.be_function();
          xs: ["hello", 1, 1.0, time.epoch, 0b01, [], {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(matcher, to_not_match(x))
          )
        )
      ),

    ]);

}