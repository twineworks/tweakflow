import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.size as size;


library spec {
  spec:
    describe("size", [


  empty_list:
    expect(size([]), to.be(0));

  simple_list:
    expect(size([1,2,3]), to.be(3));

  empty_dict:
    expect(size({}), to.be(0));

  simple_dict:
    expect(size({:a 1, :b 2}), to.be(2));

  of_nil:
    expect(size(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> size("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
  ]);
}