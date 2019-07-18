import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.zip_dict as zip_dict;


library spec {
  spec:
    describe("data.zip_dict", [
    
      it("empty_list", () -> 
        expect(zip_dict([], []), to.be({}))
      ),
    
      it("same_length", () -> 
        expect(zip_dict([:a, :b, :c], [1, 2, 3]), to.be({:a 1, :b 2, :c 3}))
      ),
    
      it("same_length_dup_keys", () -> 
        expect(zip_dict([:a, :b, :c, :a], [1, 2, 3, 4]), to.be({:a 4, :b 2, :c 3}))
      ),
    
      it("keys_longer", () -> 
        expect(zip_dict([:a, :b, :c, :d], [1, 2, 3]), to.be({:a 1, :b 2, :c 3, :d nil}))
      ),
    
      it("keys_longer_dup_keys", () -> 
        expect(zip_dict([:a, :b, :b, :c], [1, 2, 3]), to.be({:a 1, :b 3, :c nil}))
      ),
    
      it("values_longer", () -> 
        expect(zip_dict([:a, :b, :c], [1, 2, 3, 4]), to.be({:a 1, :b 2, :c 3}))
      ),
    
      it("values_longer_dup_keys", () -> 
        expect(zip_dict([:a, :b, :a], [1, 2, 3, 4]), to.be({:a 3, :b 2}))
      ),
    
      it("of_nil_keys", () -> 
        expect(zip_dict(nil, []), to.be_nil())
      ),
    
      it("of_nil_values", () -> 
        expect(zip_dict([], nil), to.be_nil())
      ),
    
      it("invalid_key_type", () -> 
        expect_error(
          () -> zip_dict([[]], [0]),
          to.have_code("CAST_ERROR")
        )
      ),

  ]);
}