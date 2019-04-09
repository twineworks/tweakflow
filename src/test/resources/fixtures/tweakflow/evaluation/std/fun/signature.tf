import fun, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias fun.signature as signature;

library signature_spec {

  of_nil:
    expect(signature(nil), to.be_nil());

  of_unspecified_zero_args:
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
    );

  of_specified_zero_args:
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
    );

  of_specified_2_args_with_1_default:
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
    );

}