import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.flatmap as flatmap;


library spec {
  spec:
    describe("data.flatmap", [

      it("empty_list", () -> 
        expect(flatmap([], (x) -> x), to.be([]))
      ),
    
      it("simple_list", () -> 
        expect(flatmap([1,2,3], (x)->data.repeat(x, x)), to.be([1,2,2,3,3,3]))
      ),
    
      it("simple_list_swallowing", () -> 
        expect(flatmap([1,2,3,4,5,6], (x) -> if x % 2 == 0 [x, x] else []), to.be([2,2,4,4,6,6]))
      ),
    
      it("list_various_sizes_of_out", () -> 
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
        )
      ),
    
      it("simple_list_with_index", () -> 
        expect(flatmap([1,2,3], (x, i)->data.repeat(i, x)), to.be([2,3,3]))
      ),
    
      it("empty_dict", () -> 
        expect(flatmap({}, (x) -> x), to.be([]))
      ),
    
      it("simple_dict", () -> 
        expect(flatmap({:a 1, :b 2}, (x) -> ["and", x]), to.be(["and", 1, "and", 2]))
      ),
    
      it("simple_dict_with_key", () -> 
        expect(flatmap({:a 1, :b 2}, (x, k) -> [k, x]), to.be([:a, 1, :b, 2]))
      ),
    
      it("of_default", () -> 
        expect(flatmap(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(flatmap(nil), to.be_nil())
      ),
    
      it("invalid_type", () -> 
        expect_error(
          () -> flatmap("foo", (x)->x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("invalid_f_type", () -> 
        expect_error(
          () -> flatmap([1,2,3], () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}