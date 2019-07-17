import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.zip_dict as zip_dict;


library spec {
  spec:
    describe("zip_dict", [


  empty_list:
    expect(zip_dict([], []), to.be({}));

  same_length:
    expect(zip_dict([:a, :b, :c], [1, 2, 3]), to.be({:a 1, :b 2, :c 3}));

  same_length_dup_keys:
    expect(zip_dict([:a, :b, :c, :a], [1, 2, 3, 4]), to.be({:a 4, :b 2, :c 3}));

  keys_longer:
    expect(zip_dict([:a, :b, :c, :d], [1, 2, 3]), to.be({:a 1, :b 2, :c 3, :d nil}));

  keys_longer_dup_keys:
    expect(zip_dict([:a, :b, :b, :c], [1, 2, 3]), to.be({:a 1, :b 3, :c nil}));

  values_longer:
    expect(zip_dict([:a, :b, :c], [1, 2, 3, 4]), to.be({:a 1, :b 2, :c 3}));

  values_longer_dup_keys:
    expect(zip_dict([:a, :b, :a], [1, 2, 3, 4]), to.be({:a 3, :b 2}));

  of_nil_keys:
    expect(zip_dict(nil, []), to.be_nil());

  of_nil_values:
    expect(zip_dict([], nil), to.be_nil());

  invalid_key_type:
    expect_error(
      () -> zip_dict([[]], [0]),
      to.have_code("CAST_ERROR")
    );

  ]);
}