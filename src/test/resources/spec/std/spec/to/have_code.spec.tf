import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.have_code", [

      subject(to.have_code("FOO")),

      it ("matches dict with expected code", (m) ->
        expect(m, to_match({:code "FOO"}))
      ),

      it ("does not match dict with different code", (m) ->
        expect(m, to_not_match({:code "BAR"}))
      ),

      it ("does not match dict without code", (m) ->
        expect(m, to_not_match({:a ""}))
      ),

      it ("does not match nil", (m) ->
        expect(m, to_not_match(nil))
      ),

      it ("does not match non-dicts", (m) ->
        let {
          xs: [() -> true, "hello", time.epoch, 1.0, 1, [], true, false];
        }
        assert(
          data.all?(xs, (x) ->
             expect(m, to_not_match(x))
          )
        )
      ),

    ]);

}