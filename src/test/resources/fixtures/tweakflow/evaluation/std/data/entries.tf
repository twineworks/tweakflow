import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.entries as entries;


library entries_spec {

  empty_dict:
    expect(entries({}), to.be([]));

  simple_dict:
    expect(entries({:a 1, :b 2}), to.be_permutation_of([{:key "a", :value 1}, {:key "b", :value 2}]));

  of_nil:
    expect(entries(nil), to.be_nil());

}