import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.has? as has?;


library has?_spec {

  missing_list:
    expect(has?([0, nil, 3], 3), to.be_false());

  simple_list:
    expect(has?([1,nil,3], 0), to.be_true());

  nil_entry_list:
    expect(has?([1,nil,3], 1), to.be_true());

  missing_dict:
    expect(has?({:a 1, :b 2}, :c), to.be_false());

  simple_dict:
    expect(has?({:a 1, :b 2}, :b), to.be_true());

  nil_entry_dict:
    expect(has?({:a 1, :b nil}, :b), to.be_true());

  of_nil:
    expect(has?(nil, :a), to.be_nil());

  invalid_xs_type:
    expect_error(
      () -> has?("foo", 0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  invalid_key_type_list:
    expect_error(
      () -> has?([], "foo"),
      to.have_code("CAST_ERROR")
    );

  invalid_key_type_dict:
    expect_error(
      () -> has?({}, []),
      to.have_code("CAST_ERROR")
    );

  }