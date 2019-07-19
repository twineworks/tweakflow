import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_datetime", [

      subject(to.be_datetime()),

      it ("matches epoch datetime", (m) ->
        expect(m, to_match(time.epoch))
      ),

      it ("matches deep past datetime", (m) ->
        expect(m, to_match(-9999-01-01T))
      ),

      it ("matches deep future datetime", (m) ->
        expect(m, to_match(9999-01-01T))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-datetimes", (m) ->
        let {
          xs: [() -> true, "hello", 1.0, 1, 0b01, [], {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);


}