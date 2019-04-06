import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.zip as zip;


library zip_spec {

  empty_list:
    expect(zip([], []), to.be([]));

  same_length:
    expect(zip([1,2,3], [:a, :b, :c]), to.be([[1, :a], [2, :b], [3, :c]]));

  xs_longer:
    expect(zip([1,2,3], [:a, :b]), to.be([[1, :a], [2, :b], [3, nil]]));

  ys_longer:
    expect(zip([1,2,3], [:a, :b, :c, :d]), to.be([[1, :a], [2, :b], [3, :c]]));

  of_nil_xs:
    expect(zip(nil, []), to.be_nil());

  of_nil_ys:
    expect(zip([], nil), to.be_nil());

}