import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.keys as keys;


library keys_spec {

  empty_list:
    expect(keys([]), to.be([]));

  simple_list:
    expect(keys([1,nil,3]), to.be([0, 1, 2]));

  empty_dict:
    expect(keys({}), to.be([]));

  simple_dict:
    expect(keys({:a 1, :b 2}), to.be([:a, :b]));

  of_nil:
    expect(keys(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> keys("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}