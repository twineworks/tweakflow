import core, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

library spec {

  clock: (function callback) -> {
    :type 'effect',
    :effect {
      :type 'clock',
      :callback callback default core.id
    }
  };

  rand: (function callback) -> {
    :type 'effect',
    :effect {
      :type 'rand',
      :callback callback default core.id
    }
  };

  print: (string text) -> {
    :type 'effect',
    :effect {
      :type 'print',
      :text text
    }
  };

  spec:
    describe("effect", [

      describe("evaluates while building test-suite", [

        # generate a random amount of passing test cases
        rand((nr) ->
          let {
            specNrs: data.range(1, (nr*10+1) as long);
            size: data.size(specNrs);
          }
          describe("builds a random number of tests (this time: #{size})",
            data.map(specNrs, (nr) ->
              it("generates spec #"..nr, () ->
                assert(true)
              )
            )
          )
        )
      ]),

      describe("evaluates in before, after, and subject blocks", [
        before(print("***** running before block")),
        subject(effect: print("***** running subject block")),

        describe("checks the clock", [
          subject(effect: clock()),
          it("is a datetime", (s) ->
            expect(s, to.be_datetime())
          )
        ]),

        after(print("***** running after block")),
      ])

    ]);
}