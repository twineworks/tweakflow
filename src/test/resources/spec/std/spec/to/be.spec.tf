import strings, data, time, regex, fun from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

import to_match, to_not_match from "./helpers.tf";

library spec {

  spec:

    describe("to.be", [

      describe("with number 2.0", [

        subject(to.be(2.0)),

        it ("matches identical value (===)", (m) ->
          expect(m, to_match(2.0))
        ),

        it ("does not match merely equal value (==)", (m) ->
          expect(m, to_not_match(2))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),

        it ("does not match non-equal values", (m) ->
          let {
            xs: [() -> true, "hello", time.epoch, 1.0, 1, [], true, false];
          }
          assert(
            data.all?(xs, (x) ->
               expect(m, to_not_match(x))
            )
          )
        ),
      ]),

      describe("with number 2", [

        subject(to.be(2)),

        it ("matches identical value (===)", (m) ->
          expect(m, to_match(2))
        ),

        it ("does not match merely equal value (==)", (m) ->
          expect(m, to_not_match(2.0))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),

        it ("does not match non-equal values", (m) ->
          let {
            xs: [() -> true, "hello", time.epoch, 1.0, 1, [], true, false];
          }
          assert(
            data.all?(xs, (x) ->
               expect(m, to_not_match(x))
            )
          )
        ),
      ]),

      describe("with list [1, 2.0]", [

        subject(to.be([1, 2.0])),

        it ("matches identical value (===)", (m) ->
          expect(m, to_match([1, 2.0]))
        ),

        it ("matches merely equal value (==)", (m) ->
          expect(m, to_not_match([1.0, 2.0])) &&
          expect(m, to_not_match([1, 2]))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),

        it ("does not match non-equal values", (m) ->
          let {
            xs: [() -> true, "hello", time.epoch, 1.0, 1, [], true, false];
          }
          assert(
            data.all?(xs, (x) ->
               expect(m, to_not_match(x))
            )
          )
        ),
      ]),

      describe("with nil", [

        subject(to.be(nil)),

        it ("matches nil", (m) ->
          expect(m, to_match(nil))
        ),

        it ("does not match false", (m) ->
          expect(m, to_not_match(false))
        ),

        it ("does not match NaN", (m) ->
          expect(m, to_not_match(NaN))
        ),

        it ("does not match non-equal values", (m) ->
          let {
            xs: [() -> true, "hello", time.epoch, 1.0, 1, [], {}];
          }
          assert(
            data.all?(xs, (x) ->
               expect(m, to_not_match(x))
            )
          )
        ),
      ]),

      describe("with NaN", [

        subject(to.be(NaN)),

        it ("does not match NaN (NaN is non-comparable)", (m) ->
          expect(m, to_not_match(NaN))
        ),

        it ("does not match false", (m) ->
          expect(m, to_not_match(false))
        ),

        it ("does not match nil", (m) ->
          expect(m, to_not_match(nil))
        ),

        it ("does not match non-equal values", (m) ->
          let {
            xs: [() -> true, "hello", time.epoch, 1.0, 1, [], {}];
          }
          assert(
            data.all?(xs, (x) ->
               expect(m, to_not_match(x))
            )
          )
        ),
      ]),

    ]);
  

}