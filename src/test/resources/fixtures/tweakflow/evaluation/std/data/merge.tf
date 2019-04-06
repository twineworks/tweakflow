import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.merge as merge;

library merge_spec {

  empty:
    expect(merge([]), to.be({}));

  single:
    expect(merge([{:a 1, :b 2}]), to.be({:a 1, :b 2}));

  pair:
    expect(merge([{:a 1, :b 2}, {:c 3, :d 4}]), to.be({:a 1, :b 2, :c 3, :d 4}));

  pair_replace:
    expect(merge([{:a 1, :b 2}, {:c 3, :b "r", :d 4}]), to.be({:a 1, :b "r", :c 3, :d 4}));

  triple:
    expect(merge([{:a 1, :b 2}, {:c 3, :d 4}, {:e 5, :f 6}]), to.be({:a 1, :b 2, :c 3, :d 4, :e 5, :f 6}));

  triple_replace:
    expect(merge([{:a 1, :b 2}, {:c 3, :b "r", :d 4}, {:e 5, :b "win", :f 6}]), to.be({:a 1, :b "win", :c 3, :d 4, :e 5, :f 6}));

  of_nil:
    expect(merge(nil), to.be_nil());

  of_nil_element:
    expect(merge([{:a 1, :b 2}, nil]), to.be_nil());

  invalid_element_type:
    expect_error(
      () -> merge(["foo"]),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}