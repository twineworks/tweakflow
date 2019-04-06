import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.flatmap as flatmap;


library flatmap_spec {

  empty_list:
    expect(flatmap([], (x) -> x), to.be([]));

  simple_list:
    expect(flatmap([1,2,3], (x)->data.repeat(x, x)), to.be([1,2,2,3,3,3]));

  simple_list_swallowing:
    expect(flatmap([1,2,3,4,5,6], (x) -> if x % 2 == 0 [x, x] else []), to.be([2,2,4,4,6,6]));

  list_various_sizes_of_out:
    expect(
      flatmap(
        ["a", "b", "e"],
        (c) ->
          match c
            "a" -> ["a", "c", "e"], # a -> ace
            "b" -> [],              # b -> _
            "e" -> "man"            # e -> man
      ),
      to.be(
        ["a", "c", "e", "man"]
      )
    );

  simple_list_with_index:
    expect(flatmap([1,2,3], (x, i)->data.repeat(i, x)), to.be([2,3,3]));

  empty_dict:
    expect(flatmap({}, (x) -> x), to.be([]));

  simple_dict:
    expect(flatmap({:a 1, :b 2}, (x) -> ["and", x]), to.be(["and", 1, "and", 2]));

  simple_dict_with_key:
    expect(flatmap({:a 1, :b 2}, (x, k) -> [k, x]), to.be([:a, 1, :b, 2]));

  of_default:
    expect(flatmap(), to.be_nil());

  of_nil:
    expect(flatmap(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> flatmap("foo", (x)->x),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  invalid_f_type:
    expect_error(
      () -> flatmap([1,2,3], () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}