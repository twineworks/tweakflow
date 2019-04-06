import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.mapcat as mapcat;


library mapcat_spec {

  empty_list:
    expect(mapcat([], (x) -> x), to.be([]));

  simple_list:
    expect(mapcat([1,2,3], (x)->data.repeat(x, x)), to.be([1,2,2,3,3,3]));

  simple_list_swallowing:
    expect(mapcat([1,2,3,4,5,6], (x) -> if x % 2 == 0 [x, x] else nil), to.be([2,2,4,4,6,6]));

  list_various_sizes_of_out:
    expect(
      mapcat(
        ["a", "b", "e"],
        (c) ->
          match c
            "a" -> ["a", "c", "e"], # a -> ace
            "b" -> [],              # b -> _
            "e" -> nil              # e -> _
      ),
      to.be(
        ["a", "c", "e"]
      )
    );

  simple_list_with_index:
    expect(mapcat([1,2,3], (x, i)->data.repeat(i, x)), to.be([2,3,3]));

  empty_dict:
    expect(mapcat({}, (x) -> x), to.be([]));

  simple_dict:
    expect(mapcat({:a 1, :b 2}, (x) -> ["and", x]), to.be(["and", 1, "and", 2]));

  simple_dict_with_key:
    expect(mapcat({:a 1, :b 2}, (x, k) -> [k, x]), to.be([:a, 1, :b, 2]));

  of_default:
    expect(mapcat(), to.be_nil());

  of_nil:
    expect(mapcat(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> mapcat("foo", (x)->x),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  invalid_f_type:
    expect_error(
      () -> mapcat([1,2,3], () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}