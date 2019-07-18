import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.signature as signature;

library spec {
  spec:
    describe("fun.signature", [

      it("of_nil", () ->
        expect(signature(nil), to.be_nil())
      ),

      it("of_unspecified_zero_args", () ->
        let {
          f: () -> true;
          s: signature(f);
        }
        expect(s,
          to.be(
            {
              :return_type "any",
              :parameters []
            }
          )
        )
      ),

      it("of_specified_zero_args", () ->
        let {
          f: () -> boolean true;
          s: signature(f);
        }
        expect(s,
          to.be(
            {
              :return_type "boolean",
              :parameters []
            }
          )
        )
      ),

      it("of_specified_2_args_with_1_default", () ->
        let {
          f: (list xs, long s=1) -> list [];
          s: signature(f);
        }
        expect(s,
          to.be(
            {
              :return_type "list",
              :parameters [{
                :name "xs",
                :index 0,
                :default_value nil,
                :declared_type "list"
              }, {
                :name "s",
                :index 1,
                :default_value 1,
                :declared_type "long"
              }]
            }
          )
        )
      ),

  ]);
}