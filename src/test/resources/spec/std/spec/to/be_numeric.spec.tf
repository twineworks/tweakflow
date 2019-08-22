import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_numeric", [

      subject(to.be_numeric()),

      it ("matches long", (m) ->
        expect(m, to_match(2))
      ),

      it ("matches decimal", (m) ->
        expect(m, to_match(2d))
      ),

      it ("matches finite double", (m) ->
        expect(m, to_match(2.0))
      ),

      it ("matches infinities", (m) ->
        expect(m, to_match(Infinity)) &&
        expect(m, to_match(-Infinity))
      ),

      it ("matches NaN", (m) ->
        expect(m, to_match(NaN))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-numeric", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 0b01, [], {}, true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}