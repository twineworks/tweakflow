import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be_binary", [

      subject(to.be_binary()),

      it ("matches empty binary", (m) ->
        expect(m, to_match(0b))
      ),

      it ("matches simple binary", (m) ->
        expect(m, to_match(0b010203))
      ),

      it ("matches long binary", (m) ->
        expect(m, to_match(0b0102030405060708090A0B0C0D0E0F))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-binaries", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, {}, [], true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}